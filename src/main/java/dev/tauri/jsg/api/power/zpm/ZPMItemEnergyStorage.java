package dev.tauri.jsg.api.power.zpm;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ZPMItemEnergyStorage implements IEnergyStorageZPM {
    private final ItemStack stack;
    protected final long maxEnergyStored;

    public ZPMItemEnergyStorage(ItemStack stack, long maxEnergyStored) {
        this.stack = stack;
        this.maxEnergyStored = maxEnergyStored;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;
        if (maxExtract > getEnergyStored()) maxExtract = (int) getEnergyStored();
        if (!simulate) {
            setEnergyStored(getEnergyStored() - maxExtract);
        }
        return maxExtract;
    }

    public void setEnergyStored(long energy) {
        getOrCreateCompound(stack).putLong("longEnergy", energy);
    }

    @Override
    public long getEnergyStored() {
        CompoundTag tag = getOrCreateCompound(stack);
        if (tag.contains("energy")) {
            return tag.getInt("energy");
        }
        return tag.getLong("longEnergy");
    }

    @Override
    public long getMaxEnergyStored() {
        return maxEnergyStored;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    private CompoundTag getOrCreateCompound(ItemStack stack) {
        if (!stack.hasTag())
            stack.setTag(new CompoundTag());
        return stack.getTag();
    }
}
