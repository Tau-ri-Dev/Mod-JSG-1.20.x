package dev.tauri.jsg.common.dialhomedevice.manager;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.dialhomedevice.DHDReactorState;
import dev.tauri.jsg.api.dialhomedevice.manager.IDHDReactorManager;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.util.FluidTank;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DHDReactorManager extends AbstractDHDManager<DHDAbstractBE> implements IDHDReactorManager {
    protected final FluidTank tank;
    protected DHDReactorState state = DHDReactorState.NO_CRYSTAL;
    protected Predicate<FluidTank> canStartOrContinueReaction;
    protected Function<FluidTank, Integer> energyFromOneMb;

    protected int lastAmount = 0;
    protected DHDReactorState lastState = null;

    public DHDReactorManager(DHDAbstractBE dhd, Supplier<FluidTank> tankSupplier, Predicate<FluidTank> canStartOrContinueReaction, Function<FluidTank, Integer> energyFromOneMb) {
        super(dhd);
        this.tank = tankSupplier.get();
        this.canStartOrContinueReaction = canStartOrContinueReaction;
        this.energyFromOneMb = energyFromOneMb;
    }

    @Override
    public DHDReactorState getState() {
        return state;
    }

    @Override
    public FluidTank getTank() {
        return tank;
    }

    @Override
    public void tick(@NotNull Level level) {
        if (level.isClientSide()) return;

        if ((lastAmount > getTank().getFluidAmount() && level.getGameTime() % 20 == 0) && lastAmount < getTank().getFluidAmount()) {
            lastAmount = getTank().getFluidAmount();
            dhd.getStateManager().getAndSendState(CoreStateTypes.RENDERER_STATE.get());
        }
        if (state != lastState) {
            lastState = state;
            dhd.getStateManager().getAndSendState(CoreStateTypes.RENDERER_STATE.get());
        }

        if (!dhd.isAssembled(dhd.getFluidTankItemPart())) {
            state = DHDReactorState.NO_FLUID_TANK;
            return;
        }

        if (!canStartOrContinueReaction.test(tank)) {
            state = DHDReactorState.NO_CRYSTAL;
            return;
        }

        dhd.getLinkedDeviceOptional().ifPresentOrElse((gateTile) -> {
            var energyStorageOpt = gateTile.getStargateCapability(ForgeCapabilities.ENERGY, null).resolve();
            if (energyStorageOpt.isEmpty() || !(energyStorageOpt.get() instanceof JSGEnergyStorage energyStorage)) {
                state = DHDReactorState.STANDBY;
                return;
            }

            int amount = 1;

            if (state != DHDReactorState.STANDBY) {
                FluidStack simulatedDrain = tank.drain(amount, IFluidHandler.FluidAction.SIMULATE);

                if (simulatedDrain.getAmount() >= amount)
                    state = DHDReactorState.ONLINE;
                else state = DHDReactorState.NO_FUEL;
            }

            if (state == DHDReactorState.ONLINE || state == DHDReactorState.STANDBY) {
                float percent = energyStorage.getTrueEnergyStored() / (float) energyStorage.getTrueMaxEnergyStored();

                if (percent < JSGConfig.DialHomeDevice.activationLevel.get())
                    state = DHDReactorState.ONLINE;

                else if (percent >= JSGConfig.DialHomeDevice.deactivationLevel.get())
                    state = DHDReactorState.STANDBY;
            }

            if (state == DHDReactorState.ONLINE) {
                tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                energyStorage.receiveLongEnergy(energyFromOneMb.apply(tank), false);
            }
        }, () -> state = DHDReactorState.NOT_LINKED);
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.put("tank", tank.writeToNBT(new CompoundTag()));
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        tank.readFromNBT(compound.getCompound("tank"));
    }
}
