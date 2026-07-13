package dev.tauri.jsg;

import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.registry.JSGRegistries;
import dev.tauri.jsg.client.listener.EventTickClient;
import dev.tauri.jsg.client.screen.gui.admincontroller.AdminControllerTabsRegistry;
import dev.tauri.jsg.client.screen.gui.admincontroller.tabs.DiagnosticsTab;
import dev.tauri.jsg.client.screen.gui.admincontroller.tabs.DialingTab;
import dev.tauri.jsg.client.screen.gui.admincontroller.tabs.NetworkTab;
import dev.tauri.jsg.client.screen.gui.mainmenu.GuiCustomMainMenu;
import dev.tauri.jsg.common.command.JSGCommands;
import dev.tauri.jsg.common.config.data.ProgressJSON;
import dev.tauri.jsg.common.injectors.JSGLootTableInjectors;
import dev.tauri.jsg.common.injectors.JSGTemplatePoolInjectors;
import dev.tauri.jsg.common.integration.cctweaked.CCDevices;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.registry.JSGRegistriesInit;
import dev.tauri.jsg.common.stargate.StargateTypesLoader;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.common.stargate.network.StargateReservedAddresses;
import dev.tauri.jsg.core.JSGAddon;
import dev.tauri.jsg.core.JSGAddons;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.LoggerWrapper;
import dev.tauri.jsg.core.common.integration.Integrations;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.*;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jspecify.annotations.NonNull;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Mod(JSG.MOD_ID)
public class JSG implements JSGAddon {
    public static final String MOD_ID = "jsg";
    public static final String MOD_NAME = "Just Stargate Mod";
    public static LoggerWrapper logger;

    public static String MOD_VERSION = "";
    public static String MOD_VERSION_ONLY = "";
    public static final String MC_VERSION = "1.21.1";

    public static long memoryTotal = 0;

    public static ResourceLocation rl(String path) {
        return JSGMapping.rl(JSG.MOD_ID, path);
    }

    public JSG(IEventBus eventBus) {
        logger = new LoggerWrapper("[" + MOD_ID + "] ", LoggerFactory.getLogger(MOD_NAME));

        // API INIT
        JSGApi.init();
        JSGApi.logger = logger;
        JSGApi.jsgModMainClass = this.getClass();
        JSGApi.SGNGetter = (level) -> level.getDataStorage().computeIfAbsent(
                new net.minecraft.world.level.saveddata.SavedData.Factory<>(StargateNetwork::new, (tag, registries) -> {
                    var sgn = new StargateNetwork();
                    sgn.load(tag);
                    return sgn;
                }, null), StargateNetwork.DATA_NAME);
        StargateTypesLoader.load();
        // ----------

        Util.make(JSGAddons.getInfo(this), info -> {
            MOD_VERSION_ONLY = info.get(JSGAddons.AddonInfo.VERSION);
            MOD_VERSION = MC_VERSION + "-" + MOD_VERSION_ONLY;
            JSGApi.MOD_VERSION = MOD_VERSION;
        });
        JSG.memoryTotal = Runtime.getRuntime().maxMemory();

        JSG.logger.info("Started loading JSG mod");
        JSG.logger.info("Mods directory: {}", JSGCore.modConfigDir.getAbsolutePath());
        JSG.logger.info("Loading JSG version {}", JSG.MOD_VERSION);

        if (FMLEnvironment.dist.isClient()) {
            try {
                var clazz = Class.forName("dev.tauri.jsg.JSGServer");
                ModList.get().getModContainerById(getId()).ifPresentOrElse(jsgMod -> {
                    ModLoader.addLoadingIssue(net.neoforged.fml.ModLoadingIssue.warning(
                            "Trying to run JSG server version on a client, this is not going to end well... (class " + clazz.getCanonicalName() + " is present on client)").withAffectedMod(jsgMod.getModInfo()));
                }, () -> {
                    throw new RuntimeException("Trying to run JSG server version on a client, this is not going to end well... (class " + clazz.getCanonicalName() + " is present on client)");
                });
            } catch (ClassNotFoundException ignored) {
            }
        }

        JSGConfig.load();
        JSGConfig.register();

        Constants.init();
        JSGRegistries.init();
        JSGRegistries.register(eventBus);
        JSGRegistriesInit.init();

        JSGPacketHandler.init();

        JSGTemplatePoolInjectors.register();
        JSGLootTableInjectors.register();

        dev.tauri.jsg.common.datafixer.JSGDataFixers.registerAliases();

        JSGRegistriesInit.register(eventBus);

        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::loadCompleteServer);
        eventBus.addListener(this::registerSpawnPlacements);
        NeoForge.EVENT_BUS.register(this);
        Runtime.getRuntime().addShutdownHook(new Thread(JSG::shutDown));

        Integrations.CCT.addOnLoad(CCDevices::load);
        // OC2 has no 1.21.x build; its devices load hook returns when the integration is re-enabled (enable_oc2)

        JSGAddons.registerAddon(this);
    }

    private void registerSpawnPlacements(net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent event) {
        event.register(dev.tauri.jsg.common.registry.JSGEntities.MASTADGE.get(),
                net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND,
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                net.minecraft.world.entity.animal.Animal::checkAnimalSpawnRules,
                net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // custom registries are frozen now; Forge's onBake callback used to do this
        dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerMode.calculateModesSequence();

        ProgressJSON.INSTANCE.load(JSGCore.modConfigDir);
        try {
            ProgressJSON.INSTANCE.reload(null);
        } catch (Exception e) {
            JSG.logger.error("Error while reloading progressJSON:", e);
        }
    }

    public void loadCompleteServer(FMLLoadCompleteEvent event) {
        JSG.logger.info("Just Stargate Mod loading completed!");
    }

    public static void shutDown() {
        JSG.logger.info("Good bye! Thank you for using Just Stargate Mod :)");
        if (FMLEnvironment.dist.isClient()) {
            if (JSGConfig.C_GENERAL.builtSpec == null || !JSGConfig.C_GENERAL.builtSpec.isLoaded()) return;
            if (!FMLEnvironment.production) return;
            JSGConfig.General.mainMenuMusicVolume.set(GuiCustomMainMenu.musicVolume.doubleValue());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        new StargateNetwork().register(event.getServer().overworld().getDataStorage());
        StargateReservedAddresses.register();
    }

    @SubscribeEvent
    public void serverStarted(ServerStartedEvent event) {
        JSG.logger.info("Server started!");
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        JSGCommands.registerCommands(event);
    }

    @Override
    @NonNull
    public String getId() {
        return MOD_ID;
    }

    @Override
    public String @NonNull [] getWelcomeLogo() {
        return new String[]{
                "░░░░░██╗░██████╗░██████╗░",
                "░░░░░██║██╔════╝██╔════╝░",
                "░░░░░██║╚█████╗░██║░░██╗░",
                "██╗░░██║░╚═══██╗██║░░╚██╗",
                "╚█████╔╝██████╔╝╚██████╔╝",
                "░╚════╝░╚═════╝░░╚═════╝░"
        };
    }

    @Override
    @NonNull
    public Optional<LoggerWrapper> getLoggerWrapper() {
        return Optional.of(logger);
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            AdminControllerTabsRegistry.addTab(DialingTab::new);
            AdminControllerTabsRegistry.addTab(DiagnosticsTab::new);
            AdminControllerTabsRegistry.addTab(NetworkTab::new);
        }
    }

    /**
     * Contains las pos of player (client side) - helps to debug sound in main menu.
     * <p>
     * Updated in {@link EventTickClient}
     */
    public static BlockPos lastPlayerPosInWorld = new BlockPos(0, 0, 0);
}
