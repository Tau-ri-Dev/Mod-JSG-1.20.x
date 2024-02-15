package dev.tauri.jsg.api.event;

import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Event that posted when stargate is opening
 * This event is cancelable. You can cancel it and stargate will not open
 */
@Cancelable
public final class StargateOpeningEvent extends StargateConnectedAbstractEvent {

    public StargateOpeningEvent(StargateAbstractBaseBE tile, StargateAbstractBaseBE targetTile, boolean initiating) {
        super(tile, targetTile, initiating);
    }


}
