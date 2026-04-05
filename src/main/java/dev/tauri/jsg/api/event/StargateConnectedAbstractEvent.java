package dev.tauri.jsg.api.event;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.core.common.symbol.SymbolType;

/**
 * Parent event for all stargate events with connected stargates
 */
public abstract class StargateConnectedAbstractEvent extends StargateAbstractEvent {
    private final Stargate<?> targetTile;
    private final boolean initiating;

    public StargateConnectedAbstractEvent(Stargate<?> tile, Stargate<?> targetTile, boolean initiating) {
        super(tile);
        this.targetTile = targetTile;
        this.initiating = initiating;
    }

    /**
     * Get target stargate
     *
     * @return target stargate tileentity
     */
    public Stargate<?> getTargetTile() {
        return targetTile;
    }

    /**
     * Get address of target stargate
     *
     * @return stargate address by stargate type
     */
    public StargateAddress getTargetAddress() {
        return targetTile.getStargateAddress(targetTile.getSymbolType());
    }

    /**
     * Get address of target stargate
     *
     * @param type address type
     * @return stargate address
     */
    public StargateAddress getTargetAddress(SymbolType<?> type) {
        return targetTile.getStargateAddress(type);
    }

    /**
     * Is {@link #getTile()} initiating stargate or not
     *
     * @return true if yes, false if no
     */
    public boolean isInitiating() {
        return initiating;
    }
}
