package dev.tauri.jsg.common.stargate.manager.dialing;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.animation.EnumDialingType;
import dev.tauri.jsg.api.stargate.animation.EnumSpinDirection;
import dev.tauri.jsg.api.stargate.exception.StargateException;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.result.StargateChevronEngageResult;
import dev.tauri.jsg.common.blockentity.stargate.StargateUniverseBaseBE;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.stargate.StargateAddressDialSequence;
import dev.tauri.jsg.common.stargate.animation.chevron.StargateUniverseChevronsState;
import dev.tauri.jsg.common.stargate.animation.incoming.IncomingAnimation;
import dev.tauri.jsg.common.stargate.animation.incoming.UniverseIncomingAnimation;
import dev.tauri.jsg.core.common.chunkloader.ChunkManager;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class StargateUniverseDialingManager extends StargateClassicDialingManager<StargateUniverseBaseBE> {
    public StargateUniverseDialingManager(StargateUniverseBaseBE stargate) {
        super(stargate);
    }

    @Override
    public StargateChevronEngageResult engageSymbolDHD(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons) {
        if (!getStargateState().idle() && !getStargateState().dialingDHD())
            return StargateChevronEngageResult.BUSY;
        var r = canAddSymbol(symbol, ignoreMaxChevrons);
        if (r != StargateChevronEngageResult.OK)
            return r;

        r = engageSymbolInternal(symbol, false, noEnergy, ignoreMaxChevrons);
        if (isFinalActive) {
            getSpinHelper().stopSpinning(false);
        }
        if (r == StargateChevronEngageResult.OK_CONNECTED) {
            getConnection().runOnConnected((conn, sg) -> ((StargateAbstractDialingManager<?>) sg.getDialingManager()).runIncomingWormhole(dialedAddress.addOriginIfMissingAndImmutable().size(), 30));
        }
        if (r.ok()) {
            StargateComputerEvents.CHEVRON_ENGAGED.apply(StargateComputerEvents.ChevronEvent.Source.DHD, symbol, getNextChevron(symbol, true, ignoreMaxChevrons), getDialedAddressSize()).sendVia(stargate);

            setStargateState(EnumStargateState.DIALING);
            if (dialedAddress.size() == 1) {
                beginSpinAndLightUpFirstSymbol(symbol);
            } else {
                lightUpSymbol(symbol);
            }
        }
        return r;
    }

    public boolean engageSymbolByRemote(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons) {
        if (stargate.getStateManager().getChevronsState().get(ChevronEnum.C1).isLocked()) {
            if (!getStargateState().idle() && !getStargateState().dialingRemote())
                return false;
            if (getSpinHelper().isSpinning())
                return false;
            var r = getSpinHelper().moveToAndEngage(symbol, isLockChevron(symbol, ignoreMaxChevrons), noEnergy, ignoreMaxChevrons);
            if (r.first()) {
                setStargateState(EnumStargateState.DIALING_REMOTE);
                if (isLockChevron(symbol, true, ignoreMaxChevrons)) {
                    getConnection().runOnConnected((conn, sg) -> ((StargateAbstractDialingManager<?>) sg.getDialingManager()).runIncomingWormhole(dialedAddress.addOriginIfMissingAndImmutable().size(), r.second().intValue()));
                }
            }
            return r.first();
        }
        if (!getStargateState().idle())
            return false;
        if (getSpinHelper().isSpinning())
            return false;
        setStargateState(EnumStargateState.DIALING_REMOTE);
        beginSpinAndMoveToFirstSymbol(symbol, true, noEnergy, true, ignoreMaxChevrons);
        return true;
    }

    @Override
    public boolean engageSymbolBySpin(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons) {
        if (stargate.getStateManager().getChevronsState().get(ChevronEnum.C1).isLocked())
            return super.engageSymbolBySpin(symbol, noEnergy, ignoreMaxChevrons);
        if (!getStargateState().idle()) return false;
        if (getSpinHelper().isSpinning()) return false;
        setStargateState(EnumStargateState.DIALING_COMPUTER);
        beginSpinAndMoveToFirstSymbol(symbol, true, noEnergy, false, ignoreMaxChevrons);
        return true;
    }

    @Override
    public StargateChevronEngageResult engageAddressByNox(StargateAddressDynamic address, boolean noEnergy, boolean ignoreMaxChevrons) {
        if (!getStargateState().idle())
            return StargateChevronEngageResult.BUSY;
        for (var s : address.subList(0, address.size())) {
            var r = engageSymbolInternal(s, true, noEnergy, ignoreMaxChevrons);
            if (!r.ok()) {
                return r;
            }
            if (r == StargateChevronEngageResult.OK_CONNECTED) {
                getConnection().runOnConnected((conn, sg) -> ((StargateAbstractDialingManager<?>) sg.getDialingManager()).runIncomingWormhole(dialedAddress.addOriginIfMissingAndImmutable().size(), 10));
            }
        }
        lightUpDialedSymbols();
        stargate.getStateManager().getChevronsState().scheduleChevronsLockAll(0, false);
        attemptOpenDialed();
        return StargateChevronEngageResult.OK;
    }

    @Override
    protected StargateAddressDialSequence.DialNextConsumer getAddressDialSequenceSymbolConsumer() {
        return (symbol, noEnergy, ignoreMaxChevrons, dialingType) -> {
            if (symbol == null) {
                //..if (dialingType == EnumDialingType.FAST)
                //..getSpinHelper().stopSpinning(false);
                if (dialingType != EnumDialingType.FAST)
                    attemptOpenDialed();
                this.addressDialSequence = null;
                this.stargate.setChanged();
                return;
            }
            if (dialingType == EnumDialingType.NORMAL) {
                engageSymbolBySpin(symbol, noEnergy, ignoreMaxChevrons);
                return;
            }
            if (dialingType == EnumDialingType.REMOTE) {
                engageSymbolByRemote(symbol, noEnergy, ignoreMaxChevrons);
                return;
            }
            engageSymbolDHD(symbol, noEnergy, ignoreMaxChevrons);
        };
    }

    @Override
    protected StargateChevronEngageResult engageSymbolInternal(SymbolInterface symbol, boolean isNox, boolean noEnergy, boolean ignoreMaxChevrons) {
        var result = super.engageSymbolInternal(symbol, isNox, noEnergy, ignoreMaxChevrons);
        if (!result.ok()) return result;
        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.activateSymbol(symbol);
        }
        return result;
    }

    protected void beginSpinAndLightUpFirstSymbol(SymbolInterface symbol) {
        stargate.playSoundEvent(JSGSoundEvents.GATE_UNIVERSE_DIAL_START);
        stargate.getStateManager().getChevronsState().scheduleChevronsLockAll(10, false);
        var data = new CompoundTag();
        var stopData = new CompoundTag();
        stopData.putBoolean("openGate", true);
        data.put("stopData", stopData);
        data.putInt("litAfterSpin", symbol.getId());
        addTask(new ScheduledTask(JSGScheduledTaskTypes.BEGIN_SPIN, data));
    }

    protected void beginSpinAndMoveToFirstSymbol(SymbolInterface symbol, boolean engage, boolean noEnergy, boolean usingRemote, boolean ignoreMaxChevrons) {
        stargate.playSoundEvent(JSGSoundEvents.GATE_UNIVERSE_DIAL_START);
        stargate.getStateManager().getChevronsState().scheduleChevronsLockAll(10, false);
        var data = new CompoundTag();
        data.putBoolean("engage", engage);
        data.putBoolean("noEnergy", noEnergy);
        data.putBoolean("ignoreMaxChevrons", ignoreMaxChevrons);
        data.putInt("symbol", symbol.getId());
        data.putBoolean("usingRemote", usingRemote);
        addTask(new ScheduledTask(JSGScheduledTaskTypes.BEGIN_SPIN, data));
    }

    protected void lightUpSymbol(SymbolInterface symbol) {
        stargate.playSoundEvent(StargateSoundEventEnum.CHEVRON_SHUT);
        var data = new CompoundTag();
        data.putInt("symbol", symbol.getId());
        stargate.getStateManager().getChevronsState().addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_SYMBOL_LOCK, data));
        if (addressDialSequence != null)
            addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_DIAL_NEXT, 20));
    }

    protected void lightUpDialedSymbols() {
        stargate.playSoundEvent(StargateSoundEventEnum.CHEVRON_SHUT);
        var data = new CompoundTag();
        data.putBoolean("litAddress", true);
        stargate.getStateManager().getChevronsState().addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_SYMBOL_LOCK, data));
    }

    @Override
    public boolean canAbortDialing() {
        return super.canAbortDialing() && getStargateState() != EnumStargateState.RESETTING;
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        if (scheduledTask == JSGScheduledTaskTypes.BEGIN_SPIN.get()) {
            if (customData.getBoolean("freely")) {
                getSpinHelper().rotateFreely(EnumSpinDirection.random(new Random()));
            }
            if (customData.contains("litAfterSpin")) {
                lightUpSymbol(stargate.getSymbolType().valueOf(customData.getInt("litAfterSpin")));
                getSpinHelper().rotateFreely(EnumSpinDirection.random(new Random()));
                if (customData.contains("stopData"))
                    getSpinHelper().setSpinStopData(customData.getCompound("stopData"));
            }
            if (customData.contains("symbol")) {
                var symbol = stargate.getSymbolType().valueOf(customData.getInt("symbol"));
                if (customData.getBoolean("engage")) {
                    if (customData.getBoolean("usingRemote"))
                        engageSymbolByRemote(symbol, customData.getBoolean("noEnergy"), customData.getBoolean("ignoreMaxChevrons"));
                    else
                        engageSymbolBySpin(symbol, customData.getBoolean("noEnergy"), customData.getBoolean("ignoreMaxChevrons"));
                } else {
                    getSpinHelper().moveTo(symbol);
                    if (customData.contains("stopData"))
                        getSpinHelper().setSpinStopData(customData.getCompound("stopData"));
                }
            }
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_RESET.get()) {
            resetStargate();
        } else
            super.executeTask(scheduledTask, customData);
    }

    @Override
    protected boolean canUnforceChunk() {
        return this.getStargateState() != EnumStargateState.RESETTING;
    }

    protected void scheduleResetStargate() {
        scheduledTasks.clear();
        addressDialSequence = null;
        ChunkManager.forceChunk(stargate.getStargateLevel(), stargate.blockPosition());
        setStargateState(EnumStargateState.RESETTING);
        var ticksToStop = (getSpinHelper().isSpinning() ? getSpinHelper().getTickToStop() : 0);
        if (ticksToStop > 0)
            getSpinHelper().stopSpinning(true);
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_RESET, (int) (ticksToStop + JSGScheduledTaskTypes.STARGATE_RESET.get().waitTicks())));
    }

    protected void resetStargate() {
        scheduledTasks.clear();
        stargate.getStateManager().getChevronsState().scheduleChevronsDimAll(0, false);

        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.clearSymbols();
        }

        var data = new CompoundTag();
        data.putBoolean("isResetting", true);

        if (getSpinHelper().getCurrentTopSymbol(1) == stargate.getSymbolType().getTopSymbol()) {
            onRingStopSpinning(data);
            return;
        }

        setStargateState(EnumStargateState.RESETTING);
        getSpinHelper().moveTo(stargate.getSymbolType().getTopSymbol());
        getSpinHelper().setSpinStopData(data);
    }


    @Override
    protected void onRingStopSpinning(CompoundTag stopSpinData) {
        if (stargate.getStargateLevel() == null || stargate.getStargateLevel().isClientSide()) return;
        if (stopSpinData.contains("symbol")) {
            var symbol = stargate.getSymbolType().valueOf(stopSpinData.getInt("symbol"));
            var result = engageSymbolInternal(symbol, false, stopSpinData.getBoolean("noEnergy"), stopSpinData.getBoolean("ignoreMaxChevrons"));
            if (result.ok()) {
                if (getStargateState().dialingComputer())
                    setStargateState(EnumStargateState.IDLE);
                lightUpSymbol(symbol);
                StargateComputerEvents.CHEVRON_ENGAGED.apply(StargateComputerEvents.ChevronEvent.Source.BY_SPIN, symbol, getNextChevron(symbol, true, stopSpinData.getBoolean("ignoreMaxChevrons")), getDialedAddressSize()).sendVia(stargate);
            } else {
                stargate.getStateManager().getChevronsState().scheduleChevronFail(40, ChevronEnum.getFinal(), true);
            }
        } else if (getStargateState().dialingComputer())
            setStargateState(EnumStargateState.IDLE);
        if (stopSpinData.getBoolean("isResetting")) {
            setStargateState(EnumStargateState.IDLE);
            addressDialSequence = null;
            stargate.getListenerHandler().gateReset();
            stargate.setChanged();
            ChunkManager.unforceChunk(stargate.getStargateLevel(), stargate.blockPosition());
        }
        if (stopSpinData.getBoolean("openGate")) {
            attemptOpenDialed();
        }
    }

    @Override
    protected IncomingAnimation<?> getIncomingAnimation(CompoundTag tag) {
        return new UniverseIncomingAnimation(this, tag);
    }

    @Override
    protected IncomingAnimation<?> getIncomingAnimation(int addressSize, int duration) {
        return new UniverseIncomingAnimation(stargate.getTime(), this, addressSize, duration);
    }

    @Override
    protected void clearIncomingWormholeOnSelf() {
        scheduleResetStargate();
    }

    @Override
    public void runIncomingWormhole(int addressSize, int duration) {
        if (!getConnection().getStatus().prepared() && !getConnection().getStatus().waiting()) {
            JSG.logger.error("", new StargateException("Gate must be prepared to accept incoming", stargate));
            return;
        }
        if (incomingAnimation != null) {
            JSG.logger.error("", new StargateException("incomingAnimation != null!", stargate));
            return;
        }
        scheduledTasks.clear();
        ((StargateUniverseChevronsState) stargate.getStateManager().getChevronsState()).dimAllSymbols();
        getSpinHelper().stopSpinning(true);
        dialedAddress.clear();
        addressDialSequence = null;
        incomingAnimation = getIncomingAnimationByConfig(addressSize, duration);
        setStargateState(EnumStargateState.INCOMING);
        stargate.getListenerHandler().gateIncoming(addressSize);
        onIncoming(addressSize, duration);
        stargate.setStargateChanged();
        StargateComputerEvents.WORMHOLE_INCOMING.apply(addressSize).sendVia(stargate);

        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.clearSymbols();
        }
    }

    @Override
    protected void failGate() {
        super.failGate();
        scheduleResetStargate();
        var dhd = stargate.getLinkedDevice();
        if (stargate.isLinkedAndDHDOperational() && dhd != null) {
            dhd.clearSymbols();
        }
    }

    @Override
    protected void onWormholeDisconnected() {
        super.onWormholeDisconnected();
        scheduleResetStargate();
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
}
