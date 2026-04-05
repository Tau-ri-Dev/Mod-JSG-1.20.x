package dev.tauri.jsg.item.stargate.dialhomedevice;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.common.item.JSGModelOBJInGUIRenderer;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.loader.ElementEnum;
import dev.tauri.jsg.registry.JSGBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class DHDMilkyWayItem extends DHDAbstractItem {
    public static final ResourceLocation SYMBOLS_TEX = JSGMapping.rl(JSG.MOD_ID, "textures/tesr/milkyway/dhd/dhd_button_light_0.jpg");
    public static final ResourceLocation BRB_TEX = JSGMapping.rl(JSG.MOD_ID, "textures/tesr/milkyway/dhd/dhd_brb_0.jpg");

    public DHDMilkyWayItem(Block block) {
        super(block);
    }

    @Override
    public JSGModelOBJInGUIRenderer.RenderPartInterface getRenderPartInterface() {
        return (itemStack, itemDisplayContext, stack, bufferSource, light, overlay) -> {
            if (Block.byItem(itemStack.getItem()) == JSGBlocks.DHD_MILKYWAY.get()) {
                // render DHD
                ElementEnum.MILKYWAY_DHD_BASE.bindTexture().render(stack, bufferSource, light);
                ElementEnum.MILKYWAY_DHD_BUTTON_CONSOLE.render(stack, bufferSource, light);
                ElementEnum.MILKYWAY_DHD_UPGRADE_COVER.render(stack, bufferSource, light);

                // render symbols
                for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
                    stack.pushPose();

                    // render symbol base
                    ElementEnum.MILKYWAY_DHD_BASE.bindTexture(CoreBiomeOverlays.NORMAL);
                    symbol.getModel(StargateTypes.MILKYWAY.get(), null, StargatePointOfOriginsDefaults.VARIANT_DHD).render(stack, bufferSource, light);

                    // render symbol light emissive
                    JSGApi.JSG_LOADERS_HOLDER.texture().getTexture((symbol.brb() ? BRB_TEX : SYMBOLS_TEX)).bindTexture();
                    symbol.getModel(StargateTypes.MILKYWAY.get(), null, StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT).render(stack, bufferSource, light);
                    stack.popPose();
                }
            }
        };
    }
}
