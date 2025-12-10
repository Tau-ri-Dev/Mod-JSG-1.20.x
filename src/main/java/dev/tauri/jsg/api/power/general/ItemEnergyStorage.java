package dev.tauri.jsg.api.power.general;

import dev.tauri.jsg.api.config.JSGConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class ItemEnergyStorage implements IEnergyStorage {
    protected final ItemStack stack;
    protected final int maxEnergyStored;

    public ItemEnergyStorage(ItemStack stack, int maxEnergyStored) {
        this.stack = stack;
        this.maxEnergyStored = maxEnergyStored;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyStored = getEnergyStored();
        int energyReceived = Math.min(getMaxEnergyStored() - energyStored, Math.min(JSGConfig.Stargate.stargateMaxEnergyTransfer.get(), maxReceive));
        if (!simulate)
            setEnergyStored(energyStored + energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract > getEnergyStored()) maxExtract = getEnergyStored();
        if (!simulate) {
            setEnergyStored(getEnergyStored() - maxExtract);
        }
        return maxExtract;
    }

    public void setEnergyStored(int energy){
        getOrCreateCompound(stack).putInt("energy", energy);
    }

    @Override
    public int getEnergyStored() {
        return getOrCreateCompound(stack).getInt("energy");
    }

    @Override
    public int getMaxEnergyStored() {
        return maxEnergyStored;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    protected CompoundTag getOrCreateCompound(ItemStack stack) {
        if(!stack.hasTag())
            stack.setTag(new CompoundTag());
        return stack.getTag();
    }
}
