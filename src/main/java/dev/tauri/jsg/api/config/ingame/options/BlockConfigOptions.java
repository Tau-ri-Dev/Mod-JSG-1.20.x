package dev.tauri.jsg.api.config.ingame.options;

import dev.tauri.jsg.api.config.ingame.JSGConfigOption;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public interface BlockConfigOptions extends Supplier<JSGConfigOption<?>> {
    @Override
    @Nonnull
    JSGConfigOption<?> get();
}
