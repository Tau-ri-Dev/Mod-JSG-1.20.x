package dev.tauri.jsg.common.stargate.animation.incoming;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateAbstractDialingManager;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;

public final class StaticIncomingAnimation extends IncomingAnimation<StargateAbstractDialingManager<?>> {
    public StaticIncomingAnimation(StargateAbstractDialingManager<?> dialer, CompoundTag tag) {
        super(dialer, tag);
    }

    public StaticIncomingAnimation(long time, StargateAbstractDialingManager<?> dialer, int addressLength, int duration) {
        super(time, dialer, addressLength, duration);
    }

    @Override
    public void start() {
        super.start();
        dialer.stargate.getStateManager().getChevronsState().scheduleChevronsActivateMultiple(0, false, true, Util.make(new ArrayList<>(), (list) -> {
            for (var i = 0; i < addressLength; ++i) {
                list.add(ChevronEnum.valueOf(((i == (addressLength - 1)) ? 8 : i) % 9));
            }
        }));
        stop();
    }
}
