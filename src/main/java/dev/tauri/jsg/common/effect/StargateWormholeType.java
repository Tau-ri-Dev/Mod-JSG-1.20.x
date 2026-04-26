package dev.tauri.jsg.common.effect;

import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.Stargate;

import javax.annotation.Nonnull;

public enum StargateWormholeType {
    MILKYWAY(117, 154),
    PEGASUS(104, 143),
    UNIVERSE(117, 154);

    public final int startFrame;
    public final int endFrame;

    StargateWormholeType(int startFrame, int endFrame) {
        this.startFrame = startFrame;
        this.endFrame = endFrame;
    }

    @Nonnull
    public static StargateWormholeType fromTileEntity(Stargate<?> stargate) {
        if (stargate.getSymbolType() == JSGSymbolTypes.UNIVERSE.get()) return UNIVERSE;
        if (stargate.getSymbolType() == JSGSymbolTypes.PEGASUS.get()) return PEGASUS;
        return MILKYWAY;
    }
}
