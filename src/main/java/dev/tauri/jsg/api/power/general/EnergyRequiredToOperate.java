package dev.tauri.jsg.api.power.general;

import dev.tauri.jsg.api.config.JSGConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class EnergyRequiredToOperate implements INBTSerializable<CompoundTag> {

    public int energyToOpen;
    public int keepAlive;

    public EnergyRequiredToOperate(int energyToOpen, int keepAlive) {
        this.energyToOpen = energyToOpen;
        this.keepAlive = keepAlive;
    }

    public static EnergyRequiredToOperate stargate() {
        return new EnergyRequiredToOperate(JSGConfig.Stargate.openingBlockToEnergyRatio.get(), JSGConfig.Stargate.keepAliveBlockToEnergyRatioPerTick.get());
    }

    public static EnergyRequiredToOperate free() {
        return new EnergyRequiredToOperate(0, 0);
    }

    public EnergyRequiredToOperate(double energyToOpen, double keepAlive) {
        this((int) energyToOpen, (int) keepAlive);
    }

    @Override
    public String toString() {
        return "[open=" + energyToOpen + ", keepAlive=" + keepAlive + "]";
    }

    public EnergyRequiredToOperate mul(double mul) {
        return new EnergyRequiredToOperate(energyToOpen * mul, keepAlive * mul);
    }

    public EnergyRequiredToOperate add(EnergyRequiredToOperate add) {
        return new EnergyRequiredToOperate(energyToOpen + add.energyToOpen, keepAlive + add.keepAlive);
    }

    public EnergyRequiredToOperate cap(int max) {
        return new EnergyRequiredToOperate(Math.min(energyToOpen, max), keepAlive);
    }

    public EnergyRequiredToOperate update(EnergyRequiredToOperate updated) {
        this.keepAlive = updated.keepAlive;
        this.energyToOpen = updated.energyToOpen;
        return this;
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putInt("keepAlive", keepAlive);
        compound.putInt("energyToOpen", energyToOpen);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        keepAlive = compound.getInt("keepAlive");
        energyToOpen = compound.getInt("energyToOpen");
    }
}
