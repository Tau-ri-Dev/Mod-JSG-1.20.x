package dev.tauri.jsg.common.stargate.manager.dialing;

import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.animation.EnumDialingType;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.result.StargateChevronEngageResult;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.common.blockentity.stargate.StargateOrlinBaseBE;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.stargate.animation.incoming.IncomingAnimation;
import dev.tauri.jsg.common.stargate.animation.incoming.StaticIncomingAnimation;
import dev.tauri.jsg.common.stargate.animation.spinning.ClassicSpinHelper;
import dev.tauri.jsg.common.stargate.animation.spinning.OrlinSpinHelper;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class StargateOrlinDialingManager extends StargateAbstractDialingManager<StargateOrlinBaseBE> {
    protected final ClassicSpinHelper spinHelper;
    protected final StargateAddressDynamic addressToDial;

    public StargateOrlinDialingManager(StargateOrlinBaseBE stargate) {
        super(stargate);
        this.spinHelper = generateSpinHelper();
        this.addressToDial = new StargateAddressDynamic(stargate.getSymbolType());
    }

    @Override
    public ClassicSpinHelper generateSpinHelper() {
        return new OrlinSpinHelper(stargate);
    }

    @Override
    public ClassicSpinHelper getSpinHelper() {
        return spinHelper;
    }

    @Override
    protected IncomingAnimation<?> getIncomingAnimation(CompoundTag tag) {
        return new StaticIncomingAnimation(this, tag);
    }

    @Override
    protected IncomingAnimation<?> getIncomingAnimation(int addressSize, int duration) {
        return new StaticIncomingAnimation(stargate.getTime(), this, addressSize, duration);
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        if (scheduledTask == JSGScheduledTaskTypes.STARGATE_ORLIN_BROKE_SOUND.get()) {
            stargate.playSoundEvent(StargateSoundEventEnum.GATE_BROKE);
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_ORLIN_OPEN.get()) {
            if (!getStargateState().dialing()) return;
            if (getDialedAddressSize() < 7) return;
            setStargateState(EnumStargateState.IDLE);
            attemptOpenDialed();
        } else super.executeTask(scheduledTask, customData);
    }

    public void updateDialedAddress() {
        var addressToDial = stargate.getAddressFromPageNBT();
        this.addressToDial.clear();
        if (addressToDial == null) return;
        var address = new StargateAddressDynamic(addressToDial);
        var addressNeeded = address.subList(0, 6);
        addressNeeded.add(stargate.getSymbolType().getOrigin());
        this.addressToDial.addAll(addressNeeded);
        stargate.setChanged();
    }

    @Override
    protected void onWormholeDisconnected() {
        super.onWormholeDisconnected();
        if (getConnection().isInitiating() && getConnection().wasFully()) {
            stargate.incrementOpenCount();
            if (stargate.isBroken(false) && stargate.getLevel() != null) {
                addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_ORLIN_BROKE_SOUND, 10));
            }
        }
    }

    public void beginOpening() {
        if (stargate.getLevel() == null)
            return;
        if (stargate.isBroken(false)) {
            failGate();
            return;
        }
        updateDialedAddress();
        if (getDialedAddressSize() > 0) {
            attemptOpenDialed();
            return;
        }

        var chevronIndex = 0;
        var lastChevronWait = getTicksToChevron(6);
        for (var symbol : addressToDial) {
            var r = engageSymbolInternal(symbol, false, false, false);
            if (!r.ok()) {
                stargate.getStateManager().getChevronsState().clearTasks();
                dialingFailed(StargateOpenResult.ADDRESS_MALFORMED);
                return;
            }
            stargate.getStateManager().getChevronsState().scheduleChevronActivate(getTicksToChevron(chevronIndex), ChevronEnum.valueOf((chevronIndex == 6 ? 8 : chevronIndex)), null, false);
            chevronIndex++;
            if (r == StargateChevronEngageResult.OK_CONNECTED) {
                getConnection().runOnConnected((conn, sg) -> ((StargateAbstractDialingManager<?>) sg.getDialingManager()).runIncomingWormhole(dialedAddress.addOriginIfMissingAndImmutable().size(), lastChevronWait));
            }
        }
        stargate.playSoundEvent(JSGSoundEvents.GATE_ORLIN_DIAL);
        setStargateState(EnumStargateState.DIALING);
        addTask(new ScheduledTask(JSGScheduledTaskTypes.STARGATE_ORLIN_OPEN, lastChevronWait + 10));
    }

    public int getTicksToChevron(int chevronIndex) {
        return new int[]{
                (int) (0.09 * 20.0),
                (int) (1.253 * 20.0),
                (int) (2.822 * 20.0),
                (int) (3.795 * 20.0),
                (int) (5.573 * 20.0),
                (int) (6.366 * 20.0),
                (int) (7.655 * 20.0)
        }[Math.min(6, chevronIndex)];
    }

    @Override
    public StargateChevronEngageResult dialAddress(StargateAddressDynamic address, boolean noEnergy, boolean ignoreMaxChevrons, EnumDialingType dialingType) {
        return StargateChevronEngageResult.BLOCKED_BY_EVENT;
    }
}
