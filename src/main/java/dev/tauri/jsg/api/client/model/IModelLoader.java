package dev.tauri.jsg.api.client.model;

import net.minecraft.resources.ResourceLocation;

public interface IModelLoader {
    void loadModels();

    AbstractOBJModel getModel(ResourceLocation resourceLocation);

    ResourceLocation getModelResource(String model);
}
