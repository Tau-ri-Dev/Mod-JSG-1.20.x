package dev.tauri.jsg.registry;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public class JSGDamageTypes {
    public static final ResourceKey<DamageType> KAWOOSH = ResourceKey.create(Registries.DAMAGE_TYPE, JSGMapping.rl(JSG.MOD_ID, "kawoosh"));
    public static final ResourceKey<DamageType> IRIS = ResourceKey.create(Registries.DAMAGE_TYPE, JSGMapping.rl(JSG.MOD_ID, "iris"));
    public static final ResourceKey<DamageType> WRONG_SIDE = ResourceKey.create(Registries.DAMAGE_TYPE, JSGMapping.rl(JSG.MOD_ID, "wrong_side"));
    public static final ResourceKey<DamageType> UNSTABLE_EH = ResourceKey.create(Registries.DAMAGE_TYPE, JSGMapping.rl(JSG.MOD_ID, "unstable_eh"));

    public static void killEntity(Entity e, ResourceKey<DamageType> source) {
        hurtEntity(e, source, Float.MAX_VALUE);
    }

    public static void hurtEntity(Entity e, ResourceKey<DamageType> source, float amount) {
        e.hurt(new DamageSource(e.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(source), e), amount);
    }
}
