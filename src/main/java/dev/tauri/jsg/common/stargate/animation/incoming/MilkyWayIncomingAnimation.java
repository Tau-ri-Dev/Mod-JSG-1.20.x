package dev.tauri.jsg.common.stargate.animation.incoming;

import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.animation.EnumSpinDirection;
import dev.tauri.jsg.common.blockentity.stargate.StargateMilkyWayBaseBE;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateAbstractDialingManager;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public class MilkyWayIncomingAnimation extends IncomingAnimation<StargateAbstractDialingManager<? extends StargateMilkyWayBaseBE>> {
    public MilkyWayIncomingAnimation(StargateAbstractDialingManager<? extends StargateMilkyWayBaseBE> dialer, CompoundTag tag) {
        super(dialer, tag);
    }

    public MilkyWayIncomingAnimation(long time, StargateAbstractDialingManager<? extends StargateMilkyWayBaseBE> dialer, int addressLength, int duration) {
        super(time, dialer, addressLength, duration);
    }

    protected boolean shouldLockWhenStop = false;

    @Override
    public void start() {
        super.start();
        if (duration < 30) return;
        if (dialer.stargate.getConfig().getValueOrDefault(StargateConfigOptions.MilkyWay.INCOMING_RING_SPIN))
            dialer.getSpinHelper().rotateFreely(EnumSpinDirection.random(new Random()));
    }

    @Override
    public void stop() {
        super.stop();
        dialer.getSpinHelper().stopSpinning(true);
        if (shouldLockWhenStop) {
            dialer.stargate.getStateManager().getChevronsState().scheduleChevronFinalOpenAndActivate((int) dialer.getSpinHelper().getTickToStop(), null, false);
            shouldLockWhenStop = false;
        }
    }

    @Override
    public void runPart(int partIndex) {
        super.runPart(partIndex);
        if (partIndex >= getParts() - 1) {
            shouldLockWhenStop = true;
            return;
        }
        dialer.stargate.getStateManager().getChevronsState().scheduleChevronActivate(0, ChevronEnum.valueOf(partIndex % 9), null, false);
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        super.deserializeNBT(compoundTag);
        shouldLockWhenStop = compoundTag.getBoolean("shouldLockWhenStop");
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = super.serializeNBT();
        compound.putBoolean("shouldLockWhenStop", shouldLockWhenStop);
        return compound;
    }
}
