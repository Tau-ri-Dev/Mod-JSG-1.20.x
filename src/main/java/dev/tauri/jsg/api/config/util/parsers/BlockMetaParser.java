package dev.tauri.jsg.api.config.util.parsers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockMetaParser {
	
	/**
	 * Parses array of configured blocks.
	 * 
	 * @param config Array of single lines
	 * @return List of {@link BlockState}s or empty list.
	 */
	@Nonnull
	public static Map<BlockState, Boolean> parseConfig(List<? extends String> config) {
		Map<BlockState, Boolean> map = new HashMap<>();

		for (String line : config) {
			String[] parts = line.trim().split(":", 3);
			if(parts.length < 2) continue;
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parts[0] + ":" + parts[1]));

			if (block != null && block != Blocks.AIR) {
				map.put(block.defaultBlockState(), Boolean.TRUE);
				/*if (parts.length == 2){
					map.put(block.defaultBlockState(), Boolean.FALSE);
				}
				else if(parts[2].equals("*")){
					map.put(block.defaultBlockState(), Boolean.TRUE);
				}
				else
					map.put(block.(Integer.parseInt(parts[2])), Boolean.FALSE);*/
			}
		}
		
		return map;
	}
}
