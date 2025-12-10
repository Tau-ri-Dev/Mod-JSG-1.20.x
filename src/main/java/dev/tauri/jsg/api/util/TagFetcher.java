package dev.tauri.jsg.api.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("all")
public class TagFetcher {
    public static List<Item> getItemsInTag(TagKey<Item> tag) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(tag).stream().toList();
    }

    public static List<Block> getBlocksInTag(TagKey<Block> tag) {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).getTag(tag).stream().toList();
    }

    public static List<Biome> getBiomesInTag(TagKey<Biome> tag) {
        return Objects.requireNonNull(ForgeRegistries.BIOMES.tags()).getTag(tag).stream().toList();
    }
}