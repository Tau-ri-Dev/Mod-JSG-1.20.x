package dev.tauri.jsg.common.registry;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.client.renderer.entity.KinoRenderer;
import dev.tauri.jsg.client.renderer.entity.MastadgeRenderer;
import dev.tauri.jsg.common.entity.animal.MastadgeEntity;
import dev.tauri.jsg.common.entity.camera.KinoEntity;
import dev.tauri.jsg.core.common.registry.helper.RegistryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = JSG.MOD_ID)
public class JSGEntities {
    public static final RegistryObject<EntityType<MastadgeEntity>> MASTADGE = JSGApi.REGISTRY_HELPER.entity().register("mastadge", () -> EntityType.Builder.of(MastadgeEntity::new, MobCategory.CREATURE).sized(3f, 2.7f).setShouldReceiveVelocityUpdates(true).build("mastadge"));
    public static final RegistryObject<EntityType<KinoEntity>> KINO = JSGApi.REGISTRY_HELPER.entity().register("kino", () -> EntityType.Builder.of(KinoEntity::new, MobCategory.MISC).sized(0.2f, 0.2f).setShouldReceiveVelocityUpdates(true).build("kino"));

    public static void init() {
        JSGApi.REGISTRY_HELPER.entityRendererRegister(() -> {
            RegistryHelper.registerEntityRenderer(MASTADGE.get(), MastadgeRenderer::new);
            RegistryHelper.registerEntityRenderer(KINO.get(), KinoRenderer::new);
        });
    }

    @SubscribeEvent
    public static void onAttributesRegister(EntityAttributeCreationEvent event) {
        event.put(KINO.get(), KinoEntity.createAttributes().build());
    }
}
