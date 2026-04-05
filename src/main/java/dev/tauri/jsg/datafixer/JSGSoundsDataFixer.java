package dev.tauri.jsg.datafixer;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.List;

public class JSGSoundsDataFixer {
    public static void fixMappings(List<MissingMappingsEvent.Mapping<SoundEvent>> listToFix) {
        listToFix.forEach(mapping -> {
            var newKey = JSGMapping.rl(JSGCore.MOD_ID, mapping.getKey().getPath());
            var coreSound = ForgeRegistries.SOUND_EVENTS.getValue(newKey);
            if (coreSound == null) {
                mapping.warn();
                return;
            }
            mapping.remap(coreSound);
        });
    }
}
