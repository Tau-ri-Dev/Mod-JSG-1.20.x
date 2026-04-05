package dev.tauri.jsg.recipes;

import dev.tauri.jsg.item.linkable.dialer.UniverseDialerItem;
import dev.tauri.jsg.item.linkable.dialer.modes.UDMemoryMode;
import dev.tauri.jsg.item.linkable.dialer.modes.UniverseDialerModes;
import dev.tauri.jsg.registry.JSGItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class DialerRecipeUtils {
    public static final ItemStack DIALER1 = getDialerWithAddresses(1, "Plains");
    public static final ItemStack DIALER2 = getDialerWithAddresses(1, "Tundra");
    public static final ItemStack DIALER_OUT = getDialerWithAddresses(2, "Plains", "Tundra");
    public static final ItemStack DIALER_OUT_PAGE = getDialerWithAddresses(1, "Plains", "End");

    public static ItemStack getDialerWithAddresses(int quantity, String... addressNames) {
        ItemStack stack = new ItemStack(JSGItems.UNIVERSE_DIALER.get(), quantity);
        var compound = new CompoundTag();
        var list = new ListTag();
        var modeTag = new CompoundTag();

        for (String name : addressNames) {
            var nbt = new CompoundTag();
            nbt.putString("name", name);
            list.add(nbt);
        }

        modeTag.put(UDMemoryMode.C_ENTRIES, list);
        compound.put(UniverseDialerModes.MEMORY.id + UniverseDialerItem.C_MODE_TAG, modeTag);
        stack.setTag(compound);

        return stack;
    }
}
