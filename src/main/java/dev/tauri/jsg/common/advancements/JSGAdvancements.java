package dev.tauri.jsg.common.advancements;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.common.advancement.JSGAdvancement;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.advancements.CriteriaTriggers;

public class JSGAdvancements {
    /* GATE MERGING */
    public static final JSGAdvancement MERGED_ORLIN = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "merged_orlin"));
    public static final JSGAdvancement MERGED_MILKYWAY = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "merged_milkyway"));
    public static final JSGAdvancement MERGED_PEGASUS = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "merged_pegasus"));
    public static final JSGAdvancement MERGED_UNIVERSE = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "merged_universe"));
    public static final JSGAdvancement MERGED_TOLLAN = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "merged_tollan"));
    public static final JSGAdvancement MERGED_MOVIE = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "merged_movie"));

    /* GATE OPEN */
    public static final JSGAdvancement CHEVRON_SEVEN_LOCKED = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "chevron_seven_locked"));
    public static final JSGAdvancement CHEVRON_EIGHT_LOCKED = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "chevron_eight_locked"));
    public static final JSGAdvancement CHEVRON_NINE_LOCKED = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "chevron_nine_locked"));
    public static final JSGAdvancement STATIC_ADDRESS_LOCKED = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "static_address_locked"));

    /* OTHER */
    public static final JSGAdvancement WORMHOLE_GO = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "wormhole_go"));
    public static final JSGAdvancement IRIS_IMPACT = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "iris_impact"));
    public static final JSGAdvancement GDO_USED = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "gdo_used"));
    public static final JSGAdvancement KAWOOSH_CREMATION = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "kawoosh_cremation"));
    public static final JSGAdvancement UNSTABLE_SURVIVE = new JSGAdvancement(JSGMapping.rl(JSG.MOD_ID, "unstable_eh_survive"));

    public static final JSGAdvancement[] TRIGGER_ARRAY = new JSGAdvancement[]{
            MERGED_ORLIN,
            MERGED_MILKYWAY,
            MERGED_PEGASUS,
            MERGED_UNIVERSE,
            MERGED_TOLLAN,
            MERGED_MOVIE,

            CHEVRON_SEVEN_LOCKED,
            CHEVRON_EIGHT_LOCKED,
            CHEVRON_NINE_LOCKED,
            STATIC_ADDRESS_LOCKED,

            WORMHOLE_GO,
            IRIS_IMPACT,
            GDO_USED,
            KAWOOSH_CREMATION,
            UNSTABLE_SURVIVE
    };

    public static void register() {
        for (int i = 0; i < JSGAdvancements.TRIGGER_ARRAY.length; i++) {
            JSGAdvancement a = JSGAdvancements.TRIGGER_ARRAY[i];
            CriteriaTriggers.register(a);
        }
        JSG.logger.info("Successfully registered Advancement Triggers!");
    }
}
