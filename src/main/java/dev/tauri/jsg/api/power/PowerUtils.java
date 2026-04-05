package dev.tauri.jsg.api.power;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.core.common.power.general.EnergyRequiredToOperate;
import dev.tauri.jsg.core.common.power.general.ItemEnergyStorage;
import dev.tauri.jsg.core.common.power.general.LargeEnergyStorage;
import dev.tauri.jsg.core.common.power.general.SmallEnergyStorage;
import net.minecraft.world.item.ItemStack;

public class PowerUtils {
    public static LargeEnergyStorage getLarge(Runnable onChanged) {
        return new LargeEnergyStorage(JSGConfig.Stargate.stargateEnergyStorage.get() / 4, JSGConfig.Stargate.stargateMaxEnergyTransfer.get(), 0) {
            @Override
            protected void onEnergyChanged() {
                onChanged.run();
            }
        };
    }

    public static SmallEnergyStorage getSmall(Runnable onChanged) {
        return new SmallEnergyStorage(JSGConfig.Stargate.stargateEnergyStorage.get() / 4, JSGConfig.Stargate.stargateMaxEnergyTransfer.get(), 0) {
            @Override
            protected void onEnergyChanged() {
                onChanged.run();
            }
        };
    }

    public static SmallEnergyStorage getSmall(int capacity) {
        return new SmallEnergyStorage(capacity, JSGConfig.Stargate.stargateMaxEnergyTransfer.get(), 0);
    }

    public static SmallEnergyStorage getSmall() {
        return getSmall(JSGConfig.Stargate.stargateEnergyStorage.get() / 4);
    }

    public static ItemEnergyStorage getItem(ItemStack stack, int capacity) {
        return new ItemEnergyStorage(stack, capacity, JSGConfig.Stargate.stargateMaxEnergyTransfer.get());
    }

    public static EnergyRequiredToOperate stargateConsumption() {
        return new EnergyRequiredToOperate(JSGConfig.Stargate.openingBlockToEnergyRatio.get(), JSGConfig.Stargate.keepAliveBlockToEnergyRatioPerTick.get());
    }
}
