package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.traveler.IStargateTeleporter;
import dev.tauri.jsg.api.stargate.traveler.IStargateTraveler;
import dev.tauri.jsg.api.stargate.traveler.TravelerSendResult;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;

public interface IStargateEventHorizonManager extends INBTSerializable<CompoundTag>, ITickable {
    TravelerSendResult send(IStargateTraveler<?> traveler);

    void receive(IStargateTraveler<?> traveler);

    IStargateTraveler<?> getTraveler(Entity entity);

    IStargateTraveler<?> getRIGTraveler(ServerLevel level, Entity entity, Vec3 originalMotion);

    IStargateTraveler<?> getTraveler(Entity entity, Vec3 originalMotion);

    IStargateTraveler<?> getStaticTraveler(Stargate<?> targetGate, Entity entity);

    boolean isMovingTowardsGate(Vec3 motionVec);

    IStargateTeleporter getTeleporter(IStargateTraveler<?> traveler, Consumer<IStargateTraveler<?>> afterPlace);

    boolean canBeSend(IStargateTraveler<?> traveler);

    IStargateTraveler<?> getTraveler(Entity entity, IStargateTraveler<?> traveler);

    IStargateTraveler<?> getTraveler(Entity entity, Vec3 destinationPos, Vec3 originalMotion, Vec3 destinationMotion, float destinationYaw, Stargate<?> sourceGate, Stargate<?> targetGate, boolean isStatic);

    default IStargateTraveler<?> getTraveler(Entity entity, Vec3 destinationPos, Vec3 originalMotion, Vec3 destinationMotion, float destinationYaw, Stargate<?> sourceGate, Stargate<?> targetGate) {
        return getTraveler(entity, destinationPos, originalMotion, destinationMotion, destinationYaw, sourceGate, targetGate, false);
    }
}
