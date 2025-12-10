package dev.tauri.jsg.api.stargate;

import dev.tauri.jsg.api.util.EnumKeyInterface;
import dev.tauri.jsg.api.util.EnumKeyMap;

import java.util.HashMap;
import java.util.Map;

public enum ChevronEnum implements EnumKeyInterface<Integer> {
    C1(0, 1, 0), C2(1, 2, 0), C3(2, 3, 0),

    C4(3, 6, 0), C5(4, 7, 0), C6(5, 8, 0),

    C7(6, 4, 1), C8(7, 5, 2),

    C9(8, 0, 0);

    public final int index;
    public final int rotation;
    public final int rotationIndex;
    public final int additionalIndex;

    ChevronEnum(int index, int rotationIndex, int additionalIndex) {
        this.index = index;
        this.rotationIndex = rotationIndex;
        this.rotation = -40 * rotationIndex;
        this.additionalIndex = additionalIndex;
    }

    public boolean isFinal() {
        return this == C9;
    }

    public static ChevronEnum getFinal() {
        return C9;
    }

    public ChevronEnum getNext() {
        if (isFinal()) throw new IllegalStateException("Requested next chevron, while chevron was already final.");

        return valueOf(index + 1);
    }

    private static final EnumKeyMap<Integer, ChevronEnum> ID_MAP = new EnumKeyMap<>(values());
    private static final Map<Integer, ChevronEnum> ROTATION_MAP = new HashMap<>();

    @Override
    public Integer getKey() {
        return index;
    }

    public static ChevronEnum valueOf(int index) {
        return ID_MAP.valueOf(index);
    }

    public static ChevronEnum fromRotationIndex(int index) {
        if (ROTATION_MAP.isEmpty()) {
            for (var v : values()) {
                ROTATION_MAP.put(v.rotationIndex, v);
            }
        }
        return ROTATION_MAP.get(index);
    }
}
