package dev.tauri.jsg;

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
import dev.tauri.jsg.common.integration.oc.OCDevices;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
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
    public static final String MC_VERSION = "1.20.1";

    public static long memoryTotal = 0;

    public static ResourceLocation rl(String path) {
        return JSGMapping.rl(JSG.MOD_ID, path);
    }

    public JSG(FMLJavaModLoadingContext ctx) {
        logger = new LoggerWrapper("[" + MOD_ID + "] ", LoggerFactory.getLogger(MOD_NAME));

        // API INIT
        JSGApi.init();
        JSGApi.logger = logger;
        JSGApi.jsgModMainClass = this.getClass();
        JSGApi.SGNGetter = (level) -> level.getDataStorage().computeIfAbsent((tag) -> {
            var sgn = new StargateNetwork();
            sgn.load(tag);
            return sgn;
        }, StargateNetwork::new, StargateNetwork.DATA_NAME);
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
                    ModLoader.get().addWarning(new ModLoadingWarning(jsgMod.getModInfo(), ModLoadingStage.CONSTRUCT, "Trying to run JSG server version on a client, this is not going to end well...", "Class " + clazz.getCanonicalName() + " is present on client"));
                }, () -> {
                    throw new RuntimeException("Trying to run JSG server version on a client, this is not going to end well... (class " + clazz.getCanonicalName() + " is present on client)");
                });
            } catch (ClassNotFoundException ignored) {
            }
        }

        JSGConfig.load();
        JSGConfig.register();

        IEventBus eventBus = ctx.getModEventBus();

        Constants.init();
        JSGRegistries.init();
        JSGRegistries.register(eventBus);
        JSGRegistriesInit.init();

        JSGPacketHandler.init();

        JSGTemplatePoolInjectors.register();
        JSGLootTableInjectors.register();

        JSGRegistriesInit.register(eventBus);

        eventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        Runtime.getRuntime().addShutdownHook(new Thread(JSG::shutDown));

        Integrations.CCT.addOnLoad(CCDevices::load);
        Integrations.OCCE.addOnLoad(OCDevices::load);

        JSGAddons.registerAddon(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ProgressJSON.INSTANCE.load(JSGCore.modConfigDir);
        try {
            ProgressJSON.INSTANCE.reload(null);
        } catch (Exception e) {
            JSG.logger.error("Error while reloading progressJSON:", e);
        }
    }

    @SubscribeEvent
    public void loadCompleteServer(FMLLoadCompleteEvent event) {
        JSG.logger.info("Just Stargate Mod loading completed!");
    }

    public static void shutDown() {
        JSG.logger.info("Good bye! Thank you for using Just Stargate Mod :)");
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (JSGConfig.C_GENERAL.builtSpec == null || !JSGConfig.C_GENERAL.builtSpec.isLoaded()) return;
            if (!FMLEnvironment.production) return;
            JSGConfig.General.mainMenuMusicVolume.set(GuiCustomMainMenu.musicVolume.doubleValue());
        });
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

    @SubscribeEvent
    public void addCreative(BuildCreativeModeTabContentsEvent event) {
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

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
