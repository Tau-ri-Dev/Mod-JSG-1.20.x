package dev.tauri.jsg.api.event;

import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Event that posted when entity teleported through stargate
 * You can redirect entity to another <b>active</b> stargate with {@link #redirectTo(StargatePos)} or back to itself
 * This event is cancelable. You can cancel teleportation
 */
@Cancelable
public final class StargateTeleportEntityEvent extends StargateConnectedAbstractEvent {
    private final Entity entity;
    private StargatePos redirectTarget = null;

    public StargateTeleportEntityEvent(Stargate<?> tile, Stargate<?> targetTile, Entity entity) {
        super(tile, targetTile, true);
        this.entity = entity;
    }

    /**
     * Get entity that will be teleported
     *
     * @return entity that will be teleported
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Check if teleportation is redirected
     *
     * @return true if redirected, false if not
     */
    public boolean isRedirected() {
        return redirectTarget != null;
    }

    /**
     * Get current redirect target
     *
     * @return redirect target
     */
    public StargatePos getRedirectTarget() {
        return redirectTarget;
    }

    /*public void redirectToSelf(){
        redirectTo(new StargatePos(tile.getWorld().provider.getDimension(), tile.getPos(), tile.getStargateAddress(tile.getSymbolType())));
    }*/

    /**
     * Redirect entity to another dialed stargate
     *
     * @param pos stargate position.
     */
    public void redirectTo(StargatePos pos) {
        if (pos.getStargate().getDialingManager().getStargateState().equals(EnumStargateState.ENGAGED))
            redirectTarget = pos;
    }
}
