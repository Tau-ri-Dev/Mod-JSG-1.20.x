package dev.tauri.jsg.api.event;

import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;

/**
 * Event that posted when stargate is fully closed
 */
public final class StargateClosedEvent extends StargateAbstractEvent {
    public StargateClosedEvent(StargateAbstractBaseBE tile) {
        super(tile);
    }

}
