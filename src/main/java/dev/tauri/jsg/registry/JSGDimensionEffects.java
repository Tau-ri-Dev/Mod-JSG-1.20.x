package dev.tauri.jsg.registry;

import dev.tauri.jsg.renderer.dimension.AbydosSkyEffects;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;

public class JSGDimensionEffects {
    public static void register(RegisterDimensionSpecialEffectsEvent event) {
        event.register(JSGDimensions.ABYDOS.location(), new AbydosSkyEffects());
    }
}
