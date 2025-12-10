package dev.tauri.jsg.api.event;

import dev.tauri.jsg.api.stargate.Stargate;

/**
 * Event that posted when stargate is fully opened
 */
public final class StargateOpenedEvent extends StargateConnectedAbstractEvent {

    public StargateOpenedEvent(Stargate<?> tile, Stargate<?> targetTile, boolean initiating) {
        super(tile, targetTile, initiating);
    }
}
