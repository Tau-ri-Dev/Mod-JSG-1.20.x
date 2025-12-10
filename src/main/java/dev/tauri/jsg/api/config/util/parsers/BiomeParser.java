package dev.tauri.jsg.api.config.util.parsers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BiomeParser {
	
	/**
	 * Parses array of configured biomes.
	 * 
	 * @param config Array of single lines containing biome definitions.
	 * @return List of {@link Biome}s or empty list.
	 */
	@Nonnull
	public static List<Biome> parseConfig(List<? extends String> config) {
		List<Biome> list = new ArrayList<>();

		for (String line : config) {
			Biome biome = getBiomeFromString(line);
			
			if(biome != null) {
				list.add(biome);
			}
		}
		
		return list;
	}
	
	/**
	 * Parses single line of the config.
	 * 
	 * @param line Consists of modid:biomename
	 * @return {@link Biome} when valid biome, {@code null} otherwise.
	 */
	@Nullable
	static Biome getBiomeFromString(String line) {
        String[] parts = line.trim().split(":", 2);
        return ForgeRegistries.BIOMES.getValue(new ResourceLocation(parts[0], parts[1]));
    }
}
