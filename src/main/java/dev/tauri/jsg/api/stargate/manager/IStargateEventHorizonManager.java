package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.traveller.IStargateTeleporter;
import dev.tauri.jsg.api.stargate.traveller.IStargateTraveller;
import dev.tauri.jsg.api.stargate.traveller.TravellerSendResult;
import dev.tauri.jsg.api.util.ITickable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;

public interface IStargateEventHorizonManager extends INBTSerializable<CompoundTag>, ITickable {
    TravellerSendResult send(IStargateTraveller<?> traveller);

    void receive(IStargateTraveller<?> traveller);

    IStargateTraveller<?> getTraveller(Entity entity);

    IStargateTraveller<?> getTraveller(Entity entity, Vec3 originalMotion);

    IStargateTraveller<?> getStaticTraveller(Stargate<?> targetGate, Entity entity);

    boolean isMovingTowardsGate(Vec3 motionVec);

    IStargateTeleporter getTeleporter(IStargateTraveller<?> traveller, Consumer<IStargateTraveller<?>> afterPlace);

    boolean canBeSend(IStargateTraveller<?> traveller);

    IStargateTraveller<?> getTraveller(Entity entity, IStargateTraveller<?> traveller);

    IStargateTraveller<?> getTraveller(Entity entity, Vec3 destinationPos, Vec3 originalMotion, Vec3 destinationMotion, float destinationYaw, Stargate<?> sourceGate, Stargate<?> targetGate, boolean isStatic);

    default IStargateTraveller<?> getTraveller(Entity entity, Vec3 destinationPos, Vec3 originalMotion, Vec3 destinationMotion, float destinationYaw, Stargate<?> sourceGate, Stargate<?> targetGate) {
        return getTraveller(entity, destinationPos, originalMotion, destinationMotion, destinationYaw, sourceGate, targetGate, false);
    }
}
