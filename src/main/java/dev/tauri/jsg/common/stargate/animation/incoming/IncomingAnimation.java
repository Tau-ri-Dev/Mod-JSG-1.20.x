package dev.tauri.jsg.common.stargate.animation.incoming;

import dev.tauri.jsg.common.stargate.manager.dialing.StargateAbstractDialingManager;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

// holds instance of incoming animation on the server
public class IncomingAnimation<D extends StargateAbstractDialingManager<?>> implements INBTSerializable<CompoundTag>, ITickable {
    protected final D dialer;
    protected long started;
    protected int addressLength;
    protected int duration;
    protected int lastRun = -2;
    protected boolean finished = false;

    public IncomingAnimation(D dialer, CompoundTag tag) {
        this.dialer = dialer;
        deserializeNBT(tag);
    }

    public IncomingAnimation(long time, D dialer, int addressLength, int duration) {
        this.dialer = dialer;
        this.duration = duration;
        this.addressLength = addressLength;
        this.started = time;
    }

    public void start() {
        this.finished = false;
        this.lastRun = -1;
        if (duration <= 0) {
            finish();
        }
    }

    public void stop() {
        this.finished = true;
    }

    public int getParts() {
        return addressLength;
    }

    public void runPart(int partIndex) {
    }

    public void finish() {
        if (finished) return;
        if (lastRun < -1) {
            start();
        }
        int countToRun = (getParts() - lastRun);
        if (countToRun <= 0) return;
        for (var i = lastRun + 1; i <= (lastRun + countToRun); i++) {
            runPart(i);
        }
        stop();
    }

    @Override
    public void tick(@NotNull Level level) {
        if (level.isClientSide) return;
        if (finished) return;
        var ticks = level.getGameTime();
        if (lastRun < -1) {
            start();
        }
        var ticksPerPart = (double) duration / (double) getParts();
        var shouldRunIndexNow = ticksPerPart > 0 ? (Math.floor((ticks - started) / ticksPerPart)) : (getParts() - 1);
        var difference = (shouldRunIndexNow - lastRun);
        if (difference > 0) {
            var lastRun = this.lastRun;
            lastRun += 1;
            for (var i = lastRun; i < lastRun + difference; i++) {
                runPart(i);
                this.lastRun = i;
                if (i == (getParts() - 1))
                    stop();
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putLong("started", started);
        compound.putInt("lastRun", lastRun);
        compound.putInt("duration", duration);
        compound.putBoolean("finished", finished);
        compound.putInt("addressLength", addressLength);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        started = compoundTag.getLong("started");
        lastRun = compoundTag.getInt("lastRun");
        duration = compoundTag.getInt("duration");
        finished = compoundTag.getBoolean("finished");
        addressLength = compoundTag.getInt("addressLength");
    }
}
