package dev.tauri.jsg.api.dialhomedevice.upgrade;

import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IDHDUpgradeBehavior extends ITickable, INBTSerializable<CompoundTag> {
    void onAttach(Level level);
    void onDetach(Level level);

    long modifyEnergyPerNaquadah(long baseEnergy);

    boolean onSymbolActivated(SymbolInterface symbol);

    boolean onSymbolButtonPushed(SymbolInterface symbol, @Nullable ServerPlayer player, boolean force);

    int modifyMaxSymbolsInAddress(int symbolsCount, StargateAddressDynamic dialedAddress);

    class Builder {
        public IDHDUpgradeBehavior build() {
            return new DHDUpgradeBehaviorImpl();
        }
    }
}
