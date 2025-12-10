package dev.tauri.jsg.api.stargate.result;

public enum StargateConnectResult {
    OK,
    NOT_ENOUGH_POWER,
    ADDRESS_MALFORMED,
    ADDRESS_MALFORMED_SGN_OK,
    BUSY,
    ALREADY_CONNECTED,
    TARGET_BUSY,
    GATE_BURRIED,
    TARGET_GATE_BURRIED;

    public boolean ok() {
        return this == OK;
    }

    public StargateConnectResult reverse() {
        if (this == TARGET_GATE_BURRIED) return GATE_BURRIED;
        if (this == TARGET_BUSY) return BUSY;
        return this;
    }
}
