package dev.tauri.jsg.common.stargate.teleportation.traveler;

import dev.tauri.jsg.api.stargate.Stargate;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.phys.Vec3;

public class FireballTraveler extends EntityTraveler<AbstractHurtingProjectile> {
    public FireballTraveler(AbstractHurtingProjectile entity, Vec3 destinationPos, Vec3 originalMotion, Vec3 destinationMotion, float destinationYaw, Stargate<?> sourceGate, Stargate<?> receivingGate, boolean isStatic) {
        super(entity, destinationPos, originalMotion, destinationMotion, destinationYaw, sourceGate, receivingGate, isStatic);
    }

    @Override
    public void setMotion(Vec3 newMotion) {
        super.setMotion(newMotion);
        get().xPower = newMotion.x() * 0.1D;
        get().yPower = newMotion.y() * 0.1D;
        get().zPower = newMotion.z() * 0.1D;
    }
}
