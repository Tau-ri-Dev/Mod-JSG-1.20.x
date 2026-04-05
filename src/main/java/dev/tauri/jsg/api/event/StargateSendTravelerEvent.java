package dev.tauri.jsg.api.event;

import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.traveler.IStargateTraveler;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public final class StargateSendTravelerEvent extends StargateConnectedAbstractEvent {
    private final IStargateTraveler<?> traveler;
    private StargatePos redirectTarget = null;

    public StargateSendTravelerEvent(Stargate<?> stargate, Stargate<?> targetTile, IStargateTraveler<?> traveler) {
        super(stargate, targetTile, true);
        this.traveler = traveler;
    }

    /**
     * Get entity that will be teleported
     *
     * @return entity that will be teleported
     */
    public IStargateTraveler<?> getTraveler() {
        return traveler;
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
