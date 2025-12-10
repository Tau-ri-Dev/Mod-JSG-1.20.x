package dev.tauri.jsg.api.sound.client;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.sound.IPositionedSound;
import dev.tauri.jsg.api.sound.ISoundEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class JSGSoundHelperClient {
    private static final Map<BlockPos, Map<IPositionedSound, JSGPositionedSound>> POSITIONED_SOUND_RECORDS_MAP = new HashMap<>();

    public static JSGPositionedSound getRecord(IPositionedSound soundEnum, BlockPos pos) {
        Map<IPositionedSound, JSGPositionedSound> soundRecordsMapPos = POSITIONED_SOUND_RECORDS_MAP.computeIfAbsent(pos, k -> new HashMap<>());

        JSGPositionedSound soundRecord = soundRecordsMapPos.get(soundEnum);

        if (soundRecord == null) {
            soundRecord = soundEnum.getInstance(pos);
            soundRecordsMapPos.put(soundEnum, soundRecord);
            POSITIONED_SOUND_RECORDS_MAP.put(pos, soundRecordsMapPos);
        }

        return soundRecord;
    }

    public static void playPositionedSoundClientSide(BlockPos pos, IPositionedSound soundEnum, boolean play) {
        if (pos == null) return;
        JSGPositionedSound soundRecord = getRecord(soundEnum, pos);

        if (play)
            soundRecord.play();
        else
            soundRecord.stopPlaying();
    }

    public static void playSoundEventClientSide(BlockPos pos, ISoundEvent sound, float volumeModifier, float pitchModifier) {
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(sound.getInstance().get(), SoundSource.AMBIENT, (float) (sound.getVolume() * JSGConfig.General.volume.get()) * volumeModifier, pitchModifier, RandomSource.create(), pos));
    }

    public static FlybySoundInstance playPositionedFlyBySound(BlockPos pos, IPositionedSound sound, float volumeModifier, float pitchModifier) {
        FlybySoundInstance ins = new FlybySoundInstance(sound.getInstance().get(), SoundSource.AMBIENT, sound.getVolume() * volumeModifier, pitchModifier, RandomSource.create(), pos);
        Minecraft.getInstance().getSoundManager().play(ins);
        return ins;
    }

    private static final Map<IPositionedSound, JSGMainMenuSound> MAINMENU_SOUND_RECORDS_MAP = new HashMap<>();

    public static JSGMainMenuSound getRecordMainMenu(Supplier<Float> volumeSupplier, IPositionedSound soundEnum) {
        JSGMainMenuSound soundRecord = MAINMENU_SOUND_RECORDS_MAP.get(soundEnum);

        if (soundRecord == null) {
            soundRecord = soundEnum.getInstanceAbsolute(volumeSupplier);
            MAINMENU_SOUND_RECORDS_MAP.put(soundEnum, soundRecord);
        }
        soundRecord.volumeSupplier = volumeSupplier;

        return soundRecord;
    }

    public static void playMainMenuTheme(Supplier<Float> volumeSupplier, IPositionedSound soundEnum, boolean play) {
        JSGMainMenuSound soundRecord = getRecordMainMenu(volumeSupplier, soundEnum);

        if (play)
            soundRecord.play();
        else
            soundRecord.stopPlaying();
    }
}
