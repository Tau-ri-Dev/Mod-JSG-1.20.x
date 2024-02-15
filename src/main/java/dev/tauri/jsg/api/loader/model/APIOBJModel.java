package dev.tauri.jsg.api.loader.model;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.loader.model.OBJModel;
import org.jetbrains.annotations.Nullable;

/**
 * Holder for OBJ Model since JSG OBJ MODEL class is not inside API
 */
public record APIOBJModel(OBJModel model) {
    public APIOBJModel(@Nullable OBJModel model) {
        if (model == null) {
            this.model = null; // TODO: add missing model
        } else
            this.model = model;
    }

    /**
     * Renders the model - only in-game render (no GUI)
     * @param stack - PoseStack
     */
    public void render(PoseStack stack) {
        render(stack, false);
    }

    /**
     * Renders the model
     * @param stack - PoseStack
     * @param renderGUI - should render to GUI?
     */
    public void render(PoseStack stack, boolean renderGUI) {
        boolean lastRender = OBJModel.renderGui;
        OBJModel.renderGui = renderGUI;
        model.render(stack);
        OBJModel.renderGui = lastRender;
    }
}
