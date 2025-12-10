package dev.tauri.jsg.api.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.util.ILinkableBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public interface LinkableRenderer {
    default void renderLink(BlockPos pos, Object source, PoseStack stack, MultiBufferSource bufferSource) {
        if (Minecraft.getInstance().player == null) return;
        if (!JSGConfig.Debug.renderBoundingBoxes.get() && !Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes())
            return;
        var level = Minecraft.getInstance().player.level();
        if (!(source instanceof ILinkableBE<?> linkable)) return;
        var linked = linkable.getLinkedPos();
        if (linked == null) return;
        var linkedTile = level.getBlockEntity(linked);
        var relative = linked.subtract(pos).getCenter();
        stack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = stack.last().pose();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.debugLineStrip(6.0D));
        vertexconsumer.vertex(matrix, 0.5f, 0.5f, 0.5f).color(24f / 255f, 169f / 255f, 0f, 1f).endVertex();
        vertexconsumer.vertex(matrix, (float) relative.x, (float) relative.y, (float) relative.z).color(0, 34f / 255f, 169f / 255f, 1f).endVertex();

        var relativeNormal = relative.normalize();
        var titleX = 0.5 + (relativeNormal.x * 1.5f);
        var titleY = 0.5 + (relativeNormal.y * 1.5f) + (0.25f * (relative.y > 0 ? 1 : -1));
        var titleZ = 0.5 + (relativeNormal.z * 1.5f);
        var sourceName = linkable.getClass().getName();
        var targetName = (linkedTile == null ? "null" : linkedTile.getClass().getName());
        sourceName = sourceName.split("\\.")[sourceName.split("\\.").length - 1];
        targetName = targetName.split("\\.")[targetName.split("\\.").length - 1];
        renderTitle(Component.literal("Link: " + sourceName + " - " + targetName), new Vec3(titleX, titleY, titleZ), 1, stack, bufferSource, 24f / 255f, 169f / 255f, 0f);

        stack.popPose();
    }

    private void renderTitle(Component title, Vec3 pos, float scale, PoseStack stack, MultiBufferSource bufferSource, float r, float g, float b) {
        if (bufferSource == null) return;
        if (title == null) return;
        stack.pushPose();
        stack.translate(pos.x, pos.y, pos.z);
        stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        scale *= 0.4f;
        stack.scale(-0.025f * scale, -0.025f * scale, 0.025f * scale);
        Matrix4f matrix4f = stack.last().pose();
        float backgroundOpacityConfig = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        int backgroundOpacity = (int) (backgroundOpacityConfig * 255.0f) << 24;
        Font font = Minecraft.getInstance().font;
        float x = (float) (-font.width(title) / 2);
        int color = (int) (r * 0xff) << 16 | (int) (g * 0xff) << 8 | (int) (b * 0xff);
        font.drawInBatch(title, x, 0, 553648127, false, matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH, backgroundOpacity, LightTexture.FULL_BRIGHT);
        font.drawInBatch(title, x, 0, color, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        stack.popPose();
    }
}
