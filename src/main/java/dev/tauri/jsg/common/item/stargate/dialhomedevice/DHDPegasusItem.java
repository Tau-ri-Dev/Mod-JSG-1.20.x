package dev.tauri.jsg.common.item.stargate.dialhomedevice;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolPegasusEnum;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.core.common.item.JSGModelOBJInGUIRenderer;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class DHDPegasusItem extends DHDAbstractItem {
    public static final ResourceLocation SYMBOLS_TEX = JSGMapping.rl(JSG.MOD_ID, "textures/tesr/pegasus/dhd/dhd_button_light_0.png");
    public static final ResourceLocation BRB_TEX = JSGMapping.rl(JSG.MOD_ID, "textures/tesr/pegasus/dhd/dhd_bbb_0.jpg");

    public DHDPegasusItem(Block block) {
        super(block);
    }

    @Override
    public JSGModelOBJInGUIRenderer.RenderPartInterface getRenderPartInterface() {
        return (itemStack, itemDisplayContext, stack, bufferSource, light, overlay) -> {
            if (Block.byItem(itemStack.getItem()) == JSGBlocks.DHD_PEGASUS.get()) {
                // render DHD
                ElementEnum.PEGASUS_DHD_BASE.bindTexture().render(stack, bufferSource, light);
                ElementEnum.PEGASUS_DHD_BUTTON_CONSOLE.render(stack, bufferSource, light);
                ElementEnum.PEGASUS_DHD_UPGRADE_COVER.render(stack, bufferSource, light);

                // render symbols
                for (SymbolPegasusEnum symbol : SymbolPegasusEnum.values()) {
                    stack.pushPose();
                    JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(symbol.brb() ? BRB_TEX : SYMBOLS_TEX).bindTexture();
                    symbol.getModel(symbol.getSymbolType().getPointOfOriginType(), null, StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT).render(stack, bufferSource, light);
                    if (symbol.brb())
                        ElementEnum.PEGASUS_DHD_BASE.bindTexture(CoreBiomeOverlays.NORMAL);
                    symbol.getModel(symbol.getSymbolType().getPointOfOriginType(), null, StargatePointOfOriginsDefaults.VARIANT_DHD).render(stack, bufferSource, light);
                    stack.popPose();
                }
            }
        };
    }
}
