package dev.tauri.jsg.registry;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class JSGBiomes {
    public static final ResourceKey<Biome> ABYDOS_DESERT = register("abydos_desert");
    public static final ResourceKey<Biome> ABYDOS_NAQUADAH_DEPOSITS = register("abydos_naquadah_deposits");
    public static final ResourceKey<Biome> ABYDOS_PLAINS = register("abydos_plains");

    private static ResourceKey<Biome> register(String pKey) {
        return ResourceKey.create(Registries.BIOME, JSGMapping.rl(JSG.MOD_ID, pKey));
    }
}
