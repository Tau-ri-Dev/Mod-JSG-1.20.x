package dev.tauri.jsg.api.sound.client;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class FlybySoundInstance extends AbstractTickableSoundInstance {
    public BlockPos currentPosition;
    public BlockPos lastPosition;

    public BlockPos centerPos = BlockPos.ZERO;

    public float pitchOriginal;

    public FlybySoundInstance(SoundEvent sound, SoundSource source, float volume, float pitch, RandomSource rsource, BlockPos pos) {
        super(sound, source, rsource);
        this.currentPosition = pos;
        this.lastPosition = pos;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
        this.pitchOriginal = pitch;
        this.x = ((float) pos.getX());
        this.y = ((float) pos.getY());
        this.z = ((float) pos.getZ());
    }

    public boolean canStartSilent() {
        return true;
    }

    public void tick() {
        if (currentPosition == null) {
            this.stop();
        } else {
            this.x = ((float) this.currentPosition.getX());
            this.y = ((float) this.currentPosition.getY());
            this.z = ((float) this.currentPosition.getZ());
            float f = (float) Math.abs(centerPos.distSqr(currentPosition));
            if (f > 0.00F) {
                this.pitch = Mth.clamp(this.pitchOriginal * f/50, 0.0F, 2.0F);
                this.volume = Mth.lerp(Mth.clamp(f, 0.0F, 0.5F), 5F, 0F);
            }
            lastPosition = currentPosition;
        }
    }
}
