package dev.tauri.jsg.api.loader.model;

import dev.tauri.jsg.loader.model.ModelLoader;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class APIOBJLoader {
    public static APIOBJLoader createLoader(String modId, Class<?> modMainClass) {
        return new APIOBJLoader(modId, modMainClass);
    }

    protected final ModelLoader LOADER;

    private APIOBJLoader(String modId, Class<?> modMainClass) {
        LOADER = new ModelLoader(modId, modMainClass);
    }

    public void loadResources() {
        LOADER.loadModels();
    }

    public APIOBJModel getModel(ResourceLocation resourceLocation) {
        return new APIOBJModel(LOADER.getModel(resourceLocation));
    }
}
