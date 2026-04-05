package dev.tauri.jsg.stargate.manager.dialing;

import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.result.StargateChevronEngageResult;
import dev.tauri.jsg.blockentity.stargate.StargateMovieBaseBE;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.stargate.animation.incoming.IncomingAnimation;
import dev.tauri.jsg.stargate.animation.incoming.MovieIncomingAnimation;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

import static dev.tauri.jsg.stargate.animation.chevron.StargateChevronsState.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StargateMovieDialingManager extends StargateMilkyWayDialingManager {
    public StargateMovieDialingManager(StargateMovieBaseBE stargate) {
        super(stargate);
    }

    @Override
    protected IncomingAnimation<?> getIncomingAnimation(CompoundTag tag) {
        return new MovieIncomingAnimation(this, tag);
    }

    @Override
    protected IncomingAnimation<?> getIncomingAnimation(int addressSize, int duration) {
        return new MovieIncomingAnimation(stargate.getTime(), this, addressSize, duration);
    }

    @Override
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
            addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_DIAL_NEXT, (A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY * 2) + 10));
        return StargateChevronEngageResult.OK;
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
        stargate.getStateManager().getChevronsState().scheduleChevronActivate(10 + plusTime, getNextChevron(symbol, true, ignoreMaxChevrons), null, true, false, true, noEnergy, ignoreMaxChevrons);
        if (addressDialSequence != null)
            addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_DIAL_NEXT, 15 + plusTime + A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY * 2));
        stargate.setChanged();
        return r;
    }

    @Override
    protected void onRingStopSpinning(CompoundTag stopSpinData) {
        if (stargate.getStargateLevel() == null || stargate.getStargateLevel().isClientSide()) return;
        if (stopSpinData.contains("symbol")) {
            var symbol = stargate.getSymbolType().valueOf(stopSpinData.getInt("symbol"));
            var chevron = getNextChevron(symbol, false, stopSpinData.getBoolean("ignoreMaxChevrons"));
            stargate.getStateManager().getChevronsState().scheduleChevronActivate(A_CHEVRON_OPEN_AFTER_STOPPED_DELAY, chevron, symbol, false, true, true, stopSpinData.getBoolean("noEnergy"), stopSpinData.getBoolean("ignoreMaxChevrons"));
        } else if (getStargateState().dialingComputer())
            setStargateState(EnumStargateState.IDLE);
    }

    @Override
    public StargateChevronEngageResult engageCurrentSymbol() {
        var symbol = getSpinHelper().getCurrentTopSymbol();
        if (symbol == null) return StargateChevronEngageResult.BLOCKED_BY_EVENT;
        stargate.getStateManager().getChevronsState().scheduleChevronActivate(A_CHEVRON_OPEN_AFTER_STOPPED_DELAY + A_CHEVRON_ACTIVATE_AFTER_OPEN_DELAY, getNextChevron(symbol, false, false), symbol, false, true, true);
        return StargateChevronEngageResult.OK;
    }
}
