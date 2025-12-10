package dev.tauri.jsg.api.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * Modified version of {@link ItemStackHandler}.
 * Respects resizing of the item handlers.
 */
public class JSGItemStackHandler extends ItemStackHandler {

    private int size;

    public JSGItemStackHandler(int size) {
        super(size);
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        super.setSize(size);
        this.size = size;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = getStackInSlot(slot);
        //if (!CreativeItemsChecker.canInteractWith(stack, false)) return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }
}
