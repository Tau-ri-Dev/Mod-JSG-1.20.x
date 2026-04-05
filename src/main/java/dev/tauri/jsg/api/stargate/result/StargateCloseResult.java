package dev.tauri.jsg.api.stargate.result;

public enum StargateCloseResult {
    OK,
    NOT_OPEN,
    CANNOT_DISENGAGE,
    NOT_INITIATING,
    BLOCKED_BY_EVENT;

    public boolean ok() {
        return this == OK;
    }
}
