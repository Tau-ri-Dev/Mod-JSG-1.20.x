package dev.tauri.jsg.api.power.general;

import dev.tauri.jsg.api.power.JSGEnergyStorage;
import dev.tauri.jsg.api.config.JSGConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public class SmallEnergyStorage extends JSGEnergyStorage implements INBTSerializable<Tag> {

	public SmallEnergyStorage() {
		super(JSGConfig.Stargate.stargateEnergyStorage.get()/4, JSGConfig.Stargate.stargateMaxEnergyTransfer.get(), 0);
	}

	public SmallEnergyStorage(int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
	}

	public SmallEnergyStorage(int capacity) {
		super(capacity, JSGConfig.Stargate.stargateMaxEnergyTransfer.get());
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tagCompound = new CompoundTag();

		tagCompound.putInt("energy", this.energy);

		return tagCompound;
	}

    @Override
	public void deserializeNBT(Tag nbt) {
        if(nbt instanceof CompoundTag tag){
            deserializeNBT(tag);
        }
    }
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt != null) {
			if (nbt.contains("energy")) {
				this.energy = nbt.getInt("energy");
			}
		}
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int energyReceived = super.receiveEnergy(maxReceive, simulate);

		if (energyReceived > 0)
			onEnergyChanged();

		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {

		if (!simulate) {
			energy -= maxExtract;
			if(energy < 0) energy = 0;
			onEnergyChanged();
		}
		else if (maxExtract > energy) {
			maxExtract = energy;
		}
		return maxExtract;
	}

    public int receiveEnergyInternal(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate && energyReceived > 0) {
			energy += energyReceived;
			onEnergyChanged();
		}
        return energyReceived;
    }
}
