package dev.tauri.jsg.common.advancements;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.common.advancement.JSGCriterion;
import dev.tauri.jsg.core.mapping.JSGMapping;

public class JSGCriterions {
    /* GATE MERGING */
    public static final JSGCriterion MERGED_ORLIN = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "merged_orlin"));
    public static final JSGCriterion MERGED_MILKYWAY = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "merged_milkyway"));
    public static final JSGCriterion MERGED_PEGASUS = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "merged_pegasus"));
    public static final JSGCriterion MERGED_UNIVERSE = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "merged_universe"));
    public static final JSGCriterion MERGED_TOLLAN = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "merged_tollan"));
    public static final JSGCriterion MERGED_MOVIE = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "merged_movie"));

    /* GATE OPEN */
    public static final JSGCriterion CHEVRON_SEVEN_LOCKED = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "chevron_seven_locked"));
    public static final JSGCriterion CHEVRON_EIGHT_LOCKED = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "chevron_eight_locked"));
    public static final JSGCriterion CHEVRON_NINE_LOCKED = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "chevron_nine_locked"));
    public static final JSGCriterion STATIC_ADDRESS_LOCKED = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "static_address_locked"));

    /* OTHER */
    public static final JSGCriterion WORMHOLE_GO = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "wormhole_go"));
    public static final JSGCriterion IRIS_IMPACT = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "iris_impact"));
    public static final JSGCriterion GDO_USED = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "gdo_used"));
    public static final JSGCriterion KAWOOSH_CREMATION = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "kawoosh_cremation"));
    public static final JSGCriterion UNSTABLE_SURVIVE = new JSGCriterion(JSGMapping.rl(JSG.MOD_ID, "unstable_eh_survive"));

    public static void init() {
    }
}
