package dev.tauri.jsg.common.stargate.animation.incoming;

import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.common.stargate.animation.chevron.StargatePegasusChevronsState;
import dev.tauri.jsg.common.stargate.manager.dialing.StargatePegasusDialingManager;
import net.minecraft.nbt.CompoundTag;

public class PegasusIncomingAnimation extends IncomingAnimation<StargatePegasusDialingManager> {
    public PegasusIncomingAnimation(StargatePegasusDialingManager dialer, CompoundTag tag) {
        super(dialer, tag);
    }

    public PegasusIncomingAnimation(long time, StargatePegasusDialingManager dialer, int addressLength, int duration) {
        super(time, dialer, addressLength, duration);
    }

    @Override
    public void start() {
        super.start();
        if (duration >= 2)
            dialer.stargate.getSoundManager().updateRingRollSound(true);
    }

    @Override
    public void stop() {
        if (!finished) {
            dialer.stargate.getSoundManager().updateRingRollSound(false);
            dialer.stargate.playSoundEvent(StargateSoundEventEnum.CHEVRON_SHUT);
        }
        super.stop();
    }

    @Override
    public void runPart(int partIndex) {
        super.runPart(partIndex);
        ((StargatePegasusChevronsState) dialer.stargate.getStateManager().getChevronsState()).activateSlot(partIndex, JSGSymbolTypes.PEGASUS.get().valueOf(partIndex));
        StargatePegasusChevronsState.chevronFromSlot(partIndex)
                .ifPresent(chevron -> {
                    if (chevron.additionalIndex > 0 && (chevron.additionalIndex + chevron.index + 1) > addressLength)
                        return;
                    dialer.stargate.getStateManager().getChevronsState().get(chevron).lock();
                    dialer.stargate.playSoundEvent(StargateSoundEventEnum.INCOMING);
                });
    }

    @Override
    public int getParts() {
        return 36;
    }
}
