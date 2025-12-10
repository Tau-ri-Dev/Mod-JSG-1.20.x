package dev.tauri.jsg.api.client.screen.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class GuiHelper {

    public static void translateFor3D(){
        currentStack.scale(1, -1, 1);
    }

    public static PoseStack currentStack = null;

    public static void drawTexturedRectScaled(int xLeftCoord, int yBottomCoord, TextureAtlasSprite textureSprite, int maxWidth, int maxHeight, float scaleHeight) {
        maxHeight = (int) (maxHeight * scaleHeight);
        yBottomCoord -= maxHeight;

        drawTexturedRect(xLeftCoord, yBottomCoord, textureSprite, maxWidth, maxHeight, scaleHeight);
    }

    public static void drawTexturedRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int maxWidth, int maxHeight, float scaleHeight) {
        double v = textureSprite.getV1() - textureSprite.getV0();
        v *= (1 - scaleHeight);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, textureSprite.atlasLocation());
        Matrix4f matrix = currentStack.last().pose();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, (xCoord), (yCoord + maxHeight), 0).uv(textureSprite.getU0(), textureSprite.getV1()).endVertex();
        bufferbuilder.vertex(matrix, (xCoord + maxWidth), (yCoord + maxHeight), 0).uv(textureSprite.getU1(), textureSprite.getV1()).endVertex();
        bufferbuilder.vertex(matrix, (xCoord + maxWidth), (yCoord), 0).uv(textureSprite.getU1(), (float) (textureSprite.getV0() + v)).endVertex();
        bufferbuilder.vertex(matrix, (xCoord), (yCoord), 0).uv(textureSprite.getU0(), (float) (textureSprite.getV0() + v)).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void drawTexturedRect(float x, float y, float textureX, float textureY, float width, float height) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = currentStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, (x), (y + height), 0).uv(((float) (textureX) * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.vertex(matrix, (x + width), (y + height), 0).uv(((float) (textureX + width) * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.vertex(matrix, (x + width), (y), 0).uv(((float) (textureX + width) * 0.00390625F), ((float) (textureY) * 0.00390625F)).endVertex();
        bufferbuilder.vertex(matrix, (x), (y), 0).uv(((float) (textureX) * 0.00390625F), ((float) (textureY) * 0.00390625F)).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        drawTexturedModalRect(x, y, u, v, width, height, 0);
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float zLevel) {
        float uScale = 0.00390625F;
        float vScale = 0.00390625F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = currentStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, (float) x, (float) (y + height), zLevel).uv((float) u * 0.00390625F, (float) (v + height) * 0.00390625F).endVertex();
        bufferbuilder.vertex(matrix, (float) (x + width), (float) (y + height), zLevel).uv((float) (u + width) * 0.00390625F, (float) (v + height) * 0.00390625F).endVertex();
        bufferbuilder.vertex(matrix, (float) (x + width), (float) y, zLevel).uv((float) (u + width) * 0.00390625F, (float) v * 0.00390625F).endVertex();
        bufferbuilder.vertex(matrix, (float) x, (float) y, zLevel).uv((float) u * 0.00390625F, (float) v * 0.00390625F).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
        return pointX >= rectX && pointY >= rectY && pointX < (rectX + rectWidth) && pointY < (rectY + rectHeight);
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = currentStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, x, y + height, 0.0f).uv((u * f), ((v + (float) height) * f1)).endVertex();
        bufferbuilder.vertex(matrix, (x + width), (y + height), 0.0f).uv(((u + (float) width) * f), ((v + (float) height) * f1)).endVertex();
        bufferbuilder.vertex(matrix, (x + width), y, 0.0f).uv(((u + (float) width) * f), (v * f1)).endVertex();
        bufferbuilder.vertex(matrix, x, y, 0.0f).uv((u * f), (v * f1)).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Matrix4f matrix = currentStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, x, (y + height), 0.0f).uv((u * f), ((v + (float) vHeight) * f1)).endVertex();
        bufferbuilder.vertex(matrix, (x + width), (y + height), 0.0f).uv(((u + (float) uWidth) * f), ((v + (float) vHeight) * f1)).endVertex();
        bufferbuilder.vertex(matrix, (x + width), y, 0.0f).uv(((u + (float) uWidth) * f), (v * f1)).endVertex();
        bufferbuilder.vertex(matrix, x, y, 0.0f).uv((u * f), (v * f1)).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void drawScaledCustomSizeModalRectColor(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight, float r, float g, float b, float a) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Matrix4f matrix = currentStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix, x, (y + height), 0.0f).uv((u * f), ((v + (float) vHeight) * f1)).color(r, g, b, a).endVertex();
        bufferbuilder.vertex(matrix, (x + width), (y + height), 0.0f).uv(((u + (float) uWidth) * f), ((v + (float) vHeight) * f1)).color(r, g, b, a).endVertex();
        bufferbuilder.vertex(matrix, (x + width), y, 0.0f).uv(((u + (float) uWidth) * f), (v * f1)).color(r, g, b, a).endVertex();
        bufferbuilder.vertex(matrix, x, y, 0.0f).uv((u * f), (v * f1)).color(r, g, b, a).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Matrix4f matrix = currentStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        //RenderSystem.setShaderColor(f, f1, f2, f3);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix, left, bottom, 0.0f).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(matrix, right, bottom, 0.0f).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(matrix, right, top, 0.0f).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(matrix, left, top, 0.0f).color(f, f1, f2, f3).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        drawGradientRect(currentStack, left, top, right, bottom, startColor, endColor);
    }

    public static void drawGradientRect(PoseStack stack, int left, int top, int right, int bottom, int startColor, int endColor) {
        drawGradientRect(stack.last().pose(), 0, left, top, right, bottom, startColor, endColor);
    }

    public static void drawGradientRect(Matrix4f mat, int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(mat, right, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        bufferbuilder.vertex(mat, left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        bufferbuilder.vertex(mat, left, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        bufferbuilder.vertex(mat, right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());

        RenderSystem.disableBlend();
    }

    public static void drawTexturedRectWithShadow(int x, int y, int xOffset, int yOffset, int xSize, int ySize, float color) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(color, color, color, 1);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, xSize, ySize, xSize, ySize);

        RenderSystem.setShaderColor(color, color, color, 0.2f);
        drawModalRectWithCustomSizedTexture(x + xOffset, y + yOffset, 0, 0, xSize, ySize, xSize, ySize);
        RenderSystem.disableBlend();

        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public static void drawHoveringText(GuiGraphics graphics, Font font, List<String> textLines, int mouseX, int mouseY) {
        List<Component> components = new ArrayList<>();
        for (String s : textLines)
            components.add(Component.literal(s));
        graphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
    }


    public static void renderTexturedRect(GuiGraphics graphics, ResourceLocation texture, int x, int y, int z, int u, int v, int width, int height, int texWidth, int texHeight) {
        graphics.blit(texture, x, y, z, u, v, texWidth, texHeight, width, height);
    }


    public static void blit(GuiGraphics graphics, ResourceLocation tex, int x, int y, int sizeX, int sizeY, float p_282285_, float p_283199_, int p_282186_, int p_282322_, int p_282481_, int p_281887_) {
        graphics.blit(tex, x, y, sizeX, sizeY, p_282285_, p_283199_, p_282186_, p_282322_, p_282481_, p_281887_);
    }

    public static void renderTransparentBackground(GuiGraphics graphics, Screen screen) {
        graphics.fillGradient(0, 0, screen.width, screen.height, -1072689136, -804253680);
    }

    public static void drawTiledSprite(int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite) {
        drawTiledSprite(xPosition, yPosition, yOffset, desiredWidth, desiredHeight, sprite, 16, 16, 0);
    }

    public static void drawTiledSprite(int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int zLevel) {
        drawTiledSprite(xPosition, yPosition, yOffset, desiredWidth, desiredHeight, sprite, textureWidth, textureHeight, zLevel, true);
    }

    public static void drawTiledSprite(int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int zLevel, boolean blend) {
        if (desiredWidth == 0 || desiredHeight == 0 || textureWidth == 0 || textureHeight == 0) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        int xTileCount = desiredWidth / textureWidth;
        int xRemainder = desiredWidth - (xTileCount * textureWidth);
        int yTileCount = desiredHeight / textureHeight;
        int yRemainder = desiredHeight - (yTileCount * textureHeight);
        int yStart = yPosition + yOffset;
        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();
        float uDif = uMax - uMin;
        float vDif = vMax - vMin;
        if (blend) {
            RenderSystem.enableBlend();
        }
        //Note: We still use the tesselator as that is what GuiGraphics#innerBlit does
        BufferBuilder vertexBuffer = Tesselator.getInstance().getBuilder();
        vertexBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = currentStack.last().pose();
        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            int width = (xTile == xTileCount) ? xRemainder : textureWidth;
            if (width == 0) {
                break;
            }
            int x = xPosition + (xTile * textureWidth);
            int maskRight = textureWidth - width;
            int shiftedX = x + textureWidth - maskRight;
            float uLocalDif = uDif * maskRight / textureWidth;
            float uLocalMin;
            float uLocalMax;
            uLocalMin = uMin + uLocalDif;
            uLocalMax = uMax;
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int height = (yTile == yTileCount) ? yRemainder : textureHeight;
                if (height == 0) {
                    //Note: We don't want to fully break out because our height will be zero if we are looking to
                    // draw the remainder, but there is no remainder as it divided evenly
                    break;
                }
                int y = yStart - ((yTile + 1) * textureHeight);
                int maskTop = textureHeight - height;
                float vLocalDif = vDif * maskTop / textureHeight;
                float vLocalMin;
                float vLocalMax;
                vLocalMin = vMin;
                vLocalMax = vMax - vLocalDif;
                vertexBuffer.vertex(matrix4f, x, y + textureHeight, zLevel).uv(uLocalMin, vLocalMax).endVertex();
                vertexBuffer.vertex(matrix4f, shiftedX, y + textureHeight, zLevel).uv(uLocalMax, vLocalMax).endVertex();
                vertexBuffer.vertex(matrix4f, shiftedX, y + maskTop, zLevel).uv(uLocalMax, vLocalMin).endVertex();
                vertexBuffer.vertex(matrix4f, x, y + maskTop, zLevel).uv(uLocalMin, vLocalMin).endVertex();
            }
        }
        BufferUploader.drawWithShader(vertexBuffer.end());
        if (blend) {
            RenderSystem.disableBlend();
        }
    }
}
