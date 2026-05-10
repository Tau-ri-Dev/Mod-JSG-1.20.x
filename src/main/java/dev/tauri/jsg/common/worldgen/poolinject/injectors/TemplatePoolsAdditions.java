package dev.tauri.jsg.common.worldgen.poolinject.injectors;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Registry;
import java.util.ArrayList;
import java.util.List;

public class TemplatePoolsAdditions {
    public static void injectPoolToPool(Registry<StructureTemplatePool> poolRegistry, ResourceLocation targetPoolLocation, ResourceLocation sourcePoolLocation) {
        StructureTemplatePool targetPool = poolRegistry.get(targetPoolLocation);
        StructureTemplatePool sourcePool = poolRegistry.get(sourcePoolLocation);

        if (targetPool != null && sourcePool != null) {
                targetPool.templates.addAll(sourcePool.templates);

                List<Pair<StructurePoolElement, Integer>> combinedRaw = new ArrayList<>(targetPool.rawTemplates);
                combinedRaw.addAll(sourcePool.rawTemplates);
                targetPool.rawTemplates = combinedRaw;
        }
    }
}