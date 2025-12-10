package dev.tauri.jsg.api.event;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Event that posted when stargate is trying to close
 * This event is cancelable but you can cancel only if {@link #getReason()} == {@link StargateClosedReasonEnum#REQUESTED}
 */
@Cancelable
public final class StargateClosingEvent extends StargateConnectedAbstractEvent {
    private final StargateClosedReasonEnum reason;

    public StargateClosingEvent(Stargate<?> tile, Stargate<?> targetTile, boolean initiating, StargateClosedReasonEnum reason) {
        super(tile, targetTile, initiating);
        this.reason = reason;
    }

    public StargateClosedReasonEnum getReason() {
        return reason;
    }
}
