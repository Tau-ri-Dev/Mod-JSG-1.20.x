package dev.tauri.jsg.api.loader.model;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.loader.model.OBJModel;
import org.jetbrains.annotations.Nullable;

public record APIOBJModel(OBJModel model) {
    public APIOBJModel(@Nullable OBJModel model) {
        if (model == null) {
            this.model = null; // TODO: add missing texture
        } else
            this.model = model;
    }

    public void render(PoseStack stack, boolean renderGUI) {
        boolean lastRender = OBJModel.renderGui;
        OBJModel.renderGui = renderGUI;
        model.render(stack);
        OBJModel.renderGui = lastRender;
    }
}
