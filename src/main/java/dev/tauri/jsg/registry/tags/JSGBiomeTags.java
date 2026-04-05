package dev.tauri.jsg.registry.tags;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class JSGBiomeTags {
    public static TagKey<Biome> IS_ABYDOS = tag("is_abydos");

    //has_structure folder
    public static TagKey<Biome> HAS_STRUCTURE_ABYDOS_CAMP = tag("has_structure/abydos_camps");
    public static TagKey<Biome> HAS_STRUCTURE_ABYDOS_CARTOUCHE = tag("has_structure/abydos_cartouche");
    public static TagKey<Biome> HAS_STRUCTURE_ABYDOS_CITY = tag("has_structure/abydos_city");
    public static TagKey<Biome> HAS_STRUCTURE_ABYDOS_DUNGEON = tag("has_structure/abydos_dungeon");
    public static TagKey<Biome> HAS_STRUCTURE_ABYDOS_MAIN_PYRAMID = tag("has_structure/abydos_main_pyramid");

    public static TagKey<Biome> HAS_STRUCTURE_MILKYWAY_OUTPOST_BADLANDS = tag("has_structure/milkyway_outpost_badlands");
    public static TagKey<Biome> HAS_STRUCTURE_MILKYWAY_OUTPOST_DESERT = tag("has_structure/milkyway_outpost_desert");
    public static TagKey<Biome> HAS_STRUCTURE_MILKYWAY_OUTPOST_MANGROVE = tag("has_structure/milkyway_outpost_mangrove");
    public static TagKey<Biome> HAS_STRUCTURE_MILKYWAY_OUTPOST_MOSSY = tag("has_structure/milkyway_outpost_mossy");
    public static TagKey<Biome> HAS_STRUCTURE_MILKYWAY_OUTPOST_NETHER = tag("has_structure/milkyway_outpost_nether");
    public static TagKey<Biome> HAS_STRUCTURE_MILKYWAY_OUTPOST_PLAINS = tag("has_structure/milkyway_outpost_plains");
    public static TagKey<Biome> HAS_STRUCTURE_MILKYWAY_OUTPOST_SNOWY = tag("has_structure/milkyway_outpost_snowy");
    public static TagKey<Biome> HAS_STRUCTURE_MILKYWAY_OUTPOST_COMPAT = tag("has_structure/milkyway_outpost_compat");

    public static TagKey<Biome> HAS_STRUCTURE_MOVIE_BURRIED = tag("has_structure/movie_outpost_buried");
    public static TagKey<Biome> HAS_STRUCTURE_MOVIE_OUTPOST_COMPAT = tag("has_structure/movie_outpost_compat");

    public static TagKey<Biome> HAS_STRUCTURE_PEGASUS_OUTPOST_BADLANDS = tag("has_structure/pegasus_outpost_badlands");
    public static TagKey<Biome> HAS_STRUCTURE_PEGASUS_OUTPOST_DESERT = tag("has_structure/pegasus_outpost_desert");
    public static TagKey<Biome> HAS_STRUCTURE_PEGASUS_OUTPOST_MANGROVE = tag("has_structure/pegasus_outpost_mangrove");
    public static TagKey<Biome> HAS_STRUCTURE_PEGASUS_OUTPOST_MOSSY = tag("has_structure/pegasus_outpost_mossy");
    public static TagKey<Biome> HAS_STRUCTURE_PEGASUS_OUTPOST_OCEAN = tag("has_structure/pegasus_outpost_ocean");
    public static TagKey<Biome> HAS_STRUCTURE_PEGASUS_OUTPOST_PLAINS = tag("has_structure/pegasus_outpost_plains");
    public static TagKey<Biome> HAS_STRUCTURE_PEGASUS_OUTPOST_SNOWY = tag("has_structure/pegasus_outpost_snowy");
    public static TagKey<Biome> HAS_STRUCTURE_PEGASUS_OUTPOST_COMPAT = tag("has_structure/pegasus_outpost_compat");

    public static TagKey<Biome> HAS_STRUCTURE_TOLLAN_OUTPOST_COMPAT = tag("has_structure/tollan_outpost_compat");

    public static TagKey<Biome> HAS_STRUCTURE_UNIVERSE_OUTPOST_END = tag("has_structure/universe_outpost_end");
    public static TagKey<Biome> HAS_STRUCTURE_UNIVERSE_OUTPOST_END_MAIN_ISLAND = tag("has_structure/universe_outpost_end_main_island");
    public static TagKey<Biome> HAS_STRUCTURE_UNIVERSE_OUTPOST_COMPAT = tag("has_structure/universe_outpost_compat");

    public static TagKey<Biome> HAS_NAQUADAH_DEPOSITS = tag("has_naquadah_deposits");


    private static TagKey<Biome> tag(String name) {
        return TagKey.create(Registries.BIOME, JSGMapping.rl(JSG.MOD_ID, name));
    }
}
