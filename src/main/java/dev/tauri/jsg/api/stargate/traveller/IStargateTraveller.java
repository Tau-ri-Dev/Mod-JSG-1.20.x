package dev.tauri.jsg.api.stargate.traveller;

import dev.tauri.jsg.api.stargate.Stargate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public interface IStargateTraveller<E> {
    E get();

    Stargate<?> getTransmitter();

    Stargate<?> getReceiver();

    Vec3 getOriginalMotion();

    Vec3 getDestinationMotion();

    void setMotion(Vec3 newMotion);

    Vec3 getOriginalPos();

    Vec3 getDestinationPos();

    void setPos(Vec3 newPos);

    void setYaw(float yaw);

    float getDestinationYaw();

    default boolean isSameDimension() {
        return getTransmitter().getStargatePos() != null && getReceiver().getStargatePos() != null && getTransmitter().getStargatePos().dimension == getReceiver().getStargatePos().dimension;
    }

    default boolean isPlayer() {
        return false;
    }

    default boolean isStatic() {
        return false;
    }

    default boolean canBeKilledByIris() {
        return true;
    }

    default void place() {
        setPos(getDestinationPos());
        setYaw(getDestinationYaw());
        setMotion(getDestinationMotion());
    }

    default boolean canBeSendThisWay(boolean rightWay) {
        return rightWay;
    }

    default boolean canBeSendToTarget() {
        return true;
    }

    void sendChangeDimension(ServerLevel targetLevel);

    default void sendSameDimension() {
        getReceiver().getEventHorizonManager().receive(this);
    }

    default void send() {
        if (!isStatic() && !getTransmitter().getEventHorizonManager().canBeSend(this)) {
            return;
        }

        if (!isSameDimension())
            sendChangeDimension((ServerLevel) getReceiver().getLevel());
        else
            sendSameDimension();
    }

    void killWrongTravel();

    void killIris();

    void killKawoosh();

    void killUnstable();

    int hashCode();

    boolean equals(Object object);
}
