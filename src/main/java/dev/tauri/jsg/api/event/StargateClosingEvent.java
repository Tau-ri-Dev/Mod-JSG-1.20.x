package dev.tauri.jsg.api.event;

import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.stargate.StargateClosedReasonEnum;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Event that posted when stargate is trying to close
 * This event is cancelable but you can cancel only if {@link #getReason()} == {@link StargateClosedReasonEnum#REQUESTED}
 */
@Cancelable
public final class StargateClosingEvent extends StargateConnectedAbstractEvent {
    private final StargateClosedReasonEnum reason;

    public StargateClosingEvent(StargateAbstractBaseBE tile, StargateAbstractBaseBE targetTile, boolean initiating, StargateClosedReasonEnum reason) {
        super(tile, targetTile, initiating);
        this.reason = reason;
    }

    public StargateClosedReasonEnum getReason() {
        return reason;
    }
}
