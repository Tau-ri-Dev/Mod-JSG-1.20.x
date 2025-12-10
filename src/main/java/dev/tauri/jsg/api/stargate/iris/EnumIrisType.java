package dev.tauri.jsg.api.stargate.iris;


import dev.tauri.jsg.api.item.IIrisItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public enum EnumIrisType {
    NULL((byte) 0),

    IRIS_TITANIUM((byte) 1),
    IRIS_TRINIUM((byte) 2),

    SHIELD((byte) 3),

    IRIS_CREATIVE((byte) 4);

    public final byte id;

    EnumIrisType(byte id) {
        this.id = id;
    }


    public static EnumIrisType byId(byte id) {
        return (id < values().length) ? values()[id] : NULL;
    }

    @NotNull
    public static EnumIrisType byItem(Item item) {
        if (item instanceof IIrisItem iris) return Optional.ofNullable(iris.getType()).orElse(NULL);
        return NULL;
    }
}
