package dev.tauri.jsg.api.stargate;

import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.type.StargateType;

public class NearbyGate {
    public StargateAddress address;
    public int symbolsNeeded;
    public StargateType gateType;

    public NearbyGate(StargateAddress address, int symbolsNeeded, StargateType gateType) {
        this.address = address;
        this.symbolsNeeded = symbolsNeeded;
        this.gateType = gateType;
    }

    public int[] getSymbolsToDisplay() {
        var result = new int[symbolsNeeded];
        for (var i = 1; i < symbolsNeeded; i++) {
            result[i - 1] = i;
        }
        result[symbolsNeeded - 1] = 9;
        return result;
    }
}
