package dev.tauri.jsg.api.client.texture;

import net.minecraft.resources.ResourceLocation;

public interface ITextureLoader {
    void loadTextures();

    ITexture getTexture(ResourceLocation resourceLocation);

    boolean isTextureLoaded(ResourceLocation resourceLocation);

    ResourceLocation getTextureResource(String texture);
}
