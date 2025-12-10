package dev.tauri.jsg.api.stargate.result;

public enum StargateChevronEngageResult {
    OK,
    BUSY,
    ALREADY_ENGAGED,
    ADDRESS_FULL,
    BLOCKED_BY_EVENT,
    FAILED_FAIL_GATE,
    OK_CONNECTED;

    public boolean ok() {
        return this == OK || this == OK_CONNECTED;
    }
}
