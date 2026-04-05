package dev.tauri.jsg.stargate.manager.dialing;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.animation.EnumDialingType;
import dev.tauri.jsg.api.stargate.exception.StargateException;
import dev.tauri.jsg.api.stargate.result.StargateChevronEngageResult;
import dev.tauri.jsg.blockentity.stargate.StargatePegasusBaseBE;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.stargate.StargateAddressDialSequence;
import dev.tauri.jsg.stargate.animation.chevron.StargatePegasusChevronsState;
import dev.tauri.jsg.stargate.animation.incoming.IncomingAnimation;
import dev.tauri.jsg.stargate.animation.incoming.PegasusIncomingAnimation;
import dev.tauri.jsg.stargate.animation.spinning.ClassicSpinHelper;
import dev.tauri.jsg.stargate.animation.spinning.PegasusSpinHelper;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static dev.tauri.jsg.stargate.animation.chevron.StargateChevronsState.A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class StargatePegasusDialingManager extends StargateClassicDialingManager<StargatePegasusBaseBE> {
    public StargatePegasusDialingManager(StargatePegasusBaseBE stargate) {
        super(stargate);
    }

    @Override
    public ClassicSpinHelper generateSpinHelper() {
        return new PegasusSpinHelper(stargate, this::onRingStopSpinning);
    }

    @Override
    protected StargateAddressDialSequence.DialNextConsumer getAddressDialSequenceSymbolConsumer() {
        return (symbol, noEnergy, ignoreMaxChevrons, dialingType) -> {
            if (symbol == null) {
                if (dialingType == EnumDialingType.FAST)
                    engageSymbolDHD(stargate.getSymbolType().getBRB(), noEnergy, ignoreMaxChevrons);
                else
                    attemptOpenDialed();
                this.addressDialSequence = null;
                this.stargate.setChanged();
                return;
            }
            if (dialingType == EnumDialingType.NORMAL) {
                engageSymbolBySpin(symbol, noEnergy, ignoreMaxChevrons);
                return;
            }
            engageSymbolDHD(symbol, noEnergy, ignoreMaxChevrons);
        };
    }

    @Override
    protected void onRingStopSpinning(CompoundTag stopSpinData) {
        if (stargate.getStargateLevel() == null || stargate.getStargateLevel().isClientSide()) return;
        if (getStargateState().dialingComputer())
            setStargateState(EnumStargateState.IDLE);
        if (stopSpinData.contains("symbol")) {
            var symbol = stargate.getSymbolType().valueOf(stopSpinData.getInt("symbol"));
            stargate.getStateManager().getChevronsState().scheduleChevronActivate(0, getNextChevron(symbol, false, stopSpinData.getBoolean("ignoreMaxChevrons")), symbol, getStargateState().dialingComputer(), false, true, stopSpinData.getBoolean("noEnergy"), stopSpinData.getBoolean("ignoreMaxChevrons"));
        }
    }

    @Override
    public @Nullable StargateChevronEngageResult onChevronActivates(CompoundTag customData) {
        if (!customData.contains("symbol"))
            return null;
        var symbol = stargate.getSymbolType().valueOf(customData.getInt("symbol"));
        if (symbol == null)
            return StargateChevronEngageResult.BLOCKED_BY_EVENT;
        var result = engageSymbolInternal(symbol, customData.getBoolean("noxDialing"), customData.getBoolean("noEnergy"), customData.getBoolean("ignoreMaxChevrons"));
        if (!result.ok())
            return result;
        StargateComputerEvents.CHEVRON_ENGAGED.apply(StargateComputerEvents.ChevronEvent.Source.BY_SPIN, symbol, getNextChevron(symbol, true, customData.getBoolean("ignoreMaxChevrons")), getDialedAddressSize()).sendVia(stargate);
        if (customData.getBoolean("checkConnection") && customData.getBoolean("isFinal") && !getConnection().getStatus().waiting())
            return StargateChevronEngageResult.FAILED_FAIL_GATE;
        if (addressDialSequence != null && !getStargateState().dialing())
            addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_DIAL_NEXT, A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY + 10));
        ((StargatePegasusChevronsState) stargate.getStateManager().getChevronsState())
                .activateSlot(StargatePegasusChevronsState.slotFromChevron(ChevronEnum.valueOf(customData.getInt("chevron"))), dialedAddress.getLast());
        return StargateChevronEngageResult.OK;
    }

    protected Pair<List<SymbolInterface>, Pair<Boolean, Boolean>> addressBuffer = Pair.of(new ArrayList<>(), Pair.of(false, false));
    protected boolean canDialNextFromBuffer = true;

    @Override
    public void tick(Level level) {
        super.tick(level);
        if (level.isClientSide) return;
        if (canDialNextFromBuffer && !addressBuffer.first().isEmpty() && level.getGameTime() % 10 == 0) {
            var symbol = addressBuffer.first().get(0);
            if (symbol == null) {
                addressBuffer.first().remove(0);
                stargate.setChanged();
                return;
            }
            if (symbol.brb()) {
                addressBuffer.first().clear();
                canDialNextFromBuffer = false;
                attemptOpenDialed();
                return;
            }
            if (engageSymbolBySpin(symbol, addressBuffer.second().first(), addressBuffer.second().second())) {
                canDialNextFromBuffer = false;
                addressBuffer.first().remove(0);
                stargate.setChanged();
            } else {
                addressBuffer.first().remove(0);
                stargate.setChanged();
            }
        }
    }

    @Override
    public StargateChevronEngageResult engageSymbolDHD(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons) {
        var r = canAddSymbolToBuffer(symbol, ignoreMaxChevrons);
        if (r != StargateChevronEngageResult.OK) return r;
        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.activateSymbol(symbol);
        }
        setStargateState(EnumStargateState.DIALING);
        addressBuffer = Pair.of(addressBuffer.first(), Pair.of(noEnergy, ignoreMaxChevrons));
        addressBuffer.first().add(symbol);
        if (addressDialSequence != null)
            addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_DIAL_NEXT, 10));
        return StargateChevronEngageResult.OK;
    }

    public StargateChevronEngageResult canAddSymbolToBuffer(SymbolInterface symbol, boolean ignoreMaxChevrons) {
        if (isFinalActive && !symbol.brb()) return StargateChevronEngageResult.ADDRESS_FULL;
        if (addressBuffer.first().contains(symbol))
            return StargateChevronEngageResult.ALREADY_ENGAGED;
        if (symbol.brb()) return StargateChevronEngageResult.OK;
        if ((dialedAddress.size() + addressBuffer.first().size() - (addressBuffer.first().contains(stargate.getSymbolType().getBRB()) || symbol.brb() ? 1 : 0)) >= (ignoreMaxChevrons ? 9 : stargate.getMaxChevrons())) {
            return StargateChevronEngageResult.ADDRESS_FULL;
        }
        return StargateChevronEngageResult.OK;
    }

    @Override
    public boolean engageSymbolBySpin(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons) {
        if (!getStargateState().idle() && !getStargateState().dialingDHD()) return false;
        if (isFinalActive) return false;
        var spinHelper = (PegasusSpinHelper) getSpinHelper();
        if (spinHelper.isSpinning()) return false;
        var nextChevron = getNextChevron(symbol, false, ignoreMaxChevrons);
        var currentChevron = dialedAddress.size() > 0 ? getNextChevron(dialedAddress.getLast(), true, ignoreMaxChevrons) : ChevronEnum.getFinal();
        var r = spinHelper.moveToAndEngage(symbol, nextChevron, currentChevron, noEnergy, ignoreMaxChevrons, !stargate.getSoundManager().isRingRollPlaying(), addressBuffer.first().size() < 2 || nextChevron.isFinal());
        if (r.first()) {
            if (!getStargateState().dialingDHD())
                setStargateState(EnumStargateState.DIALING_COMPUTER);
            if (isLockChevron(symbol, true, ignoreMaxChevrons)) {
                getConnection().runOnConnected((conn, sg) -> ((StargateAbstractDialingManager<?>) sg.getDialingManager()).runIncomingWormhole(dialedAddress.addOriginIfMissingAndImmutable().size(), r.second().intValue()));
            }
        }
        return r.first();
    }

    @Override
    public StargateChevronEngageResult engageCurrentSymbol() {
        JSG.logger.error("", new StargateException("Tried to engage current top symbol on pegasus gate. This will not work...", stargate));
        return StargateChevronEngageResult.BLOCKED_BY_EVENT;
    }

    @Override
    protected void clearIncomingWormholeOnSelf() {
        canDialNextFromBuffer = true;
        addressBuffer.first().clear();
        getSpinHelper().stopSpinning(true);
        stargate.getStateManager().getChevronsState().scheduleChevronsDimAll(0, true);
    }

    @Override
    public void runIncomingWormhole(int addressSize, int duration) {
        super.runIncomingWormhole(addressSize, duration);
        canDialNextFromBuffer = true;
        addressBuffer.first().clear();
        stargate.setChanged();
        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.clearSymbols();
        }
    }

    @Override
    protected void failGate() {
        super.failGate();
        canDialNextFromBuffer = true;
        addressBuffer.first().clear();
        getSpinHelper().stopSpinning(true);
        stargate.setChanged();
        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.clearSymbols();
        }
    }

    @Override
    protected void onWormholeDisconnected() {
        super.onWormholeDisconnected();
        canDialNextFromBuffer = true;
        addressBuffer.first().clear();
        getSpinHelper().stopSpinning(true);
        stargate.getStateManager().getChevronsState().scheduleChevronsDimAll(0, true);
        stargate.setChanged();
        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.clearSymbols();
        }
    }

    @Override
    protected void onGateOpen(boolean initiating) {
        super.onGateOpen(initiating);
        canDialNextFromBuffer = true;
        addressBuffer.first().clear();
        stargate.setChanged();
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
        canDialNextFromBuffer = true;
        stargate.setChanged();
        return result;
    }

    @Override
    protected IncomingAnimation<?> getIncomingAnimation(CompoundTag tag) {
        return new PegasusIncomingAnimation(this, tag);
    }

    @Override
    protected IncomingAnimation<?> getIncomingAnimation(int addressSize, int duration) {
        return new PegasusIncomingAnimation(stargate.getTime(), this, addressSize, duration);
    }

    @Override
    public CompoundTag serializeNBT() {
        var c = super.serializeNBT();
        c.putIntArray("addressBufferAddress", addressBuffer.first().stream().map(SymbolInterface::getId).toList());
        c.putBoolean("addressBufferNoEnergy", addressBuffer.second().first());
        c.putBoolean("addressBufferIgnoreMaxChevrons", addressBuffer.second().second());
        c.putBoolean("addressBufferCanDialNext", canDialNextFromBuffer);
        return c;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        super.deserializeNBT(compound);
        var list = new ArrayList<SymbolInterface>();
        for (var i : compound.getIntArray("addressBufferAddress")) {
            list.add(stargate.getSymbolType().valueOf(i));
        }
        addressBuffer = Pair.of(list, Pair.of(compound.getBoolean("addressBufferNoEnergy"), compound.getBoolean("addressBufferIgnoreMaxChevrons")));
        canDialNextFromBuffer = compound.getBoolean("addressBufferCanDialNext");
    }
}
