package dev.tauri.jsg.stargate.manager;

import dev.tauri.jsg.api.stargate.Stargate;

public abstract class AbstractStargateManager<SG extends Stargate<?>> {
    public final SG stargate;

    public AbstractStargateManager(SG stargate) {
        this.stargate = stargate;
    }
}
