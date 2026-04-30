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
import dev.tauri.jsg.common.advancements.JSGAdvancements;
import dev.tauri.jsg.common.command.JSGCommands;
import dev.tauri.jsg.common.item.linkable.dialer.modes.UniverseDialerModes;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.recipes.PageAndUniverseDialerRecipe;
import dev.tauri.jsg.common.recipes.StargateOrlinBaseBlockRecipe;
import dev.tauri.jsg.common.recipes.UniverseDialerCloneRecipe;
import dev.tauri.jsg.common.recipes.notebook.NotebookCloneRecipe;
import dev.tauri.jsg.common.recipes.notebook.NotebookCreationRecipe;
import dev.tauri.jsg.common.recipes.notebook.NotebookMergePageRecipe;
import dev.tauri.jsg.common.recipes.notebook.NotebookMergeRecipe;
import dev.tauri.jsg.common.registry.JSGRegistriesInit;
import dev.tauri.jsg.common.stargate.StargateTypesLoader;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.common.stargate.network.StargateReservedAddresses;
import dev.tauri.jsg.common.util.updater.GetUpdate;
import dev.tauri.jsg.common.worldgen.poolinject.injectors.StargateTemplatePoolsAdditions;
import dev.tauri.jsg.common.worldgen.poolinject.injectors.VillageTemplatePoolsAdditions;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.LoggerWrapper;
import dev.tauri.jsg.core.common.integration.Integrations;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.integration.cctweaked.CCDevices;
import dev.tauri.jsg.integration.create.PonderScenes;
import dev.tauri.jsg.integration.oc2.OCDevices;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.LoggerFactory;

import java.io.File;

// TODO(Mine): Move all client stuff into client package and server stuff into common

@Mod(JSG.MOD_ID)
public class JSG {
    public static final String MOD_ID = "jsg";
    public static final String MOD_NAME = "Just Stargate Mod";
    public static LoggerWrapper logger;

    public static String MOD_VERSION = "";
    public static String MOD_VERSION_ONLY = "";
    public static final String MC_VERSION = "1.20.1";
    public static File clientModPath;

    public static long memoryTotal = 0;

    public static final String[] WELCOME_MESS = {
            "=======================================",
            "|     $$$$$\\  $$$$$$\\   $$$$$$\\",
            "|     \\__$$ |$$  __$$\\ $$  __$$\\",
            "|        $$ |$$ /  \\__|$$ /  \\__|",
            "|        $$ |\\$$$$$$\\  $$ |$$$$\\",
            "|  $$\\   $$ | \\____$$\\ $$ |\\_$$ |",
            "|  $$ |  $$ |$$\\   $$ |$$ |  $$ |",
            "|  \\$$$$$$  |\\$$$$$$  |\\$$$$$$  |",
            "|   \\______/  \\______/  \\______/",
            "",
            " Authors: Tau'ri Dev team",
            " Wiki: https://justsgmod.eu/wiki",
            " Version: {version}",
            "======================================="
    };

    public static void displayWelcomeMessage() {
        for (String s : WELCOME_MESS) {
            logger.info(s.replaceAll("\\{version}", JSG.MOD_VERSION));
        }
    }

    @SuppressWarnings("unused")
    public static Component getInProgress() {
        return Component.literal(ChatFormatting.AQUA + "Work In Progress Item!");
    }

    public static ResourceLocation rl(String path) {
        return JSGMapping.rl(JSG.MOD_ID, path);
    }

    public JSG() {
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

        ModList.get().getModContainerById(MOD_ID).ifPresentOrElse(container -> {
            MOD_VERSION_ONLY = container.getModInfo().getVersion().getQualifier();
            MOD_VERSION = MC_VERSION + "-" + MOD_VERSION_ONLY;
            JSGApi.MOD_VERSION = MOD_VERSION;
            clientModPath = container.getModInfo().getOwningFile().getFile().getFilePath().toFile();
        }, () -> {
        });
        JSG.memoryTotal = Runtime.getRuntime().maxMemory();

        JSG.logger.info("Started loading JSG mod in {}", JSG.clientModPath.getAbsolutePath());
        JSG.logger.info("Mods directory: {}", JSGCore.modConfigDir.getAbsolutePath());
        JSG.logger.info("Loading JSG version {}", JSG.MOD_VERSION);

        JSGConfig.load();
        JSGConfig.register();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Constants.init();
        JSGRegistries.init();
        JSGRegistries.register(eventBus);
        JSGRegistriesInit.init();

        JSGPacketHandler.init();
        UniverseDialerModes.init();

        VillageTemplatePoolsAdditions.register();
        StargateTemplatePoolsAdditions.register();


        JSGRegistriesInit.register(eventBus);

        eventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        Runtime.getRuntime().addShutdownHook(new Thread(JSG::shutDown));

        Integrations.CCT.addOnLoad(CCDevices::load);
        Integrations.OC2.addOnLoad(OCDevices::load);
        Integrations.CREATE.addOnLoad(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PonderScenes::new));

        displayWelcomeMessage();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        //ProgressJSON.INSTANCE.load(JSGCore.modConfigDir);
        JSGAdvancements.register();
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
        var recipeManager = event.getServer().getRecipeManager();
        var recipes = recipeManager.getRecipes();
        recipes.add(new NotebookCloneRecipe());
        recipes.add(new NotebookCreationRecipe());
        recipes.add(new NotebookMergePageRecipe());
        recipes.add(new NotebookMergeRecipe());
        recipes.add(new UniverseDialerCloneRecipe());
        recipes.add(new PageAndUniverseDialerRecipe());
        recipes.add(new StargateOrlinBaseBlockRecipe());
        recipeManager.replaceRecipes(recipes);

        JSG.logger.info("Server started!");
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        JSGCommands.registerCommands(event);
    }

    @SubscribeEvent
    public void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            GetUpdate.checkForUpdateVoid();

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
