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
        // 1.21: the xyz power vector became a scalar; direction now comes from the delta movement set above.
        get().accelerationPower = newMotion.length() * 0.1D;
    }
}
