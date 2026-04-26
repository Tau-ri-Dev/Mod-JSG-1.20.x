package dev.tauri.jsg.common.worldgen.poolinject.injectors;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.resources.ResourceLocation;

public class VillageTemplatePoolsAdditions {
    public static final ResourceLocation PROCESSOR_MOSSY_10 = JSGMapping.rl(JSG.MOD_ID, "mosify_10");
    public static final ResourceLocation PROCESSOR_MOSSY_20 = JSGMapping.rl(JSG.MOD_ID, "mosify_20");
    public static final ResourceLocation PROCESSOR_PLAINS_OFFSET = JSGMapping.rl(JSG.MOD_ID, "plains_farm_and_stargate_offset_2_down");
    public static final ResourceLocation PROCESSOR_SAVANNA_OFFSET = JSGMapping.rl(JSG.MOD_ID, "savanna_farm_and_stargate_offset_2_down");
    public static final ResourceLocation PROCESSOR_OFFSET_2 = JSGMapping.rl(JSG.MOD_ID, "offset_2_down");

    public static ResourceLocation getStargateLoc(String path) {
        return JSGMapping.rl(path.replaceAll("\\{stargate_size}", "small"));
    }

    public static void register() {
     /*   new TemplatePoolInjector.Builder()
                .addPool(JSGMapping.rl("minecraft:village/desert/town_centers"))
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/desert/town_center_1"), 37)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/desert/town_center_2"), 37)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/desert/buried"), 48)

                .addAddition(() -> getStargateLoc("jsg:stargate/village/pegasus/{stargate_size}/desert/town_center_1"), 36)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/pegasus/{stargate_size}/desert/town_center_2"), 36)

                .addAddition(() -> getStargateLoc("jsg:stargate/village/tollan/{stargate_size}/desert/town_center_1"), 24)
                .submit();

        new TemplatePoolInjector.Builder()
                .addPool(JSGMapping.rl("minecraft:village/plains/town_centers"))
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/plains/town_center_1"), 10, PROCESSOR_MOSSY_10)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/plains/town_center_2"), 10, PROCESSOR_MOSSY_10)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/plains/town_center_3"), 10, PROCESSOR_PLAINS_OFFSET)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/plains/buried"), 20, PROCESSOR_MOSSY_10)

                .addAddition(() -> getStargateLoc("jsg:stargate/village/pegasus/{stargate_size}/plains/town_center_1"), 10, PROCESSOR_MOSSY_10)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/pegasus/{stargate_size}/plains/town_center_2"), 10, PROCESSOR_MOSSY_10)

                .addAddition(() -> getStargateLoc("jsg:stargate/village/tollan/{stargate_size}/plains/town_center_1"), 10, PROCESSOR_OFFSET_2)
                .submit();

        new TemplatePoolInjector.Builder().addPool(JSGMapping.rl("minecraft:village/savanna/town_centers"))
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/savanna/town_center_1"), 50)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/savanna/town_center_2"), 50)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/savanna/town_center_3"), 37, PROCESSOR_SAVANNA_OFFSET)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/savanna/buried"), 70, PROCESSOR_MOSSY_10)

                .addAddition(() -> getStargateLoc("jsg:stargate/village/pegasus/{stargate_size}/savanna/town_center_1"), 50)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/pegasus/{stargate_size}/savanna/town_center_2"), 50)

                .addAddition(() -> getStargateLoc("jsg:stargate/village/tollan/{stargate_size}/savanna/town_center_1"), 25)
                .submit();

        new TemplatePoolInjector.Builder().addPool(JSGMapping.rl("minecraft:village/snowy/town_centers"))
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/snowy/town_center_1"), 30)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/snowy/town_center_2"), 50)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/snowy/town_center_3"), 30, PROCESSOR_OFFSET_2)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/snowy/buried"), 70)

                .addAddition(() -> getStargateLoc("jsg:stargate/village/pegasus/{stargate_size}/snowy/town_center_1"), 30)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/pegasus/{stargate_size}/snowy/town_center_2"), 30)

                .addAddition(() -> getStargateLoc("jsg:stargate/village/tollan/{stargate_size}/snowy/town_center_1"), 25, PROCESSOR_OFFSET_2)
                .submit();

        new TemplatePoolInjector.Builder().addPool(JSGMapping.rl("minecraft:village/taiga/town_centers"))
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/taiga/town_center_1"), 15, PROCESSOR_MOSSY_20)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/taiga/town_center_2"), 15, PROCESSOR_MOSSY_20)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/milkyway/{stargate_size}/taiga/buried"), 25, PROCESSOR_MOSSY_20)

                .addAddition(() -> getStargateLoc("jsg:stargate/village/pegasus/{stargate_size}/taiga/town_center_1"), 15, PROCESSOR_MOSSY_20)
                .addAddition(() -> getStargateLoc("jsg:stargate/village/pegasus/{stargate_size}/taiga/town_center_2"), 15, PROCESSOR_MOSSY_20)

                .addAddition(() -> getStargateLoc("jsg:stargate/village/tollan/{stargate_size}/taiga/town_center_1"), 12, PROCESSOR_MOSSY_20)
                .submit();
*/
    }
}