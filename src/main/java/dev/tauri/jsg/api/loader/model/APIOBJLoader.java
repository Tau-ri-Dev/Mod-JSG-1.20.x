package dev.tauri.jsg.api.loader.model;

import dev.tauri.jsg.loader.model.ModelLoader;
import dev.tauri.jsg.util.math.TemperatureHelper;
import net.minecraft.resources.ResourceLocation;


/**
 * Creates and holds instance of JSG OBJ Loader
 */
@SuppressWarnings("unused")
public class APIOBJLoader {
    /**
     * Create instance of JSG OBJ Loader manager for your mod
     */
    public static APIOBJLoader createLoader(String modId, Class<?> modMainClass) {
        return new APIOBJLoader(modId, modMainClass);
    }

    protected final ModelLoader LOADER;

    private APIOBJLoader(String modId, Class<?> modMainClass) {
        LOADER = new ModelLoader(modId, modMainClass);
    }

    /**
     * Loads all resources inside tesr folder (models)
     */
    public void loadResources() {
        LOADER.loadModels();
    }


    /**
     * Gets model for you
     */
    public APIOBJModel getModel(ResourceLocation resourceLocation) {
        return new APIOBJModel(LOADER.getModel(resourceLocation));
    }

    /**
     * Gets model resource for you
     */
    public ResourceLocation getModelResource(String model) {
        return LOADER.getModelResource(model);
    }
}
