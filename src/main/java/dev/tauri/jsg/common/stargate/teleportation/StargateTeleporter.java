package dev.tauri.jsg.common.stargate.teleportation;

import dev.tauri.jsg.api.stargate.traveler.IStargateTeleporter;
import dev.tauri.jsg.api.stargate.traveler.IStargateTraveler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;

import java.util.function.Consumer;

public class StargateTeleporter implements IStargateTeleporter {
    protected IStargateTraveler<?> traveler;
    protected final Consumer<IStargateTraveler<?>> afterPlace;

    public StargateTeleporter(IStargateTraveler<?> traveler, Consumer<IStargateTraveler<?>> afterPlace) {
        this.traveler = traveler;
        this.afterPlace = afterPlace;
    }

    @Override
    public DimensionTransition createTransition(Entity entity, ServerLevel targetLevel) {
        return new DimensionTransition(
                targetLevel,
                traveler.getDestinationPos(),
                traveler.getDestinationMotion(),
                traveler.getDestinationYaw(),
                entity.getXRot(),
                placed -> {
                    // The entity may have been recreated during the dimension change - rebind the traveler.
                    traveler = traveler.getTransmitter().getEventHorizonManager().getTraveler(placed, traveler);
                    traveler.getReceiver().getEventHorizonManager().receive(traveler);
                    afterPlace.accept(traveler);
                });
    }
}
