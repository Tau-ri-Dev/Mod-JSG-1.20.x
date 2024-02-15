package dev.tauri.jsg.api.event;

import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.stargate.StargateOpenResult;

/**
 * Event that posted when stargate dial failed
 */
public final class StargateDialFailEvent extends StargateAbstractEvent {
    private final StargateOpenResult reason;

    public StargateDialFailEvent(StargateAbstractBaseBE tile, StargateOpenResult result) {
        super(tile);
        this.reason = result;
    }

    public StargateOpenResult getReason(){
        return reason;
    }
}
