package dev.tauri.jsg.common.stargate.manager.dialing;

import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.common.stargate.animation.spinning.ClassicSpinHelper;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class StargateClassicDialingManager<SG extends StargateClassicBaseBE<?>> extends StargateAbstractDialingManager<SG> {
    protected final ClassicSpinHelper spinHelper;

    public StargateClassicDialingManager(SG stargate) {
        super(stargate);
        this.spinHelper = generateSpinHelper();
    }

    public ClassicSpinHelper generateSpinHelper() {
        return new ClassicSpinHelper(stargate, this::onRingStopSpinning);
    }

    public ClassicSpinHelper getSpinHelper() {
        return spinHelper;
    }

    @Override
    public void tick(Level level) {
        super.tick(level);
        getSpinHelper().tick(level);
    }

    @Override
    public void onLoad(Level level) {
        super.onLoad(level);
        getSpinHelper().onLoad(level);
    }

    @Override
    protected StargateOpenResult getCanOpenDialed() {
        if (stargate.isGateBurried()) {
            return StargateOpenResult.GATE_BURRIED;
        }
        if (getConnection().callConnected((c, sg) -> sg.isGateBurried(), () -> false))
            return StargateOpenResult.TARGET_GATE_BURRIED;
        return super.getCanOpenDialed();
    }

    @Override
    protected boolean openGate() {
        if (!super.openGate())
            return false;
        stargate.getSoundManager().updateRingRollSound(false);
        stargate.tryHeatUp(8);
        stargate.setChanged();
        return true;
    }

    @Override
    protected void onWormholeDisconnected() {
        super.onWormholeDisconnected();
        stargate.getSoundManager().updateRingRollSound(false);
        stargate.getIrisManager().onGateDisconnected();
    }

    @Override
    protected void onIncoming(int addressSize, int duration) {
        super.onIncoming(addressSize, duration);
        stargate.getIrisManager().onIncomingWormhole();
    }

    @Override
    protected void failGate() {
        super.failGate();
        stargate.getSoundManager().updateRingRollSound(false);
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        if (scheduledTask == JSGScheduledTaskTypes.STARGATE_CLOSE.get()) {
            super.executeTask(scheduledTask, customData);
            var iris = stargate.getIrisManager();
            if (iris.isIrisClosed() && iris.getIrisMode() == EnumIrisMode.AUTO)
                iris.toggleIris();
        } else if (scheduledTask == JSGScheduledTaskTypes.GATE_RING_ROLL.get()) {
            stargate.getSoundManager().updateRingRollSound(true);
        } else
            super.executeTask(scheduledTask, customData);
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = super.serializeNBT();
        compound.put("spinHelper", spinHelper.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        super.deserializeNBT(compound);
        spinHelper.deserializeNBT(compound.getCompound("spinHelper"));
    }
}
