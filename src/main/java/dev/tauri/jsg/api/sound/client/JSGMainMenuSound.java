package dev.tauri.jsg.api.sound.client;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class JSGMainMenuSound extends JSGPositionedSound {
    public Supplier<Float> volumeSupplier;

    public JSGMainMenuSound(SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource, boolean loop, Supplier<Float> volumeSupplier) {
        super(new BlockPos(0, 0, 0), soundEvent, soundSource, randomSource, loop, volumeSupplier.get());
        this.attenuation = Attenuation.NONE;
        this.relative = true;
        this.volumeSupplier = volumeSupplier;
    }

    public float getVolume() {
        return volumeSupplier.get();
    }

    public float getMinVolume() {
        return volumeSupplier.get();
    }
}
