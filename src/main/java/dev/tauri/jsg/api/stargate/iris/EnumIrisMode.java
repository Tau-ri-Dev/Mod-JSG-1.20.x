package dev.tauri.jsg.api.stargate.iris;

/**
 * @author matousss
 */
public enum EnumIrisMode {
    OPENED((byte) 0),
    CLOSED((byte) 1),
    AUTO((byte) 2),
    OC((byte) 3),
    DIALER((byte) 4);

    public final byte id;

    EnumIrisMode(byte id) {
        this.id = id;
    }

    public boolean canBeOpenedByRIG() {
        return (this == AUTO || this == OC || this == DIALER);
    }

    public static EnumIrisMode getValue(byte id) {
        if (id > values().length || id < 0) throw new IllegalArgumentException("ID not set");
        return EnumIrisMode.values()[id];
    }
}
