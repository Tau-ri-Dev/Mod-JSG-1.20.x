package dev.tauri.jsg.api.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;

public interface ITexture {
    void setShader();
    void bindTexture();

    static void bindTextureWithMc(ResourceLocation location) {
        RenderSystem.setShaderTexture(0, location);
    }
}
