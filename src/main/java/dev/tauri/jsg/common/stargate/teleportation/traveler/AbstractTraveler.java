package dev.tauri.jsg.common.stargate.teleportation.traveler;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.traveler.IStargateTraveler;

public abstract class AbstractTraveler<E> implements IStargateTraveler<E> {
    protected final Stargate<?> sourceGate;
    protected final Stargate<?> receivingGate;
    protected final boolean isStatic;

    public AbstractTraveler(Stargate<?> sourceGate, Stargate<?> receivingGate, boolean isStatic) {
        this.sourceGate = sourceGate;
        this.receivingGate = receivingGate;
        this.isStatic = isStatic;
    }

    @Override
    public Stargate<?> getTransmitter() {
        return sourceGate;
    }

    @Override
    public Stargate<?> getReceiver() {
        return receivingGate;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }
}
