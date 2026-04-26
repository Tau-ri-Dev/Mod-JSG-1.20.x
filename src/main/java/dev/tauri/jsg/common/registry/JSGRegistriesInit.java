package dev.tauri.jsg.common.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.registry.*;
import dev.tauri.jsg.client.renderer.entity.MastadgeRenderer;
import dev.tauri.jsg.common.stargate.StargateTypesLoader;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import dev.tauri.jsg.core.common.registry.helper.RegistryHelper;
import net.minecraftforge.eventbus.api.IEventBus;

public class JSGRegistriesInit {
    public static void init() {
        CoreTabs.registerTransportationTab(() -> JSGBlocks.STARGATE_MILKYWAY_BASE_BLOCK);

        JSGSymbolTypes.init();
        JSGScheduledTaskTypes.init();
        JSGSymbolUsages.init();
        JSGStateTypes.init();
        JSGNotebookPageTypes.init();
        JSGSoundEvents.init();
        JSGPositionedSounds.init();
        JSGRaycastersRegistry.init();
        JSGBlocks.init();
        JSGItems.init();
        JSGTabs.init();
        JSGFeatures.init();
        JSGBlockEntities.init();
        JSGMenuTypes.init();
        JSGVillagers.init();
    }

    public static void register(IEventBus bus) {
        JSGApi.REGISTRY_HELPER.entityRendererRegister(() -> {
            RegistryHelper.registerEntityRenderer(JSGEntities.MASTADGE.get(), MastadgeRenderer::new);
        });
        JSGApi.REGISTRY_HELPER.register(bus);
        StargateTypesLoader.register(bus);
    }
}
