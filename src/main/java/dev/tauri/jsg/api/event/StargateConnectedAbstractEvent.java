package dev.tauri.jsg.api.event;

import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.stargate.network.StargateAddress;
import dev.tauri.jsg.stargate.network.SymbolTypeEnum;

/**
 * Parent event for all stargate events with connected stargates
 */
public abstract class StargateConnectedAbstractEvent extends StargateAbstractEvent {
    private final StargateAbstractBaseBE targetTile;
    private final boolean initiating;

    public StargateConnectedAbstractEvent(StargateAbstractBaseBE tile, StargateAbstractBaseBE targetTile, boolean initiating) {
        super(tile);
        this.targetTile = targetTile;
        this.initiating = initiating;
    }

    /**
     * Get target stargate
     * @return target stargate tileentity
     */
    public StargateAbstractBaseBE getTargetTile() {
        return targetTile;
    }

    /**
     * Get address of target stargate
     * @return stargate address by stargate type
     */
    public StargateAddress getTargetAddress(){
        return targetTile.getStargateAddress(targetTile.getSymbolType());
    }

    /**
     * Get address of target stargate
     * @param type address type
     * @return stargate address
     */
    public StargateAddress getTargetAddress(SymbolTypeEnum type){
        return targetTile.getStargateAddress(type);
    }

    /**
     * Is {@link #getTile()} initiating stargate or not
     * @return true if yes, false if no
     */
    public boolean isInitiating() {
        return initiating;
    }
}
