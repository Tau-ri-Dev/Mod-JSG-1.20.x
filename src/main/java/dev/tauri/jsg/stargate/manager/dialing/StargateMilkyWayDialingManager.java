package dev.tauri.jsg.stargate.manager.dialing;

import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.result.StargateChevronEngageResult;
import dev.tauri.jsg.blockentity.stargate.StargateMilkyWayBaseBE;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.stargate.animation.incoming.IncomingAnimation;
import dev.tauri.jsg.stargate.animation.incoming.MilkyWayIncomingAnimation;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

import static dev.tauri.jsg.stargate.animation.chevron.StargateChevronsState.A_CHEVRON_ACTIVATE_AFTER_OPEN_DELAY;
import static dev.tauri.jsg.stargate.animation.chevron.StargateChevronsState.A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class StargateMilkyWayDialingManager extends StargateClassicDialingManager<StargateMilkyWayBaseBE> {
    public StargateMilkyWayDialingManager(StargateMilkyWayBaseBE stargate) {
        super(stargate);
    }

    @Override
    public StargateChevronEngageResult engageSymbolDHD(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons) {
        if (!getStargateState().idle())
            return StargateChevronEngageResult.BUSY;
        var r = engageSymbolInternal(symbol, false, noEnergy, ignoreMaxChevrons);
        if (!r.ok()) {
            return r;
        }
        StargateComputerEvents.CHEVRON_ENGAGED.apply(StargateComputerEvents.ChevronEvent.Source.DHD, symbol, getNextChevron(symbol, true, ignoreMaxChevrons), getDialedAddressSize()).sendVia(stargate);
        setStargateState(EnumStargateState.DIALING);
        if (r == StargateChevronEngageResult.OK_CONNECTED) {
            getConnection().runOnConnected((conn, sg) -> ((StargateAbstractDialingManager<?>) sg.getDialingManager()).runIncomingWormhole(dialedAddress.addOriginIfMissingAndImmutable().size(), 30));
        }
        int plusTime = new Random().nextInt(5);
        if (isLockChevron(symbol, ignoreMaxChevrons)) {
            if (stargate.getConfig().getValueOrDefault(StargateConfigOptions.MilkyWay.DHD_POO_LOCK)) {
                stargate.getStateManager().getChevronsState().scheduleChevronFinalOpenAndActivate(5 + plusTime, null, true);
                if (addressDialSequence != null)
                    addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_DIAL_NEXT, 15 + plusTime + A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY + A_CHEVRON_ACTIVATE_AFTER_OPEN_DELAY));
            } else {
                stargate.getStateManager().getChevronsState().scheduleChevronActivate(10 + plusTime, ChevronEnum.getFinal(), null, true);
                if (addressDialSequence != null)
                    addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_DIAL_NEXT, 15 + plusTime));
            }
        } else {
            stargate.getStateManager().getChevronsState().scheduleChevronActivate(10 + plusTime, getNextChevron(symbol, true, ignoreMaxChevrons), null, true);
            if (addressDialSequence != null)
                addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_DIAL_NEXT, 15 + plusTime));
        }
        stargate.setChanged();
        return r;
    }


    @Override
    protected IncomingAnimation<?> getIncomingAnimation(CompoundTag tag) {
        return new MilkyWayIncomingAnimation(this, tag);
    }

    @Override
    protected IncomingAnimation<?> getIncomingAnimation(int addressSize, int duration) {
        return new MilkyWayIncomingAnimation(stargate.getTime(), this, addressSize, duration);
    }

    @Override
    protected void clearIncomingWormholeOnSelf() {
        getSpinHelper().stopSpinning(true);
        stargate.getStateManager().getChevronsState().scheduleChevronsDimAll(0, true);
    }

    @Override
    public void runIncomingWormhole(int addressSize, int duration) {
        super.runIncomingWormhole(addressSize, duration);
        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.clearSymbols();
        }
    }

    @Override
    protected void failGate() {
        super.failGate();
        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.clearSymbols();
        }
    }

    @Override
    protected void onWormholeDisconnected() {
        super.onWormholeDisconnected();
        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.clearSymbols();
        }
    }

    @Override
    protected void onGateOpen(boolean initiating) {
        super.onGateOpen(initiating);
        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.activateSymbol(stargate.getSymbolType().getBRB());
        }
    }

    @Override
    protected StargateChevronEngageResult engageSymbolInternal(SymbolInterface symbol, boolean isNox, boolean noEnergy, boolean ignoreMaxChevrons) {
        var result = super.engageSymbolInternal(symbol, isNox, noEnergy, ignoreMaxChevrons);
        if (result.ok()) {
            var dhd = stargate.getLinkedDevice();
            if (stargate.isLinkedAndDHDOperational() && dhd != null) {
                dhd.activateSymbol(symbol);
            }
        }
        return result;
    }
}
