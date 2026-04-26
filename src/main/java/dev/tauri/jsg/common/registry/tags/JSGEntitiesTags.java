package dev.tauri.jsg.common.registry.tags;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class JSGEntitiesTags {
    public static TagKey<EntityType<?>> STARGATE_UNTRANSPORTABLE_ENTITIES = tag("stargate_untransportable");

    private static TagKey<EntityType<?>> tag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, JSGMapping.rl(JSG.MOD_ID, name));
    }
}
