package dev.tauri.jsg.common.datafixer;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.List;

public class JSGBlocksDataFixer {
    public static void fixMappings(List<MissingMappingsEvent.Mapping<Block>> listToFix) {
        listToFix.forEach(mapping -> {
            var newKey = JSGMapping.rl(JSGCore.MOD_ID, mapping.getKey().getPath());
            var coreBlock = ForgeRegistries.BLOCKS.getValue(newKey);
            if (coreBlock == null) {
                mapping.warn();
                return;
            }
            mapping.remap(coreBlock);
        });
    }
}
