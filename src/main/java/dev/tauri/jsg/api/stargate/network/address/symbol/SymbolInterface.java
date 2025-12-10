package dev.tauri.jsg.api.stargate.network.address.symbol;

import dev.tauri.jsg.api.registry.BiomeOverlayRegistry;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.AbstractSymbolType;
import dev.tauri.jsg.api.util.Localizable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface SymbolInterface extends Localizable {

    boolean origin();

    float getAngle();

    int getAngleIndex();

    int getId();

    default boolean brb() {
        return false;
    }

    String getEnglishName();

    ResourceLocation getIconResource(BiomeOverlayRegistry.BiomeOverlayInstance overlay, ResourceKey<Level> dimensionId, int configOrigin);

    default ResourceLocation getIconResource(int originId) {
        return getIconResource(BiomeOverlayRegistry.NORMAL, Level.OVERWORLD, originId);
    }

    default ResourceLocation getIconResource(BiomeOverlayRegistry.BiomeOverlayInstance overlay, ResourceKey<Level> dimensionId) {
        return getIconResource(overlay, dimensionId, -1);
    }

    default ResourceLocation getIconResource() {
        return getIconResource(BiomeOverlayRegistry.NORMAL, Level.OVERWORLD);
    }

    default boolean renderIconByMinecraft(int originId) {
        return true;
    }

    AbstractSymbolType<?> getSymbolType();

    boolean isValidForAddress();

    SymbolInterface getNext(boolean previous);
}
