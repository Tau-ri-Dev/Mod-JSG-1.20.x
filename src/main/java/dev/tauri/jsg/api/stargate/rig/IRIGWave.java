package dev.tauri.jsg.api.stargate.rig;

import dev.tauri.jsg.core.common.config.IJSONConfigEntry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public interface IRIGWave extends IJSONConfigEntry {
    boolean hasFinished();

    void setup(RandomSource random);

    Entity getNextEntity(ServerLevel level);

    double shouldOpenIrisChance();
}
