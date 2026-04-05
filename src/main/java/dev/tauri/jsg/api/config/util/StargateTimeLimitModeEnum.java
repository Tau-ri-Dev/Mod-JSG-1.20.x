package dev.tauri.jsg.api.config.util;

import javax.annotation.Nonnull;
import java.util.Optional;

public enum StargateTimeLimitModeEnum {
    DISABLED("DISABLED"),
    CLOSE_GATE("CLOSE_GATE"),
    DRAW_MORE_POWER("DRAW_POWER");
    public final String name;

    StargateTimeLimitModeEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Nonnull
    public static StargateTimeLimitModeEnum byId(int id) {
        return Optional.of(id).filter(idToGet -> idToGet >= 0 && idToGet < values().length)
                .map(idToGet -> values()[idToGet]).orElse(DISABLED);
    }
}
