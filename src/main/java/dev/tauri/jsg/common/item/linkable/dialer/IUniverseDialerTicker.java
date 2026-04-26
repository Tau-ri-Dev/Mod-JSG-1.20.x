package dev.tauri.jsg.common.item.linkable.dialer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

public interface IUniverseDialerTicker {
    @ParametersAreNonnullByDefault
    void inventoryTick(ItemStack stack, CompoundTag compoundTag, Level world, Entity entity, int itemSlot, boolean isSelected);
}
