package dev.tauri.jsg.api.event;

import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.stargate.network.SymbolInterface;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Event that posted on chevron lock
 * This event is cancelable, you can cancel it and chevron will not lock
 */
@Cancelable
public final class StargateChevronEngagedEvent extends StargateAbstractEvent {
    private final SymbolInterface symbol;
    private final boolean lastSymbol;

    public StargateChevronEngagedEvent(StargateAbstractBaseBE tile, SymbolInterface symbol, boolean lastSymbol) {
        super(tile);
        this.symbol = symbol;
        this.lastSymbol = lastSymbol;
    }

    public SymbolInterface getSymbol() {
        return symbol;
    }

    public boolean isLastSymbol() {
        return lastSymbol;
    }
}
