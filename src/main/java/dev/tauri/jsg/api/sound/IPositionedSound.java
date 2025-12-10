package dev.tauri.jsg.api.sound;

import dev.tauri.jsg.api.sound.client.JSGMainMenuSound;
import dev.tauri.jsg.api.sound.client.JSGPositionedSound;
import net.minecraft.core.BlockPos;

import java.util.function.Supplier;

public interface IPositionedSound extends ISoundEvent {
    boolean isLoopSound();

    JSGPositionedSound getInstance(BlockPos pos);

    JSGMainMenuSound getInstanceAbsolute(Supplier<Float> volumeSupplier);
}
