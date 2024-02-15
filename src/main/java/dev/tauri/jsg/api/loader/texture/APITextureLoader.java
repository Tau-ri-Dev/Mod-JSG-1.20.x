package dev.tauri.jsg.api.loader.texture;

import dev.tauri.jsg.loader.texture.TextureLoader;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class APITextureLoader {
    public static APITextureLoader createLoader(String modId, Class<?> modMainClass) {
        return new APITextureLoader(modId, modMainClass);
    }

    protected final TextureLoader LOADER;

    private APITextureLoader(String modId, Class<?> modMainClass) {
        LOADER = new TextureLoader(modId, modMainClass);
    }

    public void loadResources() {
        LOADER.loadTextures();
    }

    public APITexture getTexture(ResourceLocation resourceLocation) {
        return new APITexture(LOADER.getTexture(resourceLocation));
    }

    public boolean isTextureLoaded(ResourceLocation resourceLocation) {
        return LOADER.isTextureLoaded(resourceLocation);
    }

    public ResourceLocation getTextureResource(String texturePath) {
        return LOADER.getTextureResource(texturePath);
    }
}
