package dev.tauri.jsg.client.renderer.item.dialhomedevice;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.client.renderer.AbstractItemBEWLR;
import dev.tauri.jsg.core.common.util.RotationUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public abstract class DHDAbstractBEWLR extends AbstractItemBEWLR {
    @Override
    public void renderItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float partialTick) {
        if (itemDisplayContext == ItemDisplayContext.GUI) {
            stack.mulPose(Axis.ZP.rotationDegrees(-45));
            stack.scale(0.45f, 0.45f, 0.45f);
            stack.translate(-0.55, 0, 0);
            stack.mulPose(Axis.XP.rotationDegrees(-30));
            stack.mulPose(Axis.YP.rotationDegrees(30));
        } else if (itemDisplayContext == ItemDisplayContext.GROUND) {
            stack.scale(0.6f, 0.6f, 0.6f);
        } else {
            stack.mulPose(RotationUtil.getRotation(null, Direction.NORTH));
            stack.translate(0, -0.8, -1);
            stack.scale(0.6f, 0.6f, 0.6f);
            stack.mulPose(Axis.YP.rotationDegrees(180));
        }

        renderDHD(itemStack, itemDisplayContext, stack, bufferSource, light, overlay, partialTick);
    }

    public abstract void renderDHD(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float partialTick);

    @Override
    public boolean renderHands(ItemDisplayContext itemDisplayContext) {
        return false;
    }
}
