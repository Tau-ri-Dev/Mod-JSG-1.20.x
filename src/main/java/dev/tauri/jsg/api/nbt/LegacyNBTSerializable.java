package dev.tauri.jsg.api.nbt;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

/**
 * Bridges NeoForge's provider-aware {@link INBTSerializable} signatures back to the
 * 1.20.1-style provider-less methods used throughout JSG (none of which need registry access).
 */
public interface LegacyNBTSerializable extends INBTSerializable<CompoundTag> {
    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag compound);

    @Override
    default CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return serializeNBT();
    }

    @Override
    default void deserializeNBT(HolderLookup.Provider provider, CompoundTag compound) {
        deserializeNBT(compound);
    }
}
