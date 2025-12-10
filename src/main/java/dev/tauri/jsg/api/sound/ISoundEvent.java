package dev.tauri.jsg.api.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public interface ISoundEvent {
    String getId();

    int getOrdinal();

    ResourceLocation getLocation();

    float getVolume();

    RegistryObject<SoundEvent> getInstance();
}
