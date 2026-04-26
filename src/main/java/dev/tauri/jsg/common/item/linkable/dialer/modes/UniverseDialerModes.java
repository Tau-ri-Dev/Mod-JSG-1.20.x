package dev.tauri.jsg.common.item.linkable.dialer.modes;

@SuppressWarnings("unused")
public class UniverseDialerModes {
    public static final UDNearbyMode NEARBY = new UDNearbyMode();
    public static final UDMemoryMode MEMORY = new UDMemoryMode();
    public static final UDStatusMode STATUS = new UDStatusMode();
    public static final UDManualDialMode MANUAL_DIALING = new UDManualDialMode();

    public static void init() {
    }
}
