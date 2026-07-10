package dev.tauri.jsg.api.stargate.traveler;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;

/**
 * Builds the {@link DimensionTransition} used to move a traveler through a stargate
 * into another dimension (1.20.1 ITeleporter replacement).
 */
public interface IStargateTeleporter {
    DimensionTransition createTransition(Entity entity, ServerLevel targetLevel);
}
