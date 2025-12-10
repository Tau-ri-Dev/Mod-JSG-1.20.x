package dev.tauri.jsg.api.config.ingame.option;

public class JSGConfigEnumEntry {
    public final String name;
    public final int value;

    public JSGConfigEnumEntry(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return name;
    }
}
