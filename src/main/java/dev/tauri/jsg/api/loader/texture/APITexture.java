package dev.tauri.jsg.api.loader.texture;

import dev.tauri.jsg.loader.texture.Texture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record APITexture(Texture tex) {
    public APITexture(@Nullable Texture tex) {
        if (tex == null) {
            this.tex = null; // TODO: add missing texture
        } else
            this.tex = tex;
    }

    public void bindTexture() {
        tex.bindTexture();
    }

    public static void bindTextureWithMc(ResourceLocation loc) {
        Texture.bindTextureWithMc(loc);
    }
}
