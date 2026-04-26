package dev.tauri.jsg.common.stargate.manager;

import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.sound.StargateSoundPositionedEnum;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.manager.IStargateSoundManager;
import dev.tauri.jsg.common.registry.JSGPositionedSounds;
import dev.tauri.jsg.core.common.blockentity.ScheduledTaskExecutorInterface;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class StargateSoundManager<BE extends Stargate<?>> extends AbstractStargateManager<BE> implements IStargateSoundManager, ScheduledTaskExecutorInterface {
    protected boolean wormholeSound;
    protected boolean ringRollSound;
    protected boolean shieldHummingSound;

    protected ScheduledTask ringRollStartTask;

    public StargateSoundManager(BE stargate) {
        super(stargate);
    }

    @Override
    public void updateWormholeSound(boolean play) {
        wormholeSound = play;
        JSGSoundHelper.playPositionedSound(stargate.getStargateLevel(), stargate.getGateCenterPos(), JSGPositionedSounds.WORMHOLE_LOOP, play);
        stargate.setStargateChanged();
    }

    @Override
    public void updateShieldHummingSound(boolean play) {
        shieldHummingSound = play;
        JSGSoundHelper.playPositionedSound(stargate.getStargateLevel(), stargate.getGateCenterPos(), JSGPositionedSounds.SHIELD_HUMMING, play);
        stargate.setStargateChanged();
    }

    @Override
    public void updateRingRollSound(boolean play) {
        ringRollSound = play;
        stargate.playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL_START, play);
        updateRingRollLoopSound(play);
        stargate.setStargateChanged();
    }

    private void updateRingRollLoopSound(boolean play) {
        if (play && stargate.getStargateLevel() != null && !stargate.getStargateLevel().isClientSide()) {
            ringRollStartTask = new ScheduledTask(JSGScheduledTaskTypes.STARGATE_RING_ROLL_LOOP_SOUND);
            ringRollStartTask.setExecutor(this);
            ringRollStartTask.setTaskCreated(stargate.getStargateLevel().getGameTime());
        } else {
            if (!play)
                ringRollStartTask = null;
            stargate.playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, play);
        }
        stargate.setStargateChanged();
    }

    @Override
    public boolean isRingRollPlaying() {
        return ringRollSound;
    }


    public void onLoad(@NotNull Level level) {
        if (level.isClientSide())
            stargate.getStateManager().requestState(CoreStateTypes.SOUND_UPDATE.get());
        else
            stargate.getStateManager().getAndSendState(CoreStateTypes.SOUND_UPDATE.get());
    }

    public void updateClient() {
        updateRingRollSound(ringRollSound);
        updateWormholeSound(wormholeSound);
        updateShieldHummingSound(shieldHummingSound);
    }

    @Override
    public void tick(@NotNull Level level) {
        if (ringRollStartTask != null)
            ringRollStartTask.update(level.getGameTime());
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putBoolean("wormholeSound", wormholeSound);
        compound.putBoolean("ringRollSound", ringRollSound);
        compound.putBoolean("shieldHummingSound", shieldHummingSound);
        if (ringRollStartTask != null)
            compound.put("ringRollStartTask", ringRollStartTask.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        wormholeSound = compound.getBoolean("wormholeSound");
        ringRollSound = compound.getBoolean("ringRollSound");
        shieldHummingSound = compound.getBoolean("shieldHummingSound");
        if (compound.contains("ringRollStartTask")) {
            ringRollStartTask = new ScheduledTask(compound.getCompound("ringRollStartTask"));
            ringRollStartTask.setExecutor(this);
        } else
            ringRollStartTask = null;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(wormholeSound);
        buf.writeBoolean(ringRollSound);
        buf.writeBoolean(shieldHummingSound);
    }

    public void fromBytes(ByteBuf buf) {
        wormholeSound = buf.readBoolean();
        ringRollSound = buf.readBoolean();
        shieldHummingSound = buf.readBoolean();
    }

    @Override
    public void addTask(ScheduledTask scheduledTask) {
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, @NotNull CompoundTag customData) {
        if (scheduledTask == JSGScheduledTaskTypes.STARGATE_RING_ROLL_LOOP_SOUND.get()) {
            ringRollStartTask = null;
            stargate.playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, ringRollSound);
        }
    }
}
