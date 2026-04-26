package dev.tauri.jsg.common.datafixer;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.List;

public class JSGItemsDataFixer {
    public static void fixMappings(List<MissingMappingsEvent.Mapping<Item>> listToFix) {
        listToFix.forEach(mapping -> {
            var newKey = JSGMapping.rl(JSGCore.MOD_ID, mapping.getKey().getPath());
            var coreItem = ForgeRegistries.ITEMS.getValue(newKey);
            if (coreItem == null) {
                mapping.warn();
                return;
            }
            mapping.remap(coreItem);
        });
    }
}
