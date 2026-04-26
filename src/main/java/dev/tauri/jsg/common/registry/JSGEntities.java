package dev.tauri.jsg.common.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.common.entity.animal.MastadgeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;

public class JSGEntities {
    public static final RegistryObject<EntityType<MastadgeEntity>> MASTADGE = JSGApi.REGISTRY_HELPER.entity().register("mastadge", () -> EntityType.Builder.of(MastadgeEntity::new, MobCategory.CREATURE).sized(3f, 2.7f).setShouldReceiveVelocityUpdates(true).build("mastadge"));

    public static void init() {
    }
}
