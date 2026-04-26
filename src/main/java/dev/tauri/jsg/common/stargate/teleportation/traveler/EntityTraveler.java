package dev.tauri.jsg.common.stargate.teleportation.traveler;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.common.registry.tags.JSGEntitiesTags;
import dev.tauri.jsg.common.stargate.manager.StargateEventHorizonManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityTraveler<E extends Entity> extends AbstractTraveler<E> {
    protected E entity;
    protected final Vec3 originalPos;
    protected final Vec3 originalMotion;
    protected final Vec3 destinationPos;
    protected final Vec3 destinationMotion;
    protected final float destinationYaw;

    public EntityTraveler(E entity, Vec3 destinationPos, Vec3 originalMotion, Vec3 destinationMotion, float destinationYaw, Stargate<?> sourceGate, Stargate<?> receivingGate, boolean isStatic) {
        super(sourceGate, receivingGate, isStatic);
        this.entity = entity;
        this.destinationMotion = destinationMotion;
        this.destinationPos = destinationPos;
        this.originalPos = entity.position();
        this.originalMotion = originalMotion;
        this.destinationYaw = destinationYaw;
    }

    @Override
    public boolean canBeSendToTarget() {
        return !get().getType().is(JSGEntitiesTags.STARGATE_UNTRANSPORTABLE_ENTITIES);
    }

    @Override
    public E get() {
        return entity;
    }

    @Override
    public Vec3 getOriginalMotion() {
        return originalMotion;
    }

    @Override
    public Vec3 getDestinationMotion() {
        return destinationMotion;
    }

    @Override
    public void setMotion(Vec3 newMotion) {
        get().setDeltaMovement(newMotion);
    }

    @Override
    public Vec3 getOriginalPos() {
        return originalPos;
    }

    @Override
    public Vec3 getDestinationPos() {
        return destinationPos;
    }

    @Override
    public void setPos(Vec3 newPos) {
        get().setPos(newPos);
        get().teleportTo(newPos.x, newPos.y, newPos.z);
    }

    @Override
    public void setYaw(float yaw) {
        get().setYRot(yaw);
    }

    @Override
    public void sendChangeDimension(ServerLevel targetLevel) {
        get().changeDimension(targetLevel, sourceGate.getEventHorizonManager().getTeleporter(this, (traveler) -> {
        }));
    }

    @Override
    public float getDestinationYaw() {
        return destinationYaw;
    }

    @Override
    public void killWrongTravel() {
        ((StargateEventHorizonManager) getTransmitter().getEventHorizonManager()).remove(this);
        get().setDeltaMovement(0, 0, 0);
        get().kill();
    }

    @Override
    public void killIris() {
        get().setDeltaMovement(0, 0, 0);
        get().kill();
    }

    @Override
    public void killKawoosh() {
        get().setDeltaMovement(0, 0, 0);
        get().kill();
    }

    @Override
    public void killUnstable() {
        get().setDeltaMovement(0, 0, 0);
        get().kill();
    }

    @Override
    public int hashCode() {
        return get().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof EntityTraveler<?>)) return false;
        return o.hashCode() == hashCode();
    }
}
