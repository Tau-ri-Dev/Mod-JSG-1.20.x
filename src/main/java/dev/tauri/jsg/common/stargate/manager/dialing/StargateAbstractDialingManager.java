package dev.tauri.jsg.common.stargate.manager.dialing;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.event.*;
import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.animation.EnumDialingType;
import dev.tauri.jsg.api.stargate.animation.IAddressDialSequence;
import dev.tauri.jsg.api.stargate.exception.BiStargateException;
import dev.tauri.jsg.api.stargate.exception.StargateException;
import dev.tauri.jsg.api.stargate.manager.IStargateDialingManager;
import dev.tauri.jsg.api.stargate.network.IStargateNetwork;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.result.*;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateClassicRendererState;
import dev.tauri.jsg.common.item.admincontroller.ACListener;
import dev.tauri.jsg.common.stargate.StargateAddressDialSequence;
import dev.tauri.jsg.common.stargate.animation.incoming.IncomingAnimation;
import dev.tauri.jsg.common.stargate.animation.incoming.StaticIncomingAnimation;
import dev.tauri.jsg.common.stargate.animation.spinning.ClassicSpinHelper;
import dev.tauri.jsg.common.stargate.manager.*;
import dev.tauri.jsg.common.stargate.manager.state.StargateAbstractStateManager;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.common.state.stargate.StargateRendererActionState;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.blockentity.ScheduledTaskExecutorInterface;
import dev.tauri.jsg.core.common.chunkloader.ChunkManager;
import dev.tauri.jsg.core.common.config.ingame.IConfigurable;
import dev.tauri.jsg.core.common.config.json.dimension.JSGDimensionConfig;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.tauri.jsg.common.stargate.animation.chevron.StargateChevronsState.*;

/**
 * Class that handles connection between gates.
 *
 * @param <SG> the stargate block entity type
 * @author MineDragonCZ_ (Tau'ri Dev)
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class StargateAbstractDialingManager<SG extends Stargate<?>> extends AbstractStargateManager<SG> implements ITickable, ScheduledTaskExecutorInterface, INBTSerializable<CompoundTag>, IStargateDialingManager {
    public static final Supplier<IStargateNetwork> network = () -> StargateNetwork.INSTANCE;
    protected final StargateConnection activeConnection;
    protected final Consumer<StargateConnection> tickConnection;
    protected final StargateAddressDynamic dialedAddress;
    protected boolean isFinalActive;
    @Nullable
    protected IncomingAnimation<?> incomingAnimation = null;
    @Nullable
    protected StargateAddressDialSequence addressDialSequence = null;
    private EnumStargateState stargateState = EnumStargateState.IDLE;

    public StargateAbstractDialingManager(SG stargate) {
        this(stargate, (connection) -> {
        });
    }

    public StargateAbstractDialingManager(SG stargate, Consumer<StargateConnection> tickConnection) {
        super(stargate);
        this.activeConnection = StargateConnection.createEmpty(stargate);
        this.tickConnection = tickConnection;
        this.dialedAddress = new StargateAddressDynamic(stargate.getSymbolType());
    }

    // -----------------------------------------
    // INIT

    public abstract ClassicSpinHelper generateSpinHelper();

    // -----------------------------------------
    // STARGATE MECHANICAL STATE

    public EnumStargateState getStargateState() {
        return stargateState;
    }

    public void setStargateState(EnumStargateState newState) {
        if (stargateState == newState) return;
        stargate.getListenerHandler().gateStateChanged(getStargateState(), newState);
        stargateState = newState;
        stargate.setStargateChanged();
    }

    // -----------------------------------------
    // PUBLIC GETTERS

    @Override
    public abstract ClassicSpinHelper getSpinHelper();

    @Override
    public StargateConnection getConnection() {
        return activeConnection;
    }

    @Override
    public int getDialedAddressSize() {
        return dialedAddress.size();
    }

    @Override
    public StargateAddressDynamic getDialedAddress() {
        return new StargateAddressDynamic(dialedAddress);
    }

    @Override
    public boolean isFinalActive() {
        return isFinalActive;
    }

    @Override
    public Optional<IAddressDialSequence> getDialingSequence() {
        return Optional.ofNullable(addressDialSequence);
    }

    public StargateClassicRendererState.StargateClassicRendererStateBuilder processRenderState(StargateClassicRendererState.StargateClassicRendererStateBuilder builder) {
        return builder;
    }

    // -----------------------------------------
    // SCHEDULING THE CHEVRONS OPERATIONS

    public ChevronEnum getNextChevron(@Nullable SymbolInterface symbolToLock, boolean alreadyInAddress, boolean ignoreMaxChevrons) {
        var nextChevron = ChevronEnum.valueOf(dialedAddress.size() - (alreadyInAddress ? 1 : 0));
        if (isLockChevron(symbolToLock, !alreadyInAddress, ignoreMaxChevrons))
            nextChevron = ChevronEnum.getFinal();
        return nextChevron;
    }

    protected void onRingStopSpinning(CompoundTag stopSpinData) {
        if (stargate.getStargateLevel() == null || stargate.getStargateLevel().isClientSide()) return;
        if (stopSpinData.contains("symbol")) {
            var symbol = stargate.getSymbolType().valueOf(stopSpinData.getInt("symbol"));
            stargate.getStateManager().getChevronsState().scheduleChevronOpen(A_CHEVRON_OPEN_AFTER_STOPPED_DELAY, ChevronEnum.getFinal(), false);
            stargate.getStateManager().getChevronsState().scheduleChevronActivate(A_CHEVRON_OPEN_AFTER_STOPPED_DELAY + A_CHEVRON_ACTIVATE_AFTER_OPEN_DELAY, getNextChevron(symbol, false, stopSpinData.getBoolean("ignoreMaxChevrons")), symbol, false, true, true, stopSpinData.getBoolean("noEnergy"), stopSpinData.getBoolean("ignoreMaxChevrons"));
        } else if (getStargateState().dialingComputer())
            setStargateState(EnumStargateState.IDLE);
    }

    // -----------------------------------------

    /**
     * SAFE METHOD TO USE
     * <p>
     *
     * @return
     */
    public StargateOpenResult attemptOpenDialed() {
        if (getConnection().isRIG()) {
            if (openGate())
                return StargateOpenResult.OK;
            dialingFailed(StargateOpenResult.CALLER_HUNG_UP);
            return StargateOpenResult.CALLER_HUNG_UP;
        }

        var result = getCanOpenDialed();
        if (result.ok()) {
            if (openGate())
                return StargateOpenResult.OK;
            result = StargateOpenResult.ADDRESS_MALFORMED;
        }
        dialingFailed(result);
        return result;
    }

    protected StargateOpenResult getCanOpenDialed() {
        if (!getDialedAddress().validate()) {
            return StargateOpenResult.ADDRESS_MALFORMED;
        }
        return checkEnergyRequirementsOnDialed().toOpenResult();
    }

    /**
     * SAFE METHOD TO USE
     * <p>
     *
     * @return
     */
    public StargateChevronEngageResult engageCurrentSymbol() {
        var symbol = getSpinHelper().getCurrentTopSymbol();
        if (symbol == null) return StargateChevronEngageResult.BLOCKED_BY_EVENT;
        stargate.getStateManager().getChevronsState().scheduleChevronOpen(A_CHEVRON_OPEN_AFTER_STOPPED_DELAY, ChevronEnum.getFinal(), false);
        stargate.getStateManager().getChevronsState().scheduleChevronActivate(A_CHEVRON_OPEN_AFTER_STOPPED_DELAY + A_CHEVRON_ACTIVATE_AFTER_OPEN_DELAY, getNextChevron(symbol, false, false), symbol, false, true, true);
        return StargateChevronEngageResult.OK;
    }

    /**
     * SAFE METHOD TO USE
     * <p>
     *
     * @return
     */
    public boolean engageSymbolBySpin(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons) {
        if (!getStargateState().idle() && !getStargateState().dialing()) return false;
        if (getSpinHelper().isSpinning()) return false;
        var r = getSpinHelper().moveToAndEngage(symbol, isLockChevron(symbol, true, ignoreMaxChevrons), noEnergy, ignoreMaxChevrons);
        if (r.first()) {
            setStargateState(EnumStargateState.DIALING_COMPUTER);
            if (isLockChevron(symbol, true, ignoreMaxChevrons)) {
                getConnection().runOnConnected((conn, sg) -> ((StargateAbstractDialingManager<?>) sg.getDialingManager()).runIncomingWormhole(dialedAddress.addOriginIfMissingAndImmutable().size(), r.second().intValue()));
            }
        }
        return r.first();
    }

    public StargateChevronEngageResult dialAddress(StargateAddressDynamic address, boolean noEnergy, boolean ignoreMaxChevrons, EnumDialingType dialingType) {
        if (!getStargateState().idle() || addressDialSequence != null) return StargateChevronEngageResult.BUSY;
        if (getDialedAddressSize() > 0) return StargateChevronEngageResult.ALREADY_ENGAGED;

        if (dialingType == EnumDialingType.NOX) {
            var r = checkAddressAndEnergyRequirements(address, noEnergy);
            if (r != StargateAddressCheckResult.OK) {
                dialingFailed(r.toOpenResult());
                return StargateChevronEngageResult.BLOCKED_BY_EVENT;
            }
            var result = engageAddressByNox(address, noEnergy, ignoreMaxChevrons);
            if (result != StargateChevronEngageResult.OK) {
                dialingFailed(StargateOpenResult.ADDRESS_MALFORMED);
                return result;
            }
            stargate.getListenerHandler().gateBeginDial();
            stargate.getLogManager().info(Component.literal("Gate opened using NOX"));
            return result;
        }

        addressDialSequence = getAddressDialSequence(address, noEnergy, ignoreMaxChevrons, dialingType);
        stargate.setStargateChanged();
        addressDialSequence.dialNext();
        stargate.getListenerHandler().gateBeginDial();
        stargate.getLogManager().info(Component.literal("Gate started dialing using dialing type " + dialingType.name()));
        return StargateChevronEngageResult.OK;
    }

    protected StargateAddressDialSequence.DialNextConsumer getAddressDialSequenceSymbolConsumer() {
        return (symbol, noEnergy, ignoreMaxChevrons, dialingType) -> {
            if (symbol == null) {
                attemptOpenDialed();
                this.addressDialSequence = null;
                this.stargate.setStargateChanged();
                return;
            }
            if (dialingType == EnumDialingType.NORMAL) {
                engageSymbolBySpin(symbol, noEnergy, ignoreMaxChevrons);
                return;
            }
            engageSymbolDHD(symbol, noEnergy, ignoreMaxChevrons);
        };
    }

    protected StargateAddressDialSequence getAddressDialSequence(StargateAddressDynamic address, boolean noEnergy, boolean ignoreMaxChevrons, EnumDialingType dialingType) {
        return new StargateAddressDialSequence(getAddressDialSequenceSymbolConsumer(), address, noEnergy, ignoreMaxChevrons, dialingType);
    }

    protected StargateAddressDialSequence getAddressDialSequence(CompoundTag tag) {
        return new StargateAddressDialSequence(getAddressDialSequenceSymbolConsumer(), tag);
    }

    /**
     * SAFE METHOD TO USE
     * <p>
     *
     * @param symbol
     * @return
     */
    public StargateChevronEngageResult engageSymbolDHD(SymbolInterface symbol, boolean noEnergy, boolean ignoreMaxChevrons) {
        return engageSymbolInternal(symbol, false, noEnergy, ignoreMaxChevrons);
    }

    public StargateChevronEngageResult engageAddressByNox(StargateAddressDynamic address, boolean noEnergy, boolean ignoreMaxChevrons) {
        if (!getStargateState().idle())
            return StargateChevronEngageResult.BUSY;
        for (var s : address.subList(0, address.size())) {
            var r = engageSymbolInternal(s, true, noEnergy, ignoreMaxChevrons);
            if (!r.ok()) {
                return r;
            }
            if (r == StargateChevronEngageResult.OK_CONNECTED) {
                getConnection().runOnConnected((conn, sg) -> ((StargateAbstractDialingManager<?>) sg.getDialingManager()).runIncomingWormhole(dialedAddress.addOriginIfMissingAndImmutable().size(), 0));
            }
        }
        var chevrons = new ArrayList<ChevronEnum>();
        for (var i = 0; i < address.size(); i++) {
            if (i == (address.size() - 1))
                chevrons.add(ChevronEnum.getFinal());
            else
                chevrons.add(ChevronEnum.valueOf(i));
        }
        stargate.getStateManager().getChevronsState().scheduleChevronsActivateMultiple(0, false, false, chevrons);
        attemptOpenDialed();
        return StargateChevronEngageResult.OK;
    }

    protected StargateChevronEngageResult engageSymbolInternal(SymbolInterface symbol, boolean isNox, boolean noEnergy, boolean ignoreMaxChevrons) {
        if (!getStargateState().idle() && !getStargateState().dialing()) return StargateChevronEngageResult.BUSY;
        var r = canAddSymbol(symbol, ignoreMaxChevrons);
        if (r != StargateChevronEngageResult.OK) return r;
        dialedAddress.addSymbol(symbol);
        stargate.getLogManager().debug(Component.literal("Symbol engaged: " + symbol.getEnglishName()));
        if (isLockChevron(symbol, ignoreMaxChevrons)) {
            isFinalActive = true;
            stargate.setStargateChanged();
        }
        var lastGate = getConnection().getTarget();
        var connResult = findAndConnect(dialedAddress.addOriginIfMissingAndImmutable(), isNox, noEnergy);
        if (getConnection().getStatus().prepared() && isFinalActive) {
            getConnection().updateStatus(StargateConnectionStatus.WAITING_FOR_WORMHOLE);
        }
        stargate.getListenerHandler().gateSymbolEngage(symbol);
        ACListener.sendChevronEngaged(stargate.getStateManager(), stargate.getStargatePos(), symbol, getNextChevron(symbol, true, ignoreMaxChevrons));
        if (!connResult.ok())
            return StargateChevronEngageResult.OK;
        if (lastGate.isEmpty() || !getConnection().getTarget().map(target -> target.equals(lastGate.get())).orElse(false))
            return StargateChevronEngageResult.OK_CONNECTED;
        return StargateChevronEngageResult.OK;
    }

    public StargateCloseResult disconnectSafe(StargateClosedReasonEnum reason) {
        var connState = getConnection().getStatus();
        if (connState.full()) return attemptClose(reason);
        if (connState.closing()) {
            return disconnectWormhole(reason) ? StargateCloseResult.OK : StargateCloseResult.NOT_OPEN;
        }
        if (connState.prepared() || connState.waiting()) {
            getConnection().updateStatus(StargateConnectionStatus.CLOSING);
            clearIncomingWormholeOnIncoming();
            return disconnect() ? StargateCloseResult.OK : StargateCloseResult.NOT_OPEN;
        }
        return StargateCloseResult.NOT_OPEN;
    }

    public boolean abortDialingSequence() {
        if (!canAbortDialing()) return false;
        getSpinHelper().stopSpinning(true);
        disconnectSafe(StargateClosedReasonEnum.REQUESTED);
        dialingFailed(StargateOpenResult.ABORTED);
        stargate.getListenerHandler().gateDialAbort();
        return true;
    }

    public boolean canAbortDialing() {
        return !getStargateState().notInitiating() && (addressDialSequence != null || getStargateState().dialing() || dialedAddress.size() > 0);
    }

    /**
     * SAFE METHOD TO USE
     * <p>
     * Tries to close the gate with specified reason
     *
     * @param reason why the gate should close
     * @return result of this attempt
     */
    public StargateCloseResult attemptClose(StargateClosedReasonEnum reason) {
        if (activeConnection.getStatus() != StargateConnectionStatus.FULLY) {
            StargateComputerEvents.ATTEMPT_CLOSE_FAILED.apply(StargateCloseResult.NOT_OPEN, getDialedAddress(), false).sendVia(stargate);
            return StargateCloseResult.NOT_OPEN;
        }

        if (reason == StargateClosedReasonEnum.REQUESTED) {
            var eventResult = activeConnection.callConnected((c, sg) -> {
                if (new StargateClosingEvent(stargate, sg, activeConnection.isInitiating(), reason).post())
                    return true;
                return new StargateClosingEvent(sg, stargate, c.isInitiating(), reason).post();
            }, () -> false);
            if (eventResult) {
                StargateComputerEvents.ATTEMPT_CLOSE_FAILED.apply(StargateCloseResult.BLOCKED_BY_EVENT, getDialedAddress(), activeConnection.isInitiating()).sendVia(stargate);
                return StargateCloseResult.BLOCKED_BY_EVENT;
            }
        }

        if (!closeGate(reason)) {
            StargateComputerEvents.ATTEMPT_CLOSE_FAILED.apply(StargateCloseResult.NOT_OPEN, getDialedAddress(), activeConnection.isInitiating()).sendVia(stargate);
            return StargateCloseResult.NOT_OPEN;
        }
        return StargateCloseResult.OK;
    }

    /**
     * Find gate in the network by its address and try to connect to it virtually.
     *
     * @param address the target stargate's address (usually dialed address of this gate)
     * @return result of the connection attempt
     */
    @ApiStatus.Internal
    protected StargateConnectResult findAndConnect(StargateAddressDynamic address, boolean isNox, boolean noEnergy) {
        var gate = network.get().getStargate(address);
        if (gate == null) {
            if (!activeConnection.getStatus().none()) {
                JSG.logger.debug("Connected - disconnecting - target gate null");
                stargate.getLogManager().debug(Component.literal("Connected - disconnecting - target gate null"));
                disconnectSafe(StargateClosedReasonEnum.CONNECTION_LOST);
            }
            return StargateConnectResult.ADDRESS_MALFORMED;
        }
        if (!activeConnection.getStatus().none()) {
            if (activeConnection.getTarget().map(target -> target.equals(gate)).orElse(false)) {
                JSG.logger.debug("Connected, but to the same gate - updating");
                stargate.getLogManager().debug(Component.literal("Connected, but to the same gate - updating"));
                return StargateConnectResult.ALREADY_CONNECTED;
            }
            JSG.logger.debug("Connected - disconnecting - active connection present");
            stargate.getLogManager().debug(Component.literal("Connected - disconnecting - active connection present"));
            disconnectSafe(StargateClosedReasonEnum.CONNECTION_LOST);
        }
        JSG.logger.debug("Connecting");
        stargate.getLogManager().debug(Component.literal("Connecting to a stargate " + gate));
        var result = connect(address, gate, isNox, noEnergy);
        JSG.logger.debug("Connection result: {}", result.name());
        stargate.getLogManager().debug(Component.literal("Connection result: " + result.name()));
        return result;
    }

    /**
     * Tries to open wormhole the gate to the connected stargate
     *
     * @return true if success, otherwise false
     */
    protected boolean openGate() {
        if (activeConnection.getStatus() != StargateConnectionStatus.WAITING_FOR_WORMHOLE) {
            JSG.logger.warn("", new StargateException("Tried to open gate while not prepared for that!", stargate));
            stargate.getLogManager().error(Component.literal("Tried to open gate while not prepared for that!"));
            return false;
        }
        activeConnection.runOnBothConnected((conn, stargate) -> {
            var dm = (StargateAbstractDialingManager<?>) stargate.getDialingManager();
            if (dm.incomingAnimation != null) {
                dm.incomingAnimation.finish();
            }
            dm.addressDialSequence = null;
            dm.incomingAnimation = null;
            dm.scheduledTasks.clear();
            dm.isFinalActive = true;
            stargate.getListenerHandler().gateOpen();
            dm.setStargateState(EnumStargateState.UNSTABLE_OPENING);
            StargateComputerEvents.WORMHOLE_OPEN_UNSTABLE.apply(conn.isInitiating() ? stargate.getDialingManager().getDialedAddress() : new StargateAddressDynamic(stargate.getSymbolType()), conn.isInitiating()).sendVia(stargate);

            ((StargateAbstractStateManager<?, ?>) stargate.getStateManager()).sendRenderingUpdate(StargateRendererActionState.EnumGateAction.OPEN_GATE, 0, conn.isNox());

            dm.addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_OPEN_SOUND.get(), stargate.getOpenSoundDelay()));
            dm.addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_ENGAGE.get()));

            ((StargateEventHorizonManager) stargate.getEventHorizonManager()).onGateOpen(conn.isNox());

            if (conn.isInitiating()) {
                // extract energy from the gate
                ((StargateEnergyManager<?, ?>) stargate.getEnergyManager()).onGateOpen();
            }
            dm.onGateOpen(conn.isInitiating());
            stargate.setStargateChanged();
            JSG.logger.info("Gate {} opened!", stargate.getStargatePos().toString());
            stargate.getLogManager().info(Component.literal("Stargate opened! Target: " + conn.getTarget().map(StargatePos::toString).orElse("EMPTY")));
        });
        return true;
    }

    protected void onGateOpen(boolean initiating) {

    }

    /**
     * Tries to connect wormhole - enabled event horizon teleportation and marks the gate as fully openned
     *
     * @return true if success, otherwise false
     */
    @ApiStatus.Internal
    protected boolean connectWormhole() {
        if (activeConnection.getStatus() != StargateConnectionStatus.WAITING_FOR_WORMHOLE) {
            JSG.logger.warn("", new StargateException("Tried to connect wormhole while not waiting for that!", stargate));
            stargate.getLogManager().error(Component.literal("Tried to connect wormhole while not waiting for that!"));
            return false;
        }
        activeConnection.runOnBothConnected((conn, stargate) -> {
            var dm = ((StargateAbstractDialingManager<?>) stargate.getDialingManager());
            dm.scheduledTasks.clear();
            dm.setStargateState(conn.isInitiating() ? EnumStargateState.ENGAGED_INITIATING : EnumStargateState.ENGAGED);

            stargate.getSoundManager().updateWormholeSound(true);

            stargate.getStateManager().getBlackHoleAnimationState()
                    .setConnectedToBlackHole(
                            stargate.isBlackHoleEffected() || conn.callConnected((conn1, stargate1) -> stargate1.isBlackHoleEffected(), () -> false),
                            stargate.isBlackHoleEffected()
                    );

            conn.runOnConnected((c, sg) -> new StargateOpenedEvent(stargate, sg, conn.isInitiating()).post());

            StargateComputerEvents.WORMHOLE_OPEN_FULLY.apply(conn.isInitiating() ? stargate.getDialingManager().getDialedAddress() : new StargateAddressDynamic(stargate.getSymbolType()), conn.isInitiating()).sendVia(stargate);
        });
        activeConnection.updateStatus(StargateConnectionStatus.FULLY);
        return true;
    }

    /**
     * Tries to close active gate connection
     *
     * @param reason reason why it should close
     * @return true if success, otherwise false
     */
    @ApiStatus.Internal
    protected boolean closeGate(StargateClosedReasonEnum reason) {
        if (activeConnection.getStatus() != StargateConnectionStatus.FULLY) {
            JSG.logger.warn("", new StargateException("Tried to close gate while not fully opened!", stargate));
            stargate.getLogManager().error(Component.literal("Tried to close gate while not fully opened!"));
            return false;
        }
        activeConnection.runOnBothConnected((conn, stargate) -> {
            var dm = ((StargateAbstractDialingManager<?>) stargate.getDialingManager());
            dm.scheduledTasks.clear();
            stargate.getListenerHandler().gateClose(reason);
            dm.setStargateState(EnumStargateState.UNSTABLE_CLOSING);

            dm.addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_CLOSE, 62, Util.make(new CompoundTag(), (tag) -> tag.putInt("reason", reason.ordinal()))));

            dm.addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_CLOSE_SOUND, stargate.getCloseSoundDelay()));
            ((StargateAbstractStateManager<?, ?>) stargate.getStateManager()).sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLOSE_GATE, 0, false);
            stargate.getSoundManager().updateWormholeSound(false);

            StargateComputerEvents.WORMHOLE_CLOSE_UNSTABLE.apply(conn.isInitiating() ? stargate.getDialingManager().getDialedAddress() : new StargateAddressDynamic(stargate.getSymbolType()), reason, conn.isInitiating()).sendVia(stargate);

            JSG.logger.debug("Gate at {} closed!", stargate.blockPosition().toString());
            stargate.getLogManager().info(Component.literal("Gate closed! Reason: " + reason.name()));
        });
        activeConnection.updateStatus(StargateConnectionStatus.CLOSING);
        return true;
    }

    /**
     * Tries to disconnect the wormhole - resets the autoclose etc.
     *
     * @return true if success, otherwise false
     */
    @ApiStatus.Internal
    protected boolean disconnectWormhole(StargateClosedReasonEnum reason) {
        if (activeConnection.getStatus() != StargateConnectionStatus.CLOSING) {
            JSG.logger.warn("", new StargateException("Tried to disconnect wormhole while not closing!", stargate));
            stargate.getLogManager().error(Component.literal("Tried to disconnect wormhole while not closing!"));
            return false;
        }
        activeConnection.runOnBothConnected((conn, stargate) -> {
            var level = stargate.getStargateLevel();
            if (level != null && level.getBlockState(stargate.getGateCenterPos()).getBlock() == Blocks.LIGHT)
                level.setBlockAndUpdate(stargate.getGateCenterPos(), Blocks.AIR.defaultBlockState());
            var dm = ((StargateAbstractDialingManager<?>) stargate.getDialingManager());
            if (dm.incomingAnimation != null) {
                dm.incomingAnimation.stop();
            }
            StargateComputerEvents.WORMHOLE_CLOSE_FULLY.apply(conn.isInitiating() ? dm.getDialedAddress() : new StargateAddressDynamic(stargate.getSymbolType()), reason, conn.isInitiating()).sendVia(stargate);

            dm.addressDialSequence = null;
            dm.incomingAnimation = null;
            dm.scheduledTasks.clear();
            stargate.getListenerHandler().gateDisconnect();
            ((StargateAutoCloseManager) stargate.getAutoCloseManager()).wormholeDisconnected();
            dm.isFinalActive = false;
            stargate.getStateManager().getChevronsState().scheduleChevronsDimAll(0, true);
            dm.onWormholeDisconnected();
            dm.dialedAddress.clear();
            ((StargateEventHorizonManager) stargate.getEventHorizonManager()).onGateClose();
            stargate.setStargateChanged();

            new StargateClosedEvent(stargate).post();
        });
        disconnect();
        return true;
    }

    protected void onWormholeDisconnected() {
    }


    public void dialingFailed(StargateOpenResult reason) {
        scheduledTasks.clear();
        getConnection().updateStatus(StargateConnectionStatus.CLOSING);
        addressDialSequence = null;
        stargate.getListenerHandler().gateFail(reason);
        new StargateDialFailEvent(stargate, reason).post();

        addFailedTaskAndPlaySound();
        getSpinHelper().stopSpinning(true);
        setStargateState(EnumStargateState.FAILING);

        JSG.logger.info("Gate {} failed! Cause: {}", stargate.getStargatePos().toString(), reason.name());
        stargate.getLogManager().warn(Component.literal("Failed to open! Reason: " + reason.name()));

        StargateComputerEvents.ATTEMPT_OPEN_FAILED.apply(reason, getDialedAddress()).sendVia(stargate);

        stargate.setStargateChanged();
    }

    protected void addFailedTaskAndPlaySound() {
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_FAIL, 53));
        stargate.playSoundEvent(StargateSoundEventEnum.DIAL_FAILED);
    }

    /**
     * Resets the gate dialing sequence and mark it as dialing failed. Also disconnects target gate if it's already connected.
     */
    @ApiStatus.Internal
    protected void failGate() {
        getConnection().updateStatus(StargateConnectionStatus.CLOSING);
        disconnectSafe(StargateClosedReasonEnum.CONNECTION_LOST);
        dialedAddress.clear();
        isFinalActive = false;
        stargate.getStateManager().getChevronsState().scheduleChevronsDimAll(0, true);
        stargate.setStargateChanged();
    }

    /**
     * Make a virtual connection between two gates.
     *
     * @param targetGatePos the target stargate position
     * @return result of this connection attempt
     */
    @ApiStatus.Internal
    protected StargateConnectResult connect(StargateAddressDynamic address, StargatePos targetGatePos, boolean isNox, boolean noEnergy) {
        if (!activeConnection.getStatus().none()) {
            JSG.logger.error("", new StargateException("Tried to connect to a gate when already connected to a gate", stargate));
            stargate.getLogManager().error(Component.literal("Tried to connect to a gate when already connected to a gate!"));
            return StargateConnectResult.ALREADY_CONNECTED;
        }
        if (stargate.isGateBurried()) {
            return StargateConnectResult.GATE_BURRIED;
        }
        var targetStargate = targetGatePos.getStargate();
        if (targetStargate == null) {
            return StargateConnectResult.ADDRESS_MALFORMED_SGN_OK;
        }

        var result = checkAddressAndEnergyRequirements(address, true); // we don't want to check energy here - only when establishing the wormhole
        if (result != StargateAddressCheckResult.OK) {
            return result.toConnectResult();
        }

        if (targetStargate.isGateBurried()) {
            return StargateConnectResult.TARGET_GATE_BURRIED;
        }
        var targetDm = ((StargateAbstractDialingManager<?>) targetStargate.getDialingManager());
        var targetConnection = targetDm.activeConnection;
        if (!targetConnection.getStatus().none() && targetConnection.getTarget().map(p -> !stargate.is(p)).orElse(false)) {
            return StargateConnectResult.TARGET_BUSY;
        }

        if (!activeConnection.establish(targetGatePos, isNox, noEnergy)) {
            JSG.logger.error("", new BiStargateException("Cannot establish connection between specified gates", stargate, targetStargate));
            stargate.getLogManager().error(Component.literal("Cannot establish connection between gates! Target: " + targetStargate.getStargatePos()));
            return StargateConnectResult.ADDRESS_MALFORMED;
        }
        activeConnection.runOnBothConnected((conn, stargate) -> {
            ChunkManager.forceChunk(stargate.getStargateLevel(), stargate.blockPosition());
            StargateComputerEvents.WORMHOLE_SUBSPACE_CONNECTED.apply((conn.isInitiating() ? address : new StargateAddressDynamic(stargate.getSymbolType())), conn.isInitiating()).sendVia(stargate);
        });
        return StargateConnectResult.OK;
    }

    /**
     * Destroy virtual connection between connected gates
     */
    @ApiStatus.Internal
    protected boolean disconnect() {
        activeConnection.runOnBothConnected((conn, stargate) -> {
            StargateComputerEvents.WORMHOLE_SUBSPACE_DISCONNECTED.get().sendVia(stargate);
            stargate.getStateManager().getBlackHoleAnimationState().setConnectedToBlackHole(false, false);
            if (((StargateAbstractDialingManager<?>) stargate.getDialingManager()).canUnforceChunk())
                ChunkManager.unforceChunk(stargate.getStargateLevel(), stargate.blockPosition());
        });
        activeConnection.updateStatus(StargateConnectionStatus.NONE);
        return true;
    }

    protected boolean canUnforceChunk() {
        return true;
    }

    public void onGateUnmerged() {
        getSpinHelper().stopSpinning(true);
        disconnectSafe(StargateClosedReasonEnum.CONNECTION_LOST);
        dialedAddress.clear();
        setStargateState(EnumStargateState.IDLE);
    }

    protected IncomingAnimation<?> getIncomingAnimationByConfig(CompoundTag tag) {
        boolean animate = JSGConfig.Stargate.allowIncomingAnimations.get();
        if (stargate instanceof IConfigurable configurable)
            animate = configurable.getConfig().getValueOrDefault(StargateConfigOptions.Classic.INCOMING_ANIMATION);
        if (animate)
            return getIncomingAnimation(tag);
        return getIncomingAnimationStatic(tag);
    }

    protected IncomingAnimation<?> getIncomingAnimationByConfig(int addressSize, int duration) {
        boolean animate = JSGConfig.Stargate.allowIncomingAnimations.get();
        if (stargate instanceof IConfigurable configurable)
            animate = configurable.getConfig().getValueOrDefault(StargateConfigOptions.Classic.INCOMING_ANIMATION);
        if (animate)
            return getIncomingAnimation(addressSize, duration);
        return getIncomingAnimationStatic(addressSize, duration);
    }

    protected IncomingAnimation<?> getIncomingAnimation(CompoundTag tag) {
        return new IncomingAnimation<>(this, tag);
    }

    protected IncomingAnimation<?> getIncomingAnimation(int addressSize, int duration) {
        return new IncomingAnimation<>(stargate.getTime(), this, addressSize, duration);
    }

    protected IncomingAnimation<?> getIncomingAnimationStatic(CompoundTag tag) {
        return new StaticIncomingAnimation(this, tag);
    }

    protected IncomingAnimation<?> getIncomingAnimationStatic(int addressSize, int duration) {
        return new StaticIncomingAnimation(stargate.getTime(), this, addressSize, duration);
    }

    protected void onIncoming(int addressSize, int duration) {

    }

    public void runIncomingWormhole(int addressSize, int duration) {
        if (!getConnection().getStatus().prepared() && !getConnection().getStatus().waiting()) {
            JSG.logger.error("", new StargateException("Gate must be prepared to accept incoming", stargate));
            stargate.getLogManager().error(Component.literal("Gate must be prepared to accept incoming!"));
            return;
        }
        if (incomingAnimation != null) {
            JSG.logger.error("", new StargateException("Tried to run incoming animation while already running one!", stargate));
            stargate.getLogManager().error(Component.literal("Tried to run incoming animation while already running one!"));
            return;
        }
        scheduledTasks.clear();
        stargate.getStateManager().getChevronsState().scheduleChevronsDimAll(0, false);
        getSpinHelper().stopSpinning(true);
        dialedAddress.clear();
        addressDialSequence = null;
        incomingAnimation = getIncomingAnimationByConfig(addressSize, duration);
        incomingAnimation.start();
        setStargateState(EnumStargateState.INCOMING);
        stargate.getListenerHandler().gateIncoming(addressSize);
        onIncoming(addressSize, duration);
        stargate.setStargateChanged();

        StargateComputerEvents.WORMHOLE_INCOMING.apply(addressSize).sendVia(stargate);
    }

    protected void clearIncomingWormholeOnSelf() {
        setStargateState(EnumStargateState.IDLE);
    }

    protected void clearIncomingWormholeOnIncoming() {
        if (activeConnection.getStatus() != StargateConnectionStatus.CLOSING) {
            return;
        }
        getConnection().runOnBothConnected((conn, sg) -> {
            if (conn.isInitiating()) return;
            var dm = ((StargateAbstractDialingManager<?>) sg.getDialingManager());
            dm.incomingAnimation = null;
            dm.scheduledTasks.clear();
            dm.clearIncomingWormholeOnSelf();
            dm.isFinalActive = false;
            dm.setStargateState(EnumStargateState.IDLE);
            sg.setStargateChanged();
        });
        disconnect();
    }

    // -------------------------------------------
    // ADDRESS

    public StargateAddressCheckResult checkEnergyRequirementsOnDialed() {
        if (getConnection().isRIG())
            return StargateAddressCheckResult.OK;
        return getConnection().callConnected((conn, sg) -> {
            if (!conn.getStatus().waiting()) return StargateAddressCheckResult.MALFORMED;
            if (conn.withoutEnergy()) return StargateAddressCheckResult.OK;
            var energyRequired = stargate.getEnergyManager().getEnergyRequiredToDial(sg, dialedAddress);
            if (stargate.getEnergyManager().getStorage().getEnergyStored() < energyRequired.energyToOpen)
                return StargateAddressCheckResult.NOT_ENOUGH_POWER;
            return StargateAddressCheckResult.OK;
        }, () -> StargateAddressCheckResult.MALFORMED);
    }

    public StargateAddressCheckResult checkAddressAndEnergyRequirements(StargateAddressDynamic address, boolean noEnergy) {
        var result = getTargetByAddress(address, true);
        if (result.first() != StargateAddressCheckResult.OK) return result.first();
        if (noEnergy) return StargateAddressCheckResult.OK;
        var energyRequired = stargate.getEnergyManager().getEnergyRequiredToDial(result.second(), address);
        if (stargate.getEnergyManager().getStorage().getEnergyStored() < energyRequired.energyToOpen)
            return StargateAddressCheckResult.NOT_ENOUGH_POWER;
        return StargateAddressCheckResult.OK;
    }

    @Override
    public boolean canDialAddress(StargateAddressDynamic address, boolean checkTarget) {
        return getTargetByAddress(address, checkTarget).first() == StargateAddressCheckResult.OK;
    }

    public Pair<StargateAddressCheckResult, Stargate<?>> getTargetByAddress(StargateAddressDynamic address, boolean checkTarget) {
        if (!address.validate()) {
            return Pair.of(StargateAddressCheckResult.MALFORMED, null);
        }

        var targetPosOptional = getDialableStargatePos(address);
        if (targetPosOptional.isEmpty()) return Pair.of(StargateAddressCheckResult.MALFORMED, null);
        if (!checkTarget)
            return Pair.of(StargateAddressCheckResult.OK, null);

        var targetTile = targetPosOptional.get().getStargate();
        if (targetTile == null) return Pair.of(StargateAddressCheckResult.MALFORMED, null);

        if (!targetTile.getDialingManager().canAcceptConnectionFrom(stargate.getStargatePos())) {
            return Pair.of(StargateAddressCheckResult.TARGET_BUSY, targetTile);
        }

        return Pair.of(StargateAddressCheckResult.OK, targetTile);
    }

    public Optional<StargatePos> getDialableStargatePos(StargateAddressDynamic address) {
        StargatePos targetGatePos = network.get().getStargate(address);
        if (targetGatePos == null || stargate.is(targetGatePos))
            return Optional.empty();
        if (isAddressLengthOk(address, targetGatePos))
            return Optional.of(targetGatePos);
        return Optional.empty();
    }

    public boolean isAddressLengthOk(StargateAddressDynamic address, StargatePos targetGatePosition) {
        boolean localDial = JSGDimensionConfig.INSTANCE.isGroupEqual(stargate.getStargatePos().dimension, targetGatePosition.dimension);
        return address.size() >= stargate.getSymbolType().getMinimalSymbolCountTo(targetGatePosition.getGateSymbolType(), localDial);
    }

    public boolean canAcceptConnectionFrom(@Nullable StargatePos targetGatePos) {
        if (!stargate.isMerged()) return false;

        if (targetGatePos != null && stargate.is(targetGatePos))
            return false;

        boolean allowConnectToDialing = JSGConfig.Stargate.allowConnectToDialing.get();

        var state = getStargateState();
        if (allowConnectToDialing) {
            return switch (state) {
                case IDLE, DIALING, DIALING_COMPUTER, INCOMING -> true;
                default -> false;
            };
        }
        return state.idle() || state.incoming();
    }

    public boolean isLockChevron(@Nullable SymbolInterface symbol, boolean notAddedYet, boolean ignoreMaxChevrons) {
        if ((dialedAddress.size() + (notAddedYet ? 1 : 0)) >= (ignoreMaxChevrons ? 9 : stargate.getMaxChevrons()))
            return true;
        if (symbol == null) return false;

        return (dialedAddress.size() + (notAddedYet ? 1 : 0)) >= 7 && symbol.origin();
    }

    public boolean isLockChevron(@Nullable SymbolInterface symbol, boolean ignoreMaxChevrons) {
        return isLockChevron(symbol, false, ignoreMaxChevrons);
    }

    public StargateChevronEngageResult canAddSymbol(SymbolInterface symbol, boolean ignoreMaxChevrons) {
        var r = canAddSymbolInternal(symbol, ignoreMaxChevrons);
        if (r != StargateChevronEngageResult.OK) return r;
        if (new StargateChevronEngagedEvent(stargate, symbol, isLockChevron(symbol, ignoreMaxChevrons)).post())
            return StargateChevronEngageResult.BLOCKED_BY_EVENT;
        return StargateChevronEngageResult.OK;
    }

    protected StargateChevronEngageResult canAddSymbolInternal(SymbolInterface symbol, boolean ignoreMaxChevrons) {
        if (isFinalActive) return StargateChevronEngageResult.ADDRESS_FULL;
        if (dialedAddress.contains(symbol)) return StargateChevronEngageResult.ALREADY_ENGAGED;
        return (((ignoreMaxChevrons && dialedAddress.size() < 9) || dialedAddress.size() < stargate.getMaxChevrons()) ? StargateChevronEngageResult.OK : StargateChevronEngageResult.ADDRESS_FULL);
    }

    public int getMinimalSymbolsToDial(SymbolType<?> symbolType, StargatePos targetGatePos) {
        var level = stargate.getStargateLevel();
        if (level == null) return 9;
        return stargate.getSymbolType().getMinimalSymbolCountTo(symbolType, JSGDimensionConfig.INSTANCE.isGroupEqual(targetGatePos.dimension, level.dimension()));
    }

    /**
     * @param customData
     * @return
     */
    @Nullable
    public StargateChevronEngageResult onChevronActivates(CompoundTag customData) {
        if (!customData.contains("symbol")) return null;
        var symbol = stargate.getSymbolType().valueOf(customData.getInt("symbol"));
        if (symbol == null)
            return StargateChevronEngageResult.BLOCKED_BY_EVENT;
        var result = engageSymbolInternal(symbol, customData.getBoolean("noxDialing"), customData.getBoolean("noEnergy"), customData.getBoolean("ignoreMaxChevrons"));
        if (!result.ok())
            return result;
        StargateComputerEvents.CHEVRON_ENGAGED.apply(StargateComputerEvents.ChevronEvent.Source.BY_SPIN, symbol, getNextChevron(symbol, true, customData.getBoolean("ignoreMaxChevrons")), getDialedAddressSize()).sendVia(stargate);
        if (customData.getBoolean("checkConnection") && customData.getBoolean("isFinal") && !getConnection().getStatus().waiting())
            return StargateChevronEngageResult.FAILED_FAIL_GATE;
        if (addressDialSequence != null)
            addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_DIAL_NEXT, A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY + 10));
        return StargateChevronEngageResult.OK;
    }


    // -------------------------------------------
    // SCHEDULED TASKS

    protected final List<ScheduledTask> scheduledTasks = new ArrayList<>();

    @Override
    public void addTask(ScheduledTask scheduledTask) {
        scheduledTask.setExecutor(this);
        scheduledTask.setTaskCreated(stargate.getTime());

        if (scheduledTask.getWaitTime() <= 0) {
            scheduledTask.execute();
            return;
        }

        scheduledTasks.add(scheduledTask);
        stargate.setStargateChanged();
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        // stargate operations
        if (scheduledTask == JSGScheduledTaskTypes.STARGATE_OPEN_SOUND.get()) {
            if (activeConnection.isNox())
                stargate.playSoundEvent(StargateSoundEventEnum.OPEN_NOX);
            else
                stargate.playSoundEvent(StargateSoundEventEnum.OPEN);
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CLOSE_SOUND.get()) {
            stargate.playSoundEvent(StargateSoundEventEnum.CLOSE);
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CLOSE.get()) {
            disconnectWormhole(StargateClosedReasonEnum.values()[customData.getInt("reason")]);
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_FAIL.get()) {
            failGate();
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_ENGAGE.get()) {
            connectWormhole();
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_DIAL_NEXT.get()) {
            if (addressDialSequence != null)
                addressDialSequence.dialNext();
        }
    }

    // ------------------------------------------
    // TICKING

    @Override
    public void tick(Level level) {
        ScheduledTask.iterate(scheduledTasks, stargate.getTime());
        tickConnection.accept(activeConnection);
        activeConnection.tick(level);
        if (incomingAnimation != null)
            incomingAnimation.tick(level);
    }

    public void onLoad(Level level) {
    }

    // ------------------------------------------
    // NBT

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.put("activeConnection", activeConnection.serializeNBT());
        compound.put("scheduledTasks", ScheduledTask.serializeList(scheduledTasks));
        compound.put("dialedAddress", dialedAddress.serializeNBT());
        compound.putBoolean("isFinalActive", isFinalActive);
        compound.putInt("stargateState", stargateState.ordinal());
        if (incomingAnimation != null)
            compound.put("incomingAnimation", incomingAnimation.serializeNBT());
        if (addressDialSequence != null)
            compound.put("addressDialSequence", addressDialSequence.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        activeConnection.deserializeNBT(compound.getCompound("activeConnection"));
        dialedAddress.clear();
        dialedAddress.deserializeNBT(compound.getCompound("dialedAddress"));
        try {
            ScheduledTask.deserializeList(compound.getCompound("scheduledTasks"), scheduledTasks, this);
        } catch (NullPointerException | IndexOutOfBoundsException | ClassCastException e) {
            JSG.logger.warn("Exception at reading NBT");
            JSG.logger.warn("If loading world used with previous version and nothing game-breaking doesn't happen, please ignore it", e);
        }
        isFinalActive = compound.getBoolean("isFinalActive");
        stargateState = EnumStargateState.valueOf(compound.getInt("stargateState"));
        if (compound.contains("incomingAnimation"))
            incomingAnimation = getIncomingAnimationByConfig(compound.getCompound("incomingAnimation"));
        if (compound.contains("addressDialSequence"))
            addressDialSequence = getAddressDialSequence(compound.getCompound("addressDialSequence"));
    }


    // ------------------------------------------
}
