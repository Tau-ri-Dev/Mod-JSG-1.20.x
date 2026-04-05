package dev.tauri.jsg.api.client.screen;

import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;

import javax.annotation.Nullable;
import java.util.Random;

public enum EnumMainMenuGateType {
    MILKYWAY,
    UNIVERSE,
    PEGASUS,
    BY_ACT;

    public static EnumMainMenuGateType byGateType(StargateType<?> type) {
        if (type == StargateTypes.PEGASUS.get()) {
            return PEGASUS;
        }
        if (type == StargateTypes.UNIVERSE.get()) {
            return UNIVERSE;
        }
        return MILKYWAY;
    }

    public static EnumMainMenuGateType random(@Nullable EnumMainMenuGateType previousType) {
        EnumMainMenuGateType newType;
        do {
            int i = new Random().nextInt(3);
            newType = switch (i) {
                case 1 -> PEGASUS;
                case 2 -> UNIVERSE;
                default -> MILKYWAY;
            };
        } while (newType == previousType);
        return newType;
    }
}