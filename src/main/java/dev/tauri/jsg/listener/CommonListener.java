package dev.tauri.jsg.listener;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.entity.animal.MastadgeEntity;
import dev.tauri.jsg.registry.JSGDimensionEffects;
import dev.tauri.jsg.registry.JSGEntities;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JSG.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
