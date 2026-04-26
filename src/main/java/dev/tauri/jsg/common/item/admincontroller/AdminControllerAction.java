package dev.tauri.jsg.common.item.admincontroller;

import dev.tauri.jsg.api.stargate.animation.EnumDialingType;

public enum AdminControllerAction {
    SLOW_DIAL,
    FAST_DIAL,
    NOX_DIAL,
    ADDRESS_GIVE,
    TELEPORT,

    TOGGLE_IRIS,
    ABORT_DIALING,
    CLOSE_GATE;

    public EnumDialingType getDialingType() {
        return switch (this) {
            case FAST_DIAL -> EnumDialingType.FAST;
            case NOX_DIAL -> EnumDialingType.NOX;
            default -> EnumDialingType.NORMAL;
        };
    }
}
