package dev.tauri.jsg.api.event;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;

/**
 * Event that posted when stargate dial failed
 */
public final class StargateDialFailEvent extends StargateAbstractEvent {
    private final StargateOpenResult reason;

    public StargateDialFailEvent(Stargate<?> tile, StargateOpenResult result) {
        super(tile);
        this.reason = result;
    }

    public StargateOpenResult getReason() {
        return reason;
    }
}
