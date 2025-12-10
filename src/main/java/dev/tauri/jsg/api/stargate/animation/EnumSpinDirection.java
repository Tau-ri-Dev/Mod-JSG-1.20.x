package dev.tauri.jsg.api.stargate.animation;

import dev.tauri.jsg.api.stargate.network.address.symbol.SymbolInterface;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public enum EnumSpinDirection {
    COUNTER_CLOCKWISE(0, -1),
    CLOCKWISE(1, 1);

    public int id;
    public int mul;

    private EnumSpinDirection(int id, int mul) {
        this.id = id;
        this.mul = mul;
    }

    public EnumSpinDirection opposite() {
        if (this == CLOCKWISE)
            return COUNTER_CLOCKWISE;

        else
            return CLOCKWISE;
    }

    public float getDistance(SymbolInterface currentRingSymbol, SymbolInterface targetRingSymbol) {
        int indexDiff;

        if (this == CLOCKWISE)
            indexDiff = currentRingSymbol.getAngleIndex() - targetRingSymbol.getAngleIndex();
        else
            indexDiff = targetRingSymbol.getAngleIndex() - currentRingSymbol.getAngleIndex();

        float angle = indexDiff * currentRingSymbol.getSymbolType().getAnglePerGlyph();

        if (angle < 0)
            angle += 360;

        return angle;
    }

    public static EnumSpinDirection valueOf(int id) {
        return id == 0 ? COUNTER_CLOCKWISE : CLOCKWISE;
    }

    public static EnumSpinDirection random(@NotNull Random random) {
        return EnumSpinDirection.values()[random.nextInt(2)];
    }
}
