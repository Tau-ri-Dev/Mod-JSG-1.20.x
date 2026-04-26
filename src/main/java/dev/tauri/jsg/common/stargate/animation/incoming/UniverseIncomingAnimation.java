package dev.tauri.jsg.common.stargate.animation.incoming;

import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateUniverseDialingManager;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import net.minecraft.nbt.CompoundTag;

public class UniverseIncomingAnimation extends IncomingAnimation<StargateUniverseDialingManager> {
    public UniverseIncomingAnimation(StargateUniverseDialingManager dialer, CompoundTag tag) {
        super(dialer, tag);
    }

    public UniverseIncomingAnimation(long time, StargateUniverseDialingManager dialer, int addressLength, int duration) {
        super(time, dialer, addressLength, duration);
    }

    @Override
    public void start() {
        super.start();
        dialer.stargate.playSoundEvent(StargateSoundEventEnum.INCOMING);
        dialer.stargate.getStateManager().getChevronsState().scheduleChevronsLockAll(0, false);
        if (duration < 100) return;
        var data = new CompoundTag();
        data.putBoolean("freely", true);
        dialer.addTask(new ScheduledTask(JSGScheduledTaskTypes.BEGIN_SPIN, 10, data));
    }

    @Override
    public void stop() {
        super.stop();
        dialer.getSpinHelper().stopSpinning(true);
    }
}
