package dev.tauri.jsg.api.item;

public interface ICreativeThing {
    default boolean isCreativeOnly() {
        return true;
    }
}
