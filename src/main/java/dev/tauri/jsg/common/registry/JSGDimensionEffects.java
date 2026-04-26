package dev.tauri.jsg.common.registry;

import dev.tauri.jsg.client.renderer.dimension.AbydosSkyEffects;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;

public class JSGDimensionEffects {
    public static void register(RegisterDimensionSpecialEffectsEvent event) {
        event.register(JSGDimensions.ABYDOS.location(), new AbydosSkyEffects());
    }
}
