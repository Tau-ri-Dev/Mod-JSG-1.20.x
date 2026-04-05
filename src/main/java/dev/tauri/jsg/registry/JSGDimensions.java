package dev.tauri.jsg.registry;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class JSGDimensions {
    public static ResourceKey<Level> ABYDOS = dim("abydos");

    private static ResourceKey<Level> dim(String name) {
        return ResourceKey.create(Registries.DIMENSION, JSGMapping.rl(JSG.MOD_ID, name));
    }
}
