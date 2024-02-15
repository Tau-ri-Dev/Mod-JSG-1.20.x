package dev.tauri.jsg.api.loader.holder;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.TextureOverlay;
import dev.tauri.jsg.api.loader.model.APIOBJLoader;
import dev.tauri.jsg.api.loader.texture.APITextureLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example holder for your models - copy this to your mod if you want
 */
public enum ExampleModelHolder {
    EXAMPLE_MODEL("my_model.obj", "my_texture.obj", true);

    public final ResourceLocation model;
    public final Map<TextureOverlay, ResourceLocation> biomeTextureResourceMap = new HashMap<>();
    private final List<TextureOverlay> nonExistingReported = new ArrayList<>();


    /*
    THIS SHOULD BE INSIDE YOUR MAIN CLASS NOT THIS CLASS!
    AND ALSO SHOULD BE STATIC!
     */
    public final APIOBJLoader OBJ_LOADER = APIOBJLoader.createLoader(JSG.MOD_ID, JSG.class);
    public final APITextureLoader TEXTURE_LOADER = APITextureLoader.createLoader(JSG.MOD_ID, JSG.class);


    /**
     *
     * @param modelPath - path to your model (excluding "models/tesr/")
     * @param texturePath - path to your texture (excluding "textures/tesr/")
     * @param byOverlay - should save overlay textures?
     */
    ExampleModelHolder(String modelPath, String texturePath, boolean byOverlay) {
        this.model = OBJ_LOADER.getModelResource(modelPath);

        for (ExampleTextureOverlay texOverlay : ExampleTextureOverlay.values()) {
            if (!byOverlay) {
                biomeTextureResourceMap.put(texOverlay, TEXTURE_LOADER.getTextureResource(texturePath));
            } else {
                String[] split = texturePath.split("\\.");
                biomeTextureResourceMap.put(texOverlay, TEXTURE_LOADER.getTextureResource(split[0] + texOverlay.getSuffix() + "." + split[1]));
            }
        }
    }

    /**
     * Renders the model (need to bind texture first)
     * @param ps - PoseStack from render
     */
    public void render(PoseStack ps) {
        OBJ_LOADER.getModel(model).render(ps);
    }

    /**
     * Bind texture with overlay
     * @param overlay - tex overlay
     */
    public void bindTexture(TextureOverlay overlay) {
        ResourceLocation resourceLocation = biomeTextureResourceMap.get(overlay);
        bindTexture(overlay, resourceLocation);
    }
    private void bindTexture(TextureOverlay overlay, ResourceLocation resourceLocation) {
        if (!TEXTURE_LOADER.isTextureLoaded(resourceLocation)) {
            if (!nonExistingReported.contains(overlay)) {
                JSG.logger.error(this + " tried to use BiomeOverlay " + overlay + " but it doesn't exist. (" + resourceLocation + ")");
                nonExistingReported.add(overlay);
            }
            resourceLocation = biomeTextureResourceMap.get(ExampleTextureOverlay.NORMAL);
        }

        TEXTURE_LOADER.getTexture(resourceLocation).bindTexture();
    }

    /**
     * Binds texture and renders the model
     * @param ps - PoseStack
     */
    public void bindTextureAndRender(PoseStack ps) {
        bindTextureAndRender(ExampleTextureOverlay.NORMAL, ps);
    }

    /**
     * Binds texture with overlay and renders the model
     * @param ps - PoseStack
     */
    public void bindTextureAndRender(TextureOverlay biomeOverlay, PoseStack ps) {
        bindTexture(biomeOverlay);
        render(ps);
    }
}
