package dev.tauri.jsg.common.stargate.manager;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPoOManager;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;

public class StargatePoOManager extends AbstractStargateManager<Stargate<?>> implements IPoOManager {
    public StargatePoOManager(Stargate<?> stargate) {
        super(stargate);
    }

    @Override
    public PointOfOrigin getOrigin() {
        return null;
    }
}
