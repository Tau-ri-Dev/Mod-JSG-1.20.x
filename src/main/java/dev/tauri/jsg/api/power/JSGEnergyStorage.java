package dev.tauri.jsg.api.power;

import net.minecraftforge.energy.EnergyStorage;

public abstract class JSGEnergyStorage extends EnergyStorage {
    public JSGEnergyStorage(int capacity) {
        super(capacity);
    }

    public JSGEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public JSGEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public JSGEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public void setEnergyStored(int energyStored) {
        this.energy = Math.min(energyStored, capacity);
        onEnergyChanged();
    }

    protected void onEnergyChanged() {}
}
