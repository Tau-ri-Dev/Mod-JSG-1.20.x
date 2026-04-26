package dev.tauri.jsg.common.registry.tags;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class JSGStructureTags {
    public static TagKey<Structure> HAS_STARGATE = tag("has_stargate");
    public static TagKey<Structure> HAS_MILKYWAY_STARGATE = tag("has_milkyway_stargate");
    public static TagKey<Structure> HAS_PEGASUS_STARGATE = tag("has_pegasus_stargate");
    public static TagKey<Structure> HAS_UNIVERSE_STARGATE = tag("has_universe_stargate");
    public static TagKey<Structure> HAS_TOLLAN_STARGATE = tag("has_tollan_stargate");
    public static TagKey<Structure> HAS_MOVIE_STARGATE = tag("has_movie_stargate");
    public static TagKey<Structure> HAS_COMPAT_STARGATE = tag("has_stargate_outpost_compat");
    public static TagKey<Structure> HAS_POTENTIALLY_MILKYWAY = tag("has_potentially_milkyway_stargate");
    public static TagKey<Structure> HAS_POTENTIALLY_PEGASUS = tag("has_potentially_pegasus_stargate");
    public static TagKey<Structure> HAS_POTENTIALLY_UNIVERSE = tag("has_potentially_universe_stargate");
    public static TagKey<Structure> HAS_POTENTIALLY_TOLLAN = tag("has_potentially_tollan_stargate");
    public static TagKey<Structure> HAS_POTENTIALLY_MOVIE = tag("has_potentially_movie_stargate");
    public static TagKey<Structure> HAS_POTENTIALLY_STARGATE = tag("has_potentially_stargate");
    public static TagKey<Structure> HAS_POTENTIALLY_ABYDOS_AMBIENT = tag("has_abydos_ambient");
    public static TagKey<Structure> HAS_POTENTIALLY_ABYDOS_GENERIC = tag("has_abydos_generic");
    public static TagKey<Structure> HAS_ABYDOS_PYRAMID = tag("has_abydos_pyramid");
    public static TagKey<Structure> OVERWORLD_MARKED_ON_MAP = tag("overworld_marked_on_map");
    public static TagKey<Structure> ABYDOS_MARKED_ON_MAP = tag("abydos_marked_on_map");


    private static TagKey<Structure> tag(String name) {
        return TagKey.create(Registries.STRUCTURE, JSGMapping.rl(JSG.MOD_ID, name));
    }
}
