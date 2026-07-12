package dev.tauri.jsg.common.state.energy;

import dev.tauri.jsg.common.item.energy.ZPMItemBlock;
import dev.tauri.jsg.core.common.util.ItemNBT;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public enum ZPMModelType {
    NORMAL(0, ""),
    CREATIVE(1, "_creative"),
    EXPLOSIVE(2, "");

    public final int id;
    public final String suffix;

    ZPMModelType(int id, String suffix) {
        this.id = id;
        this.suffix = suffix;
    }

    @Nullable
    public static ZPMModelType byId(int id) {
        for (ZPMModelType z : ZPMModelType.values()) {
            if (z.id == id)
                return z;
        }
        return null;
    }

    public static ZPMModelType byStack(ItemStack stack) {
        if (stack.getItem() instanceof ZPMItemBlock zpmItem && zpmItem.isCreative())
            return CREATIVE;

        var tag = ItemNBT.getTag(stack);
        if (tag != null && tag.getBoolean("corrupted"))
            return EXPLOSIVE;

        return NORMAL;
    }
}
