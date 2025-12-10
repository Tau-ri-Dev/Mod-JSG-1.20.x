package dev.tauri.jsg.api.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class AbstractOBJModel {
    protected static TextureAtlasSprite usedTexSprite = null;
    public static EnumOBJRenderMethod renderType = EnumOBJRenderMethod.NORMAL;

    public static MultiBufferSource source;
    public boolean isEmpty;

    protected static float[] rgb = new float[]{1, 1, 1, 1};
    protected static float light = 1;
    public static int packedLight = LightTexture.FULL_BRIGHT;
    public static boolean noCulling = false;

    public static void setDynamicLightning(float l) {
        light = l;
    }

    public static void setRGB(float r, float g, float b, float a) {
        rgb = new float[]{r, g, b, a};
    }

    public static void resetDynamicLightning() {
        light = 1;
    }

    public static void resetRGB() {
        setRGB(1, 1, 1, 1);
    }

    public static void setUsedTexSprite(TextureAtlasSprite sprite) {
        usedTexSprite = sprite;
    }

    public static void resetUsedTexSprite() {
        usedTexSprite = null;
    }

    public enum EnumOBJRenderMethod {
        NORMAL,
        GUI,
        ADMIN_CONTROLLER
    }

    public void render(PoseStack stack) {
        render(stack, false);
    }

    public void render(PoseStack stack, boolean renderEmissive) {
        render(stack, renderEmissive, null);
    }

    public abstract void render(PoseStack stack, boolean renderEmissive, @Nullable Supplier<ShaderInstance> shaderOverride);
}
