package dev.tauri.jsg.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.worldgen.feature.OreSpireFeature;
import dev.tauri.jsg.worldgen.feature.config.OreSpireConfig;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class JSGFeatures {
    private static final DeferredRegister<Feature<?>> REGISTER = JSGApi.REGISTRY_HELPER.feature();

    public static final RegistryObject<Feature<?>> ORE_SPIRE = register("ore_spire", new OreSpireFeature(OreSpireConfig.CODEC));

    protected static RegistryObject<Feature<?>> register(String name, Feature<?> f) {
        return REGISTER.register(name, () -> f);
    }

    public static void init() {
    }
}
