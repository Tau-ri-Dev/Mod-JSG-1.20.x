package dev.tauri.jsg.api.loader.texture;

import dev.tauri.jsg.loader.texture.TextureLoader;
import net.minecraft.resources.ResourceLocation;

/**
 * Creates and holds instance of JSG Texture loader
 */
@SuppressWarnings("unused")
public class APITextureLoader {
    /**
     * Create instance of JSG OBJ Loader manager for your mod
     */
    public static APITextureLoader createLoader(String modId, Class<?> modMainClass) {
        return new APITextureLoader(modId, modMainClass);
    }

    protected final TextureLoader LOADER;

    private APITextureLoader(String modId, Class<?> modMainClass) {
        LOADER = new TextureLoader(modId, modMainClass);
    }


    /**
     * Loads all resources inside tesr folder (textures)
     */
    public void loadResources() {
        LOADER.loadTextures();
    }

    /**
     * Gets texture for you
     */
    public APITexture getTexture(ResourceLocation resourceLocation) {
        return new APITexture(LOADER.getTexture(resourceLocation));
    }

    /**
     * Checks if the texture is loaded
     * @param resourceLocation - path to the texture
     * @return is the texture is loaded or not
     */
    public boolean isTextureLoaded(ResourceLocation resourceLocation) {
        return LOADER.isTextureLoaded(resourceLocation);
    }

    /**
     * Gets texture resource location for you
     */
    public ResourceLocation getTextureResource(String texturePath) {
        return LOADER.getTextureResource(texturePath);
    }
}
