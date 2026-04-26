package dev.tauri.jsg.common.stargate.teleportation.traveler;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.common.registry.JSGDamageTypes;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.stargate.manager.StargateEventHorizonManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class LivingTraveler<E extends LivingEntity> extends EntityTraveler<E> {
    public LivingTraveler(E entity, Vec3 destinationPos, Vec3 originalMotion, Vec3 destinationMotion, float destinationYaw, Stargate<?> sourceGate, Stargate<?> receivingGate, boolean isStatic) {
        super(entity, destinationPos, originalMotion, destinationMotion, destinationYaw, sourceGate, receivingGate, isStatic);
    }

    @Override
    public void killWrongTravel() {
        ((StargateEventHorizonManager) getTransmitter().getEventHorizonManager()).remove(this);
        if (!getTransmitter().getEventHorizonManager().isMovingTowardsGate(getOriginalMotion())) {
            return;
        }
        sourceGate.playSoundEvent(JSGSoundEvents.WORMHOLE_GO);
        JSGDamageTypes.killEntity(get(), JSGDamageTypes.WRONG_SIDE);
    }

    @Override
    public void killIris() {
        JSGDamageTypes.killEntity(get(), JSGDamageTypes.IRIS);
    }

    @Override
    public void killKawoosh() {
        JSGDamageTypes.killEntity(get(), JSGDamageTypes.KAWOOSH);
    }

    @Override
    public void killUnstable() {
        JSGDamageTypes.killEntity(get(), JSGDamageTypes.UNSTABLE_EH);
    }
}
