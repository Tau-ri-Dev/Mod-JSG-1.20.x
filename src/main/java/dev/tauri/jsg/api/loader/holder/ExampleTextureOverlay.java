package dev.tauri.jsg.api.loader.holder;

import dev.tauri.jsg.api.TextureOverlay;

public enum ExampleTextureOverlay implements TextureOverlay {
    NORMAL(""),
    BROKEN("_broken");

    private final String suffix;

    ExampleTextureOverlay(String suffix){
        this.suffix = suffix;
    }
    @Override
    public String getSuffix() {
        return suffix;
    }
}
