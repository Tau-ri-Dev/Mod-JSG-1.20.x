package dev.tauri.jsg.client.renderer.blockentity.energy;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.common.blockentity.energy.ZPMHubBE;
import dev.tauri.jsg.common.loader.ElementEnum;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ZPMSlotRenderer extends ZPMHubRenderer {
    public ZPMSlotRenderer(BlockEntityRendererProvider.Context ignored) {
        super(ignored);
    }

    @Override
    protected void renderMainObject(ZPMHubBE tile, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        stack.pushPose();
        stack.translate(-0.42, 0, -0.5);
        stack.scale(0.85f, 0.85f, 0.85f);
        ElementEnum.ZPM_SLOT.bindTexture().render(stack, source, light, overlay);
        stack.popPose();
    }

    @Override
    protected void renderZPM(int zpmId, ZPMHubBE te, float plusY, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        if (zpmId != 0) return;
        float zx = -0.1f;
        float zz = -0.08f;
        stack.pushPose();
        stack.translate(zx, 1 + (plusY * 0.8), zz);
        ZPMRenderer.renderZPM(stack, source, light, overlay, te.zpm1Level, 0.57f, (!te.isSlidingUp && !te.isAnimating), te.zpm1Type);
        stack.popPose();
    }
}
