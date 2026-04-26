package dev.tauri.jsg.common.stargate.teleportation.traveler;

import dev.tauri.jsg.api.stargate.Stargate;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

public class MinecartTraveler extends EntityTraveler<AbstractMinecart> {
    public MinecartTraveler(AbstractMinecart entity, Vec3 destinationPos, Vec3 originalMotion, Vec3 destinationMotion, float destinationYaw, Stargate<?> sourceGate, Stargate<?> receivingGate, boolean isStatic) {
        super(entity, destinationPos, originalMotion, destinationMotion, destinationYaw, sourceGate, receivingGate, isStatic);
    }

    @Override
    public void setMotion(Vec3 newMotion) {
        get().setDeltaMovement(new Vec3(0, 0, 0));
    }
}
