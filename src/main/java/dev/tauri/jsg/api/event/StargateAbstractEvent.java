package dev.tauri.jsg.api.event;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * Parent event for all stargate-related events in JSG
 */
public abstract class StargateAbstractEvent extends Event {
    protected final Stargate<?> tile;

    public StargateAbstractEvent(Stargate<?> tile) {
        this.tile = tile;
    }

    /**
     * Get stargate that posted this event
     *
     * @return stargate tileentity
     */
    public Stargate<?> getTile() {
        return tile;
    }

    /**
     * Get address of stargate that posted this event
     *
     * @return stargate address by stargate type
     */
    public StargateAddress getAddress() {
        return getAddress(tile.getSymbolType());
    }

    /**
     * Get address of stargate that posted this event
     *
     * @param type address type
     * @return stargate address
     */
    public StargateAddress getAddress(SymbolType<?> type) {
        return tile.getStargateAddress(type);
    }

    /**
     * Post event to MinecraftForge.EVENT_BUS. Internal use only
     *
     * @return true if event canceled, false if not
     */
    public boolean post() {
        return MinecraftForge.EVENT_BUS.post(this);
    }
}
