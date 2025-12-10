package dev.tauri.jsg.api.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class JSGAxisAlignedBB extends AABB {

    public static JSGAxisAlignedBB block() {
        return new JSGAxisAlignedBB(0, 0, 0, 1, 1, 1);
    }

    public JSGAxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public JSGAxisAlignedBB(BlockPos block) {
        super(block);
    }

    public JSGAxisAlignedBB(AABB a) {
        super(a.minX, a.minY, a.minZ, a.maxX, a.maxY, a.maxZ);
    }

    public JSGAxisAlignedBB(BoundingBox a) {
        super(a.minX(), a.minY(), a.minZ(), a.maxX(), a.maxY(), a.maxZ());
    }

    public JSGAxisAlignedBB(BlockPos min, BlockPos max) {
        super(min, max);
    }

    public JSGAxisAlignedBB(BlockPos center, int radius) {
        this(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius));
    }

    public JSGAxisAlignedBB(Vec3 p_82302_, Vec3 p_82303_) {
        super(p_82302_, p_82303_);
    }

    public JSGAxisAlignedBB rotate(int angle) {
        return switch (angle) {
            case 0 -> this;
            case 90 -> new JSGAxisAlignedBB(-minZ, minY, minX, -maxZ, maxY, maxX);
            case 180 -> new JSGAxisAlignedBB(-minX, minY, -minZ, -maxX, maxY, -maxZ);
            case 270, -90 -> new JSGAxisAlignedBB(minZ, minY, -minX, maxZ, maxY, -maxX);
            default -> throw new IllegalArgumentException("Angle not one of [0, 90, 180, 270, -90]");
        };
    }

    public JSGAxisAlignedBB grow(Vec3 vector) {
        return grow(vector.x(), vector.y(), vector.z());
    }

    public JSGAxisAlignedBB grow(double x, double y, double z) {
        double d0 = this.minX - x;
        double d1 = this.minY - y;
        double d2 = this.minZ - z;
        double d3 = this.maxX + x;
        double d4 = this.maxY + y;
        double d5 = this.maxZ + z;
        return new JSGAxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    public JSGAxisAlignedBB mul(double mul) {
        double d0 = this.minX * mul;
        double d1 = this.minY * mul;
        double d2 = this.minZ * mul;
        double d3 = this.maxX * mul;
        double d4 = this.maxY * mul;
        double d5 = this.maxZ * mul;
        return new JSGAxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    public JSGAxisAlignedBB offset(double x, double y, double z) {
        return new JSGAxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public JSGAxisAlignedBB inset(BlockPos pos) {
        return inset(pos.getX(), pos.getY(), pos.getZ());
    }

    public JSGAxisAlignedBB inset(double x, double y, double z) {
        return new JSGAxisAlignedBB(this.minX - x, this.minY - y, this.minZ - z, this.maxX - x, this.maxY - y, this.maxZ - z);
    }

    public JSGAxisAlignedBB offset(Vec3 vec) {
        return offset(vec.x(), vec.y(), vec.z());
    }

    public JSGAxisAlignedBB offset(Vec3i vec) {
        return offset(vec.getX(), vec.getY(), vec.getZ());
    }

    public JSGAxisAlignedBB offset(BlockPos pos) {
        return offset(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos getMinBlockPos() {
        return new BlockPos((int) minX, (int) minY, (int) minZ);
    }

    public BlockPos getMaxBlockPos() {
        return new BlockPos((int) maxX, (int) maxY, (int) maxZ);
    }

    public JSGAxisAlignedBB rotate(Direction facingHorizontal, Direction facingVertical) {
        return rotate(facingHorizontal, facingVertical, new Vec3(0, 0, 0));
    }

    public JSGAxisAlignedBB rotate(Direction facingHorizontal, Direction facingVertical, Vec3 pivot) {
        return RotationUtil.rotate(this, RotationUtil.getRotation(facingVertical, facingHorizontal), pivot);
    }

    @Override
    @NotNull
    public JSGAxisAlignedBB setMinX(double pMinX) {
        return new JSGAxisAlignedBB(pMinX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    @Override
    @NotNull
    public JSGAxisAlignedBB setMinY(double pMinY) {
        return new JSGAxisAlignedBB(this.minX, pMinY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    @Override
    @NotNull
    public JSGAxisAlignedBB setMinZ(double pMinZ) {
        return new JSGAxisAlignedBB(this.minX, this.minY, pMinZ, this.maxX, this.maxY, this.maxZ);
    }

    @Override
    @NotNull
    public JSGAxisAlignedBB setMaxX(double pMaxX) {
        return new JSGAxisAlignedBB(this.minX, this.minY, this.minZ, pMaxX, this.maxY, this.maxZ);
    }

    @Override
    @NotNull
    public JSGAxisAlignedBB setMaxY(double pMaxY) {
        return new JSGAxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, pMaxY, this.maxZ);
    }

    @Override
    @NotNull
    public JSGAxisAlignedBB setMaxZ(double pMaxZ) {
        return new JSGAxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, pMaxZ);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack stack) {
        render(null, stack, null);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack stack, float r, float g, float b, float a) {
        render(null, stack, null, r, g, b, a);
    }

    public void render(Component name, PoseStack stack, MultiBufferSource bufferSource) {
        render(name, stack, bufferSource, 1, 1, 1, 1);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(Component name, PoseStack stack, MultiBufferSource bufferSource, float r, float g, float b, float a) {
        stack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = stack.last().pose();

        renderLine(matrix, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
        renderLine(matrix, minX, minY, minZ, minX, minY, maxZ, r, g, b, a);
        renderLine(matrix, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
        renderLine(matrix, minX, minY, maxZ, maxX, minY, maxZ, r, g, b, a);

        renderLine(matrix, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
        renderLine(matrix, minX, maxY, minZ, minX, maxY, maxZ, r, g, b, a);
        renderLine(matrix, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
        renderLine(matrix, minX, maxY, maxZ, maxX, maxY, maxZ, r, g, b, a);

        renderLine(matrix, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
        renderLine(matrix, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
        renderLine(matrix, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);
        renderLine(matrix, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);

        renderName(name, stack, bufferSource, r, g, b);
        stack.popPose();
        RenderSystem.disableDepthTest();
    }

    private void renderName(Component name, PoseStack stack, MultiBufferSource bufferSource, float r, float g, float b) {
        if (bufferSource == null) return;
        if (name == null) return;
        stack.pushPose();
        stack.translate(maxX, maxY, maxZ);
        stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        var scale = 0.4f;
        stack.scale(-0.025f * scale, -0.025f * scale, 0.025f * scale);
        Matrix4f matrix4f = stack.last().pose();
        float backgroundOpacityConfig = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        int backgroundOpacity = (int) (backgroundOpacityConfig * 255.0f) << 24;
        Font font = Minecraft.getInstance().font;
        float x = (float) (-font.width(name) / 2);
        int color = (int) (r * 0xff) << 16 | (int) (g * 0xff) << 8 | (int) (b * 0xff);
        font.drawInBatch(name, x, 0, 553648127, false, matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH, backgroundOpacity, LightTexture.FULL_BRIGHT);
        font.drawInBatch(name, x, 0, color, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        stack.popPose();
    }

    private static void renderLine(Matrix4f m, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
        Tesselator t = Tesselator.getInstance();
        BufferBuilder buffer = t.getBuilder();
        buffer.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(m, (float) x1, (float) y1, (float) z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, (float) x2, (float) y2, (float) z2).color(r, g, b, a).endVertex();
        t.end();
    }
}
