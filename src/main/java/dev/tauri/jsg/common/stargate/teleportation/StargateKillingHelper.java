package dev.tauri.jsg.common.stargate.teleportation;

import dev.tauri.jsg.common.advancements.JSGAdvancements;
import dev.tauri.jsg.common.registry.JSGDamageTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class StargateKillingHelper {
    public static void kawooshKill(Entity e) {
        if (!(e instanceof LivingEntity)) {
            e.remove(Entity.RemovalReason.KILLED);
            return;
        }
        if (e instanceof ServerPlayer sp)
            JSGAdvancements.KAWOOSH_CREMATION.trigger(sp);
        JSGDamageTypes.killEntity(e, JSGDamageTypes.KAWOOSH);
    }

    public static void unstableEhKill(Entity e) {
        if (!(e instanceof LivingEntity)) {
            e.remove(Entity.RemovalReason.KILLED);
            return;
        }
        JSGDamageTypes.killEntity(e, JSGDamageTypes.UNSTABLE_EH);
    }
}
