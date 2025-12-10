package dev.tauri.jsg.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.registry.BiomeOverlayRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface IModelsHolder {
    default void loadEntry(String texturePath, boolean byOverlay) {
        for (BiomeOverlayRegistry.BiomeOverlayInstance biomeOverlay : BiomeOverlayRegistry.values()) {
            if (!byOverlay) {
                getBiomeTextureResourceMap().put(biomeOverlay, getLoadersHolder().texture().getTextureResource(texturePath));
            } else {
                String[] split = texturePath.split("\\.");
                getBiomeTextureResourceMap().put(biomeOverlay, getLoadersHolder().texture().getTextureResource(split[0] + biomeOverlay.getSuffix() + "." + split[1]));
            }
        }
    }

    @NotNull
    LoadersHolder getLoadersHolder();

    @NotNull
    ResourceLocation getModelLocation();

    @NotNull
    Map<BiomeOverlayRegistry.BiomeOverlayInstance, ResourceLocation> getBiomeTextureResourceMap();

    @NotNull
    List<BiomeOverlayRegistry.BiomeOverlayInstance> getNonExistingTexturesReported();

    default void render(PoseStack ps) {
        getLoadersHolder().model().getModel(getModelLocation()).render(ps);
    }

    default void render(PoseStack ps, boolean renderEmissive) {
        getLoadersHolder().model().getModel(getModelLocation()).render(ps, renderEmissive);
    }

    default void bindTexture(BiomeOverlayRegistry.BiomeOverlayInstance biomeOverlay) {
        ResourceLocation resourceLocation = getBiomeTextureResourceMap().get(biomeOverlay);
        bindTexture(biomeOverlay, resourceLocation);
    }

    default void bindTexture(BiomeOverlayRegistry.BiomeOverlayInstance biomeOverlay, ResourceLocation resourceLocation) {
        if (!getLoadersHolder().texture().isTextureLoaded(resourceLocation)) {
            if (!getNonExistingTexturesReported().contains(biomeOverlay)) {
                JSGApi.logger.error("{} tried to use BiomeOverlay {} but it doesn't exist. ({})", this, biomeOverlay.id, resourceLocation);
                getNonExistingTexturesReported().add(biomeOverlay);
            }
            resourceLocation = getBiomeTextureResourceMap().get(BiomeOverlayRegistry.NORMAL);
        }

        getLoadersHolder().texture().getTexture(resourceLocation).bindTexture();
    }

    default void bindTextureAndRender(PoseStack ps) {
        bindTextureAndRender(BiomeOverlayRegistry.NORMAL, ps);
    }

    default void bindTextureAndRender(BiomeOverlayRegistry.BiomeOverlayInstance biomeOverlay, PoseStack ps) {
        bindTexture(biomeOverlay);
        render(ps);
    }
}
