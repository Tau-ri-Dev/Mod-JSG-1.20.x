package dev.tauri.jsg.client.listener;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.effect.StargateWormholeEffect;
import dev.tauri.jsg.common.entity.client.JSGEntityModelLayer;
import dev.tauri.jsg.common.entity.client.MastadgeModel;
import dev.tauri.jsg.common.item.tooltips.ClientStargateInventoryTooltip;
import dev.tauri.jsg.common.item.tooltips.ServerStargateInventoryTooltip;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static dev.tauri.jsg.client.listener.InputHandlerClient.KEY_BINDINGS;

@Mod.EventBusSubscriber(modid = JSG.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("wormhole_overlay", StargateWormholeEffect::render);
    }
}
