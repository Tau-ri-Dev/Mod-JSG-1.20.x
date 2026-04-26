package dev.tauri.jsg.common.worldgen.poolinject.injectors;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.common.worldgen.TemplatePoolInjector;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

@SuppressWarnings("all")
public class StargateTemplatePoolsAdditions {
    public static final ResourceLocation PROCESSOR_OFFSET_NEUTRAL = JSGMapping.rl(JSG.MOD_ID, "surface_issues");
    public static final ResourceLocation PROCESSOR_OFFSET_1_DOWN = JSGMapping.rl(JSG.MOD_ID, "offset_1_down");
    public static final ResourceLocation PROCESSOR_OFFSET_1_UP = JSGMapping.rl(JSG.MOD_ID, "offset_1_up");
    public static final ResourceLocation PROCESSOR_OFFSET_2_DOWN = JSGMapping.rl(JSG.MOD_ID, "offset_2_down");
    public static final ResourceLocation PROCESSOR_MOSSY_10_OFFSET_1_DOWN = JSGMapping.rl(JSG.MOD_ID, "mosify_10_with_offset_1_down");
    public static final ResourceLocation PROCESSOR_MOSSY_20_OFFSET_1_DOWN = JSGMapping.rl(JSG.MOD_ID, "mosify_20_with_offset_1_down");
    public static final ResourceLocation PROCESSOR_MOSSY_10_OFFSET_2_DOWN = JSGMapping.rl(JSG.MOD_ID, "mosify_10_with_offset_2_down");
    public static final ResourceLocation PROCESSOR_MOSSY_20_OFFSET_2_DOWN = JSGMapping.rl(JSG.MOD_ID, "mosify_20_with_offset_2_down");

    public static void register() {
        addBases(StargateTypes.MILKYWAY, List.of("desert", "plains", "snowy", "nether"), 4);
        addBases(StargateTypes.MILKYWAY, List.of("mangrove"), 4, PROCESSOR_MOSSY_20_OFFSET_1_DOWN);
        addBases(StargateTypes.MILKYWAY, List.of("mossy"), 4, PROCESSOR_MOSSY_10_OFFSET_1_DOWN);
        addBases(StargateTypes.MILKYWAY, List.of("badlands"), 3);
        addBase(StargateTypes.MILKYWAY, List.of("badlands"), "in_mountain_1");
        addBase(StargateTypes.MILKYWAY, List.of("badlands"), "in_mountain_2");
        addBase(StargateTypes.MILKYWAY, List.of("badlands"), "burried");
        addBase(StargateTypes.MILKYWAY, List.of("desert"), "burried");
        addBase(StargateTypes.MILKYWAY, List.of("mangrove"), "burried", PROCESSOR_MOSSY_20_OFFSET_1_DOWN);
        addBase(StargateTypes.MILKYWAY, List.of("mossy"), "burried", PROCESSOR_MOSSY_10_OFFSET_1_DOWN);
        addBase(StargateTypes.MILKYWAY, List.of("plains"), "burried");
        addBase(StargateTypes.MILKYWAY, List.of("plains"), "horizontal");
        addBase(StargateTypes.MILKYWAY, List.of("snowy"), "burried");

        addBases(StargateTypes.PEGASUS, List.of("badlands", "desert", "plains", "snowy"), 4, PROCESSOR_OFFSET_2_DOWN);
        addBases(StargateTypes.PEGASUS, List.of("mangrove"), 4, PROCESSOR_MOSSY_20_OFFSET_2_DOWN);
        addBases(StargateTypes.PEGASUS, List.of("mossy"), 4, PROCESSOR_MOSSY_10_OFFSET_2_DOWN);
        addBases(StargateTypes.PEGASUS, List.of("ocean"), 4);

        addBases(StargateTypes.UNIVERSE, List.of("end"), 4);
        addBase(StargateTypes.UNIVERSE, List.of("end_main_island"), "main_island", PROCESSOR_OFFSET_NEUTRAL);

        addBase(StargateTypes.MOVIE, List.of("desert"), "buried");

        // ABYDOS
        new TemplatePoolInjector.Builder().
                addPool(JSGMapping.rl(JSG.MOD_ID, "abydos/main_pyramid/gateroom_gate_sections"))
                .addAddition(() -> JSGMapping.rl(("jsg:abydos/main_pyramid/gateroom_gate_{stargate_size}").replaceAll("\\{stargate_size\\}", "small")), 1)
                .submit();

        // COMPAT
        addBases(StargateTypes.MILKYWAY, List.of("compat"), 1);
        addBases(StargateTypes.PEGASUS, List.of("compat"), 1);
        addBases(StargateTypes.UNIVERSE, List.of("compat"), 1);
        addBases(StargateTypes.TOLLAN, List.of("compat"), 1);
        addBases(StargateTypes.MOVIE, List.of("compat"), 1);
    }


    private static void addBases(ResourceLocation pool, String path, int count) {
        var injector = new TemplatePoolInjector.Builder();
        injector.addPool(pool);
        for (int i = 1; i <= count; i++) {
            int finalI = i;
            injector.addAddition(() -> JSGMapping.rl("jsg:stargate/parts/" + path.replaceAll("\\{stargate_size\\}", "small") + "/base_" + finalI), 1, false);
        }
        injector.submit();
    }

    private static void addBases(ResourceLocation pool, String path, int count, ResourceLocation proceesor) {
        var injector = new TemplatePoolInjector.Builder();
        injector.addPool(pool);
        for (int i = 1; i <= count; i++) {
            int finalI = i;
            injector.addAddition(() -> JSGMapping.rl("jsg:stargate/parts/" + path.replaceAll("\\{stargate_size\\}", "small") + "/base_" + finalI), 1, false, proceesor);
        }
        injector.submit();
    }

    private static void addBases(RegistryObject<StargateType<?>> stargateType, List<String> biomes, int count) {
        for (var biome : biomes)
            addBases(JSGMapping.rl(stargateType.getId().getNamespace(), "stargate/parts/" + stargateType.getId().getPath() + "/common/bases/" + biome), stargateType.getId().getPath() + "/{stargate_size}/bases/" + biome, count);
    }

    private static void addBases(RegistryObject<StargateType<?>> stargateType, List<String> biomes, int count, ResourceLocation processor) {
        for (var biome : biomes)
            addBases(JSGMapping.rl(stargateType.getId().getNamespace(), "stargate/parts/" + stargateType.getId().getPath() + "/common/bases/" + biome), stargateType.getId().getPath() + "/{stargate_size}/bases/" + biome, count, processor);
    }

    private static void addBase(RegistryObject<StargateType<?>> stargateType, List<String> biomes, String structName) {
        for (var biome : biomes) {
            var injector = new TemplatePoolInjector.Builder();
            injector.addPool(JSGMapping.rl(stargateType.getId().getNamespace(), "stargate/parts/" + stargateType.getId().getPath() + "/common/bases/" + biome))
                    .addAddition(() -> JSGMapping.rl(("jsg:stargate/parts/" + stargateType.getId().getPath() + "/{stargate_size}/bases/" + biome + "/" + structName).replaceAll("\\{stargate_size\\}", "small")), 1, false)
                    .submit();
        }
    }

    private static void addBase(RegistryObject<StargateType<?>> stargateType, List<String> biomes, String structName, ResourceLocation processor) {
        for (var biome : biomes) {
            new TemplatePoolInjector.Builder()
                    .addPool(JSGMapping.rl(stargateType.getId().getNamespace(), "stargate/parts/" + stargateType.getId().getPath() + "/common/bases/" + biome))
                    .addAddition(() -> JSGMapping.rl(("jsg:stargate/parts/" + stargateType.getId().getPath() + "/{stargate_size}/bases/" + biome + "/" + structName).replaceAll("\\{stargate_size\\}", "small")), 1, false, processor)
                    .submit();
        }
    }
}
