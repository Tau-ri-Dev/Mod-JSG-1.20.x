package dev.tauri.jsg.api.dialhomedevice.upgrade;

import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class DHDUpgradeBehaviorImpl implements IDHDUpgradeBehavior {
    @Override
    public void onAttach(Level level) {

    }

    @Override
    public void onDetach(Level level) {

    }

    @Override
    public long modifyEnergyPerNaquadah(long baseEnergy) {
        return 0;
    }

    @Override
    public boolean onSymbolActivated(SymbolInterface symbol) {
        return false;
    }

    @Override
    public boolean onSymbolButtonPushed(SymbolInterface symbol, @Nullable ServerPlayer player, boolean force) {
        return false;
    }

    @Override
    public int modifyMaxSymbolsInAddress(int symbolsCount, StargateAddressDynamic dialedAddress) {
        return 0;
    }

    @Override
    public void tick(@NotNull Level level) {

    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
