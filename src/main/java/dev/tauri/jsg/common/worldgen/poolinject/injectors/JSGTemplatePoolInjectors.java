package dev.tauri.jsg.common.worldgen.poolinject.injectors;

import dev.tauri.jsg.core.common.worldgen.TemplatePoolInjector;
import net.minecraft.resources.ResourceLocation;

public class JSGTemplatePoolInjectors {
    public static void register() {
        TemplatePoolInjector.Builder
                .forTargets(new ResourceLocation("minecraft:ancient_city/city_center"))
                .add(new TemplatePoolInjector.PoolAddition(new ResourceLocation("jsg:ancient_city/city_center")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(new ResourceLocation("minecraft:village/desert/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(new ResourceLocation("jsg:village/desert/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(new ResourceLocation("minecraft:village/plains/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(new ResourceLocation("jsg:village/plains/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(new ResourceLocation("minecraft:village/savanna/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(new ResourceLocation("jsg:village/savanna/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(new ResourceLocation("minecraft:village/snow/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(new ResourceLocation("jsg:village/snow/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(new ResourceLocation("minecraft:village/taiga/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(new ResourceLocation("jsg:village/taiga/town_centers")))
                .submit();
    }
}
