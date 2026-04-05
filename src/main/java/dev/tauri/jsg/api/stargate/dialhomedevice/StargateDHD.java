package dev.tauri.jsg.api.stargate.dialhomedevice;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.core.common.blockentity.*;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.common.util.FluidTank;
import dev.tauri.jsg.core.common.util.IUpgrade;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public interface StargateDHD extends ILinkableBE<Stargate<?>>, ITickable, IPreparable, IUpgradable, IBiomeOverlayProvider {
    FluidTank getFluidHandler();

    Vec3 getBlockPosInFront();

    SymbolType<?> getSymbolType();

    Item getControlCrystal();

    ItemStackHandler getItemStackHandler();

    DHDReactorStateEnum getReactorState();

    boolean hasControlCrystal();

    void clearSymbols();

    void pushSymbolButton(SymbolInterface symbol, @Nullable ServerPlayer player, boolean force);

    void activateSymbol(SymbolInterface symbol);

    default Optional<Stargate<?>> getStargate() {
        if (!isLinked()) {
            return Optional.empty();
        }
        return Optional.ofNullable(getLinkedDevice());
    }

    @Nullable
    default PointOfOrigin getPointOfOrigin() {
        return getLinkedDeviceOptional().map((sg) -> sg.getPointOfOrigin(getSymbolType())).orElse(null);
    }

    enum DHDUpgradeEnum implements IUpgrade {
        CHEVRON_UPGRADE
    }
}
