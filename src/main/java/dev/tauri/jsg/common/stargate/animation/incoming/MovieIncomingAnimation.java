package dev.tauri.jsg.common.stargate.animation.incoming;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.animation.EnumSpinDirection;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateMovieDialingManager;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public class MovieIncomingAnimation extends IncomingAnimation<StargateMovieDialingManager> {
    public MovieIncomingAnimation(StargateMovieDialingManager dialer, CompoundTag tag) {
        super(dialer, tag);
    }

    public MovieIncomingAnimation(long time, StargateMovieDialingManager dialer, int addressLength, int duration) {
        super(time, dialer, addressLength, duration);
    }

    @Override
    public int getParts() {
        return super.getParts() - 1;
    }

    @Override
    public void start() {
        super.start();
        if (duration < 30) return;
        dialer.getSpinHelper().rotateFreely(EnumSpinDirection.random(new Random()));
    }

    @Override
    public void stop() {
        super.stop();
        dialer.getSpinHelper().stopSpinning(true);
    }

    @Override
    public void runPart(int partIndex) {
        super.runPart(partIndex);
        if (partIndex >= getParts()) {
            return;
        }
        dialer.stargate.getStateManager().getChevronsState().scheduleChevronActivate(0, ChevronEnum.valueOf(partIndex % 9), null, false);
    }
}
