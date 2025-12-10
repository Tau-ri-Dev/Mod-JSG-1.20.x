package dev.tauri.jsg.api.stargate.result;

public enum StargateConnectionStatus {
    NONE, // no active connection
    PREPARED, // the gates know about each other, the incoming animation is played (or will be in next chevron lock)
    WAITING_FOR_WORMHOLE, // the gates are locked (final chevron on initiating gate is locked) and ready to open
    FULLY, // the gates are fully connected via wormhole - maintain secure connection
    CLOSING; // closing the gate connection

    public boolean none() {
        return this == NONE;
    }

    public boolean full() {
        return this == FULLY;
    }

    public boolean waiting() {
        return this == WAITING_FOR_WORMHOLE;
    }

    public boolean prepared() {
        return this == PREPARED;
    }

    public boolean closing() {
        return this == CLOSING;
    }
}
