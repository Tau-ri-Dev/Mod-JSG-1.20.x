package dev.tauri.jsg.api.util;

import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;

public class GeneratedStargate {
    public final StargateAddress address;
    public final String biomePath;
    public final boolean hasUpgrade;
    public final PointOfOrigin origin;

    public GeneratedStargate(StargateAddress address, String biomePath, boolean upgrade, PointOfOrigin origin) {
        this.address = address;
        this.biomePath = biomePath;
        this.hasUpgrade = upgrade;
        this.origin = origin;
    }
}
