package dev.tauri.jsg.api.event;

import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;

/**
 * Event that posted when stargate is fully opened
 */
public final class StargateOpenedEvent extends StargateConnectedAbstractEvent {

    public StargateOpenedEvent(StargateAbstractBaseBE tile, StargateAbstractBaseBE targetTile, boolean initiating) {
        super(tile, targetTile, initiating);
    }


}
