package dev.tauri.jsg.api.config.util.parsers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemMetaParser {
	
	/**
	 * Parses array of configured items/blocks.
	 * 
	 * @param config Array of single lines
	 * @return List of {@link IBlockState}s or empty list.
	 */
	@Nonnull
	public static List<Block> parseConfig(List<? extends String> config) {
		List<Block> list = new ArrayList<>();

		for (String line : config) {
			Block stack = getItemMetaPairFromString(line);
			
			if(stack != null) {
				list.add(stack);
			}
		}
		
		return list;
	}
	
	/**
	 * Parses single line of the config.
	 * 
	 * @param line Consists of modid:blockid[:meta]
	 * @return {@link IBlockState} when valid block, {@code null} otherwise.
	 */
	@Nullable
	public static Block getItemMetaPairFromString(String line) {
        String[] parts = line.trim().split(":", 3);
        
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));

        if (item != null) {
			return Block.byItem(item);
        	/*if (parts.length == 2 || parts[2].equals("*"))
        		return new ItemMetaPair(item, 0);
        	
            try {
            	return new ItemMetaPair(item, Integer.parseInt(parts[2]));
            }
            
        	catch (NumberFormatException e) {
    			return null;
    		}*/
        }
        
        return null;
    }
}
