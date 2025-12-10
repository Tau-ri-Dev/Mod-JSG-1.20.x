package dev.tauri.jsg.api.power.general;

public class CreativeEnergyStorage extends SmallEnergyStorage {

    public CreativeEnergyStorage() {
        super();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return maxExtract;
    }

    @Override
    public int receiveEnergyInternal(int maxReceive, boolean simulate) {
        return maxReceive;
    }

    @Override
    public int getEnergyStored() {
        return getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    public void setEnergyStored(int energyStored) {
    }
}
