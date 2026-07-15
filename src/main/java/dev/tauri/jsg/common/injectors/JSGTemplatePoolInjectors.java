package dev.tauri.jsg.common.injectors;

import dev.tauri.jsg.core.common.worldgen.TemplatePoolInjector;
import dev.tauri.jsg.core.mapping.JSGMapping;

public class JSGTemplatePoolInjectors {
    public static void register() {
        TemplatePoolInjector.Builder
                .forTargets(JSGMapping.rl("minecraft:ancient_city/city_center"))
                .add(new TemplatePoolInjector.PoolAddition(JSGMapping.rl("jsg:ancient_city/city_center")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(JSGMapping.rl("minecraft:village/desert/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(JSGMapping.rl("jsg:village/desert/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(JSGMapping.rl("minecraft:village/plains/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(JSGMapping.rl("jsg:village/plains/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(JSGMapping.rl("minecraft:village/savanna/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(JSGMapping.rl("jsg:village/savanna/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(JSGMapping.rl("minecraft:village/snow/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(JSGMapping.rl("jsg:village/snow/town_centers")))
                .submit();

        TemplatePoolInjector.Builder
                .forTargets(JSGMapping.rl("minecraft:village/taiga/town_centers"))
                .add(new TemplatePoolInjector.PoolAddition(JSGMapping.rl("jsg:village/taiga/town_centers")))
                .submit();
    }
}
