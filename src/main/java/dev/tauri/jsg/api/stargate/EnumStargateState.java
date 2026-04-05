package dev.tauri.jsg.api.stargate;

public enum EnumStargateState {
    IDLE,
    DIALING,
    DIALING_COMPUTER,
    DIALING_REMOTE,
    ENGAGED,
    ENGAGED_INITIATING,
    UNSTABLE_OPENING,
    UNSTABLE_CLOSING,
    FAILING,
    RESETTING,
    INCOMING;

    public boolean idle() {
        return this == IDLE;
    }

    public boolean incoming() {
        return this == INCOMING;
    }

    public boolean engaged() {
        return this == ENGAGED || this == ENGAGED_INITIATING;
    }

    public boolean initiating() {
        return this == ENGAGED_INITIATING;
    }

    public boolean notInitiating() {
        return this == ENGAGED;
    }

    public boolean dialingComputer() {
        return this == DIALING_COMPUTER;
    }

    public boolean failing() {
        return this == FAILING;
    }

    public boolean resetting() {
        return this == RESETTING;
    }

    public boolean dialing() {
        return this == DIALING || this == DIALING_COMPUTER || this == DIALING_REMOTE;
    }

    public boolean dialingDHD() {
        return this == DIALING;
    }

    public boolean dialingRemote() {
        return this == DIALING_REMOTE;
    }

    public boolean unstable() {
        return this == UNSTABLE_OPENING || this == UNSTABLE_CLOSING;
    }

    public static EnumStargateState valueOf(int id) {
        if (id < 0 || id >= values().length) return IDLE;
        return values()[id];
    }
}
