package dev.tauri.jsg.api.event;

import dev.tauri.jsg.api.stargate.Stargate;

/**
 * Event that posted when stargate is opening
 * This event is cancelable. You can cancel it and stargate will not open
 */
public final class StargateOpeningEvent extends StargateConnectedAbstractEvent {

    public StargateOpeningEvent(Stargate<?> tile, Stargate<?> targetTile, boolean initiating) {
        super(tile, targetTile, initiating);
    }


}
