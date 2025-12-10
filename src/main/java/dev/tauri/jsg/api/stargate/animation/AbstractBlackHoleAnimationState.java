package dev.tauri.jsg.api.stargate.animation;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.state.State;
import dev.tauri.jsg.api.util.ITickable;

public abstract class AbstractBlackHoleAnimationState extends State implements ITickable {
    public final Stargate<?> stargate;
    public boolean isConnectedToBlackHole = false;
    public boolean isIncoming = false;
    public long isConnectedToBlackHoleFrom = -1;

    public AbstractBlackHoleAnimationState(Stargate<?> stargate) {
        this.stargate = stargate;
    }

    public abstract float getGravitationalFieldStrength();

    public abstract float getBackVortexRed();

    public abstract double getBackVortexAngle();

    public abstract float getBackVortexDepth();

    public abstract void sendUpdateToClient();

    public abstract void setConnectedToBlackHole(boolean connected, boolean isIncoming);
}
