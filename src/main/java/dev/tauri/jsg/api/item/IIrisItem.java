package dev.tauri.jsg.api.item;

import dev.tauri.jsg.api.stargate.iris.EnumIrisType;

public interface IIrisItem {
    boolean isCreative();

    boolean isShield();

    EnumIrisType getType();
}
