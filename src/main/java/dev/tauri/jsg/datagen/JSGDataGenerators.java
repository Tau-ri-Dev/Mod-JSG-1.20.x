package dev.tauri.jsg.datagen;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.datagen.custom.JSGRIGWavesProvider;
import dev.tauri.jsg.datagen.loot.JSGLootTableProvider;
import dev.tauri.jsg.datagen.tag.*;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JSG.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JSGDataGenerators {
    @SubscribeEvent
    public static void generate(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var exFileHelper = event.getExistingFileHelper();
        var lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new JSGRecipeProvider(output));
        generator.addProvider(event.includeServer(), JSGLootTableProvider.create(output));

        generator.addProvider(event.includeClient(), new JSGBlockStateProvider(output, exFileHelper));
        generator.addProvider(event.includeClient(), new JSGItemModelProvider(output, exFileHelper));

        var blockTagGenerator = generator.addProvider(event.includeServer(), new JSGBlockTagGenerator(output, lookupProvider, exFileHelper));
        generator.addProvider(event.includeServer(), new JSGItemTagGenerator(output, lookupProvider, blockTagGenerator.contentsGetter(), exFileHelper));

        generator.addProvider(event.includeServer(), new JSGBiomeTagGenerator(output, lookupProvider, exFileHelper));
        generator.addProvider(event.includeServer(), new JSGFluidTagGenerator(output, lookupProvider, exFileHelper));
        generator.addProvider(event.includeServer(), new JSGStructureTagGenerator(output, lookupProvider, exFileHelper));

        generator.addProvider(event.includeServer(), new JSGRIGWavesProvider(output));
    }
}
