package dev.tauri.jsg.api.event;

import dev.tauri.jsg.api.stargate.Stargate;

/**
 * Event that posted when stargate is fully closed
 */
public final class StargateClosedEvent extends StargateAbstractEvent {
    public StargateClosedEvent(Stargate<?> tile) {
        super(tile);
    }

}
