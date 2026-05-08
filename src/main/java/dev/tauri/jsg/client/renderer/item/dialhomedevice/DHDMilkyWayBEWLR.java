package dev.tauri.jsg.client.renderer.item.dialhomedevice;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.StargatePointOfOriginsDefaults;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.common.loader.ElementEnum;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class DHDMilkyWayBEWLR extends DHDAbstractBEWLR {
    public static final ResourceLocation SYMBOLS_TEX = JSGMapping.rl(JSG.MOD_ID, "textures/tesr/milkyway/dhd/dhd_button_light_0.jpg");
    public static final ResourceLocation BRB_TEX = JSGMapping.rl(JSG.MOD_ID, "textures/tesr/milkyway/dhd/dhd_brb_0.jpg");

    @Override
    public void renderDHD(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float partialTick) {
        if (Block.byItem(itemStack.getItem()) == JSGBlocks.DHD_MILKYWAY.get()) {
            // render DHD
            ElementEnum.MILKYWAY_DHD_BASE.bindTexture().render(stack, bufferSource, light);
            ElementEnum.MILKYWAY_DHD_BUTTON_CONSOLE.render(stack, bufferSource, light);
            ElementEnum.MILKYWAY_DHD_UPGRADE_COVER.render(stack, bufferSource, light);

            // render symbols
            for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
                stack.pushPose();

                if (symbol.origin()) {
                    // render plate for PoO
                    var plate = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("milkyway/dhd/buttons/4_base.obj");
                    var plateLight = JSGApi.JSG_LOADERS_HOLDER.model().getModelResource("milkyway/dhd/buttons/4.obj");
                    ElementEnum.MILKYWAY_DHD_BASE.bindTexture();
                    JSGApi.JSG_LOADERS_HOLDER.model().getModel(plate).render(stack, bufferSource, light, overlay, false);

                    JSGApi.JSG_LOADERS_HOLDER.texture().getTexture(SYMBOLS_TEX).bindTexture();
                    JSGApi.JSG_LOADERS_HOLDER.model().getModel(plateLight).render(stack, bufferSource, light, overlay, false);
                }

                // render symbol base
                ElementEnum.MILKYWAY_DHD_BASE.bindTexture();
                symbol.getModel(StargateTypes.MILKYWAY.get(), null, StargatePointOfOriginsDefaults.VARIANT_DHD).render(stack, bufferSource, light);

                // render symbol light emissive
                JSGApi.JSG_LOADERS_HOLDER.texture().getTexture((symbol.brb() ? BRB_TEX : SYMBOLS_TEX)).bindTexture();
                symbol.getModel(StargateTypes.MILKYWAY.get(), null, StargatePointOfOriginsDefaults.VARIANT_DHD_LIGHT).render(stack, bufferSource, light);
                stack.popPose();
            }
        }
    }
}
