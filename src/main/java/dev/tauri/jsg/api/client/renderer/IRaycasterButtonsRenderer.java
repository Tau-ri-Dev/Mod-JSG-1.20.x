package dev.tauri.jsg.api.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.raycaster.Raycaster;
import dev.tauri.jsg.api.raycaster.util.RayCastedButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public interface IRaycasterButtonsRenderer {
    List<RayCastedButton> getRaycasterButtons();

    Raycaster getRaycaster();

    default void renderRaycasterButtons(BlockEntity be, PoseStack poseStack, MultiBufferSource bufferSource) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        poseStack.pushPose();

        var translation = getRaycaster().getTranslation(be.getLevel(), be.getBlockPos());
        poseStack.translate(translation.getX(), translation.getY(), translation.getZ());
        var rotation = getRaycaster().getRotation(be.getLevel(), be.getBlockPos(), player);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        if (JSGConfig.Debug.renderBoundingBoxes.get() || Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            poseStack.pushPose();
            for (var btn : getRaycasterButtons()) {
                btn.render(poseStack);
            }
            poseStack.popPose();
        }

        if (player.isShiftKeyDown()) {
            var btn = getRaycaster().getRaycastedButton(player.level(), be.getBlockPos(), player);
            if (btn != null) {
                btn.renderTitle(rotation, poseStack, bufferSource);
            }
        }
        poseStack.popPose();
    }
}
