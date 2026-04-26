package dev.tauri.jsg.common.stargate.teleportation;

import dev.tauri.jsg.api.stargate.traveler.IStargateTeleporter;
import dev.tauri.jsg.api.stargate.traveler.IStargateTraveler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;

import java.util.function.Consumer;
import java.util.function.Function;

public class StargateTeleporter implements IStargateTeleporter {
    protected IStargateTraveler<?> traveler;
    protected final Consumer<IStargateTraveler<?>> afterPlace;

    public StargateTeleporter(IStargateTraveler<?> traveler, Consumer<IStargateTraveler<?>> afterPlace) {
        this.traveler = traveler;
        this.afterPlace = afterPlace;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel sourceLevel, ServerLevel targetLevel, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity e = repositionEntity.apply(false);
        if (e == null) return null;
        traveler = traveler.getTransmitter().getEventHorizonManager().getTraveler(e, traveler);
        traveler.getReceiver().getEventHorizonManager().receive(traveler);
        afterPlace.accept(traveler);
        return e;
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel targetLevel, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return new PortalInfo(
                traveler.getDestinationPos(),
                traveler.getDestinationMotion(),
                traveler.getDestinationYaw(),
                entity.getXRot());
    }

    @Override
    public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceLevel, ServerLevel targetLevel) {
        return false;
    }

    @Override
    public boolean isVanilla() {
        return false;
    }
}
