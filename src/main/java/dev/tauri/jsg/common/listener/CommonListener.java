package dev.tauri.jsg.common.listener;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.entity.animal.MastadgeEntity;
import dev.tauri.jsg.common.registry.JSGDimensionEffects;
import dev.tauri.jsg.common.registry.JSGEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = JSG.MOD_ID)
public class CommonListener {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(JSGEntities.MASTADGE.get(), MastadgeEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        JSGDimensionEffects.register(event);
    }
}
