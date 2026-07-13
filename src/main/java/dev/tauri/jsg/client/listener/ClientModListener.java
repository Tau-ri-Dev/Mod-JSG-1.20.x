package dev.tauri.jsg.client.listener;

import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.client.item.tooltip.ClientStargateInventoryTooltip;
import dev.tauri.jsg.client.renderer.StargateWormholeEffect;
import dev.tauri.jsg.common.entity.client.JSGEntityModelLayer;
import dev.tauri.jsg.common.entity.client.MastadgeModel;
import dev.tauri.jsg.common.item.tooltips.ServerStargateInventoryTooltip;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import static dev.tauri.jsg.client.listener.InputHandlerClient.KEY_BINDINGS;

@EventBusSubscriber(modid = JSG.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModListener {

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        for (var m : KEY_BINDINGS)
            event.register(m);
    }

    @SubscribeEvent
    public static void registerItemStackRenderableTooltips(RegisterClientTooltipComponentFactoriesEvent e) {
        e.register(ServerStargateInventoryTooltip.class, ClientStargateInventoryTooltip::new);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(JSGEntityModelLayer.MASTADGE_LAYER, MastadgeModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAboveAll(dev.tauri.jsg.core.mapping.JSGMapping.rl(dev.tauri.jsg.JSG.MOD_ID, "wormhole_overlay"), StargateWormholeEffect::render);
    }
}
