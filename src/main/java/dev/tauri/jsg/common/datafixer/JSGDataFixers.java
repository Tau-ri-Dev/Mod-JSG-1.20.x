package dev.tauri.jsg.common.datafixer;

import dev.tauri.jsg.JSG;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

@Mod.EventBusSubscriber(modid = JSG.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JSGDataFixers {
    @SubscribeEvent
    public static void onDataFix(MissingMappingsEvent event) {
        JSGItemsDataFixer.fixMappings(event.getMappings(ForgeRegistries.Keys.ITEMS, JSG.MOD_ID));
        JSGBlocksDataFixer.fixMappings(event.getMappings(ForgeRegistries.Keys.BLOCKS, JSG.MOD_ID));
        JSGBlockEntitiesDataFixer.fixMappings(event.getMappings(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, JSG.MOD_ID));
        JSGSoundsDataFixer.fixMappings(event.getMappings(ForgeRegistries.Keys.SOUND_EVENTS, JSG.MOD_ID));
        JSGFluidsDataFixer.fixMappings(event.getMappings(ForgeRegistries.Keys.FLUID_TYPES, JSG.MOD_ID), event.getMappings(ForgeRegistries.Keys.FLUIDS, JSG.MOD_ID));
    }
}
