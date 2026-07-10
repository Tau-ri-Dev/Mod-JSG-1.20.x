package dev.tauri.jsg.datagen.tag;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.common.registry.tags.JSGItemTags;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

public class JSGItemTagGenerator extends ItemTagsProvider {
    public JSGItemTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, JSG.MOD_ID, existingFileHelper);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void addTags(HolderLookup.Provider pProvider) {
        //Minecraft Tags

        Util.make(tag(ItemTags.MUSIC_DISCS), (tag) ->
                JSGItems.RECORDS.forEach((event, item) -> tag.add(item.get())));

        Util.make(tag(ItemTags.CREEPER_DROP_MUSIC_DISCS), (tag) ->
                JSGItems.RECORDS.forEach((event, item) -> tag.add(item.get())));

        tag(ItemTags.SMELTS_TO_GLASS).add(JSGBlocks.ABYDOS_SAND.get().asItem());

        //JSG Tags
        //Iris blades
        tag(JSGItemTags.IRIS_BLADES)
                .add(JSGItems.IRIS_BLADE.get())
                .add(JSGItems.IRIS_BLADE_TRINIUM.get())
                .addOptional(JSGMapping.rl(JSG.MOD_ID, "iris_blade_stone"));

        // Stargate energy extenders
        tag(JSGItemTags.STARGATE_CAPACITORS)
                .add(CoreItems.CRYSTAL_ENERGY_BASIC.get())
                .add(CoreItems.CRYSTAL_ENERGY_ADVANCED.get())
                .add(CoreItems.CRYSTAL_ENERGY_ULTIMATE.get())
                .add(CoreItems.CRYSTAL_ENERGY_CREATIVE.get());
    }
}
