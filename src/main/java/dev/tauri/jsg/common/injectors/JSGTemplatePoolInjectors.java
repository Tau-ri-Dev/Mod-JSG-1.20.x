package dev.tauri.jsg.common.injectors;

import dev.tauri.jsg.core.common.worldgen.TemplatePoolInjector;
import net.minecraft.resources.ResourceLocation;

public class JSGTemplatePoolInjectors {
    public static void register() {
        TemplatePoolInjector.Builder
                .forTargets(ResourceLocation.parse("minecraft:ancient_city/city_center"))
                .add(new TemplatePoolInjector.PoolAddition(ResourceLocation.parse("jsg:ancient_city/city_center")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(ResourceLocation.parse("minecraft:village/desert/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(ResourceLocation.parse("jsg:village/desert/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(ResourceLocation.parse("minecraft:village/plains/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(ResourceLocation.parse("jsg:village/plains/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(ResourceLocation.parse("minecraft:village/savanna/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(ResourceLocation.parse("jsg:village/savanna/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(ResourceLocation.parse("minecraft:village/snow/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(ResourceLocation.parse("jsg:village/snow/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(ResourceLocation.parse("minecraft:village/taiga/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(ResourceLocation.parse("jsg:village/taiga/town_centers")))
                .submit();
    }
}
