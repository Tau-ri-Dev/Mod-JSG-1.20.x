package dev.tauri.jsg.common.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.common.worldgen.feature.OreSpireFeature;
import dev.tauri.jsg.common.worldgen.feature.config.OreSpireConfig;
import net.minecraft.world.level.levelgen.feature.Feature;
import dev.tauri.jsg.core.common.registry.JSGDeferredRegister;
import dev.tauri.jsg.core.common.registry.RegistryObject;

public class JSGFeatures {
    private static final JSGDeferredRegister<Feature<?>> REGISTER = JSGApi.REGISTRY_HELPER.feature();

    public static final RegistryObject<Feature<?>> ORE_SPIRE = register("ore_spire", new OreSpireFeature(OreSpireConfig.CODEC));

    protected static RegistryObject<Feature<?>> register(String name, Feature<?> f) {
        return REGISTER.register(name, () -> f);
    }

    public static void init() {
    }
}
