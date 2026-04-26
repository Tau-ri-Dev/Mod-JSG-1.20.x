package dev.tauri.jsg.common.stargate.animation.chevron;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.exception.StargateException;
import dev.tauri.jsg.api.stargate.result.StargateChevronEngageResult;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.common.stargate.manager.state.StargateMovieStateManager;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.util.math.MathFunction;
import dev.tauri.jsg.core.common.util.math.MathFunctionImpl;
import dev.tauri.jsg.core.common.util.math.MathRange;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class StargateMovieChevronsState extends StargateChevronsState {
    public StargateMovieChevronsState(StargateMovieStateManager stateManager) {
        super(stateManager);
    }

    public void openDialed() {
        for (int i = 0; i < stateManager.stargate.getDialingManager().getDialedAddressSize() - 1; i++) {
            get(ChevronEnum.valueOf(i)).open();
        }
    }

    protected static final MathRange CHEVRON_RANGE = new MathRange(0, 0.08333f * 6f);
    protected static final MathFunction CHEVRON_OPEN_FUNCTION = new MathFunctionImpl(x -> x / 6f);
    protected static final MathFunction CHEVRON_CLOSE_FUNCTION = new MathFunctionImpl(x -> (0.08333f * 6f - x) / 6f);

    @Override
    public MathRange getChevronOpenRange() {
        return CHEVRON_RANGE;
    }

    @Override
    public MathRange getChevronCloseRange() {
        return CHEVRON_RANGE;
    }

    @Override
    public MathFunction getChevronOpenFunction() {
        return CHEVRON_OPEN_FUNCTION;
    }

    @Override
    public MathFunction getChevronCloseFunction() {
        return CHEVRON_CLOSE_FUNCTION;
    }

    @Override
    protected void onChevronFailedToLock(boolean checkingForConnection) {
        if (!checkingForConnection && !isOpenedAny())
            return;
        scheduleChevronFail(60, ChevronEnum.getFinal(), checkingForConnection);
    }

    @Override
    public void scheduleChevronsLockAll(int waitTicks, boolean setIdle) {
        var data = new CompoundTag();
        data.putBoolean("setIdle", setIdle);
        data.putBoolean("litAll", true);
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_CHEVRON_OPEN, waitTicks, data));
    }

    @Override
    public void scheduleChevronActivate(int waitTicks, ChevronEnum chevron, @org.jetbrains.annotations.Nullable SymbolInterface symbolToLock, boolean setIdle, boolean checkConnection, boolean animateTopChevronLock, boolean noEnergy, boolean ignoreMaxChevrons) {
        var data = new CompoundTag();
        data.putInt("chevron", chevron.getKey());
        data.putBoolean("isFinal", chevron.isFinal());
        data.putBoolean("setIdle", setIdle);
        data.putBoolean("checkConnection", checkConnection);
        data.putBoolean("noEnergy", noEnergy);
        data.putBoolean("ignoreMaxChevrons", ignoreMaxChevrons);
        data.putBoolean("animateTopChevronLock", animateTopChevronLock);
        if (symbolToLock != null)
            data.putInt("symbol", symbolToLock.getId());
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_CHEVRON_OPEN, waitTicks, data));
    }

    @Override
    public void scheduleChevronDim(int waitTicks, ChevronEnum chevron, boolean setIdle) {
        JSG.logger.error("", new StargateException("Tried to schedule chevron dim on a movie gate!", stateManager.stargate));
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        var stargate = stateManager.stargate;
        var dialingManager = stargate.getDialingManager();
        if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_LIGHT_UP.get()) {
            JSG.logger.error("", new StargateException("Tried to light up chevron on a movie gate!", stargate));
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_OPEN.get()) {
            stargate.playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
            var result = dialingManager.onChevronActivates(customData);
            if (result != null && result != StargateChevronEngageResult.OK) {
                if (result == StargateChevronEngageResult.FAILED_FAIL_GATE)
                    stargate.getStateManager().getChevronsState().onChevronFailedToLock(true);
                else
                    stargate.getStateManager().getChevronsState().onChevronFailedToLock(customData.getBoolean("checkConnection"));
            } else {
                if (customData.getBoolean("setIdle"))
                    dialingManager.setStargateState(EnumStargateState.IDLE);
                if (customData.getBoolean("litAll")) {
                    openAll();
                } else {
                    if (customData.getBoolean("isFinal"))
                        openDialed();
                    else
                        get(customData.getInt("chevron"), customData.getBoolean("isFinal")).open();
                }
                if (customData.getBoolean("animateTopChevronLock")) {
                    if (!customData.getBoolean("isFinal"))
                        scheduleChevronClose(A_CHEVRON_CLOSE_AFTER_ACTIVATE_DELAY * 2, ChevronEnum.valueOf(customData.getInt("chevron")), true);
                    else
                        stargate.getDialingManager().setStargateState(EnumStargateState.IDLE);
                }
            }
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_CLOSE.get()) {
            stargate.playSoundEvent(StargateSoundEventEnum.CHEVRON_SHUT);
            get(customData.getInt("chevron"), customData.getBoolean("isFinal")).close();
            if (customData.getBoolean("setIdle"))
                dialingManager.setStargateState(EnumStargateState.IDLE);
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_FAIL.get()) {
            if (customData.getBoolean("setIdle"))
                dialingManager.dialingFailed(StargateOpenResult.ADDRESS_MALFORMED);
            closeAll();
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CHEVRON_DIM.get()) {
            JSG.logger.error("", new StargateException("Tried to dim chevron on a movie gate!", stargate));
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CLEAR_CHEVRONS.get()) {
            closeAll();
            if (customData.getBoolean("setIdle"))
                dialingManager.setStargateState(EnumStargateState.IDLE);
        } else if (scheduledTask == JSGScheduledTaskTypes.LIGHT_UP_CHEVRONS.get()) {
            // We are in Movie gate which cannot light up it's chevrons - open them instead then
            if (customData.getBoolean("playSound"))
                stargate.playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
            if (customData.contains("chevrons")) {
                var chevrons = new ArrayList<ChevronEnum>();
                for (var i : customData.getIntArray("chevrons"))
                    chevrons.add(ChevronEnum.valueOf(i));
                openMultiple(chevrons);
            } else
                openAll();
            if (customData.getBoolean("setIdle"))
                dialingManager.setStargateState(EnumStargateState.IDLE);
        } else
            super.executeTask(scheduledTask, customData);
    }
}
