package dev.tauri.jsg.registry;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class JSGStructures {

    //Milkyway stargate structures
    public static final ResourceKey<Structure> MILKYWAY_BADLANDS = structure("milkyway_outpost_badlands");
    public static final ResourceKey<Structure> MILKYWAY_DESERT = structure("milkyway_outpost_desert");
    public static final ResourceKey<Structure> MILKYWAY_MANGROVE = structure("milkyway_outpost_mangrove");
    public static final ResourceKey<Structure> MILKYWAY_MOSSY = structure("milkyway_outpost_mossy");
    public static final ResourceKey<Structure> MILKYWAY_NETHER = structure("milkyway_outpost_nether");
    public static final ResourceKey<Structure> MILKYWAY_PLAINS = structure("milkyway_outpost_plains");
    public static final ResourceKey<Structure> MILKYWAY_SNOWY = structure("milkyway_outpost_snowy");
    public static final ResourceKey<Structure> MILKYWAY_COMPAT = structure("milkyway_outpost_compat");

    //Movie stargate structures
    public static final ResourceKey<Structure> MOVIE_BURIED = structure("movie_outpost_buried");
    public static final ResourceKey<Structure> MOVIE_COMPAT = structure("movie_outpost_compat");

    //Pegasus stargate structures
    public static final ResourceKey<Structure> PEGASUS_BADLANDS = structure("pegasus_outpost_badlands");
    public static final ResourceKey<Structure> PEGASUS_DESERT = structure("pegasus_outpost_desert");
    public static final ResourceKey<Structure> PEGASUS_MANGROVE = structure("pegasus_outpost_mangrove");
    public static final ResourceKey<Structure> PEGASUS_MOSSY = structure("pegasus_outpost_mossy");
    public static final ResourceKey<Structure> PEGASUS_OCEAN = structure("pegasus_outpost_ocean");
    public static final ResourceKey<Structure> PEGASUS_PLAINS = structure("pegasus_outpost_plains");
    public static final ResourceKey<Structure> PEGASUS_SNOWY = structure("pegasus_outpost_snowy");
    public static final ResourceKey<Structure> PEGASUS_COMPAT = structure("pegasus_outpost_compat");

    //Tollan stargate structures
    public static final ResourceKey<Structure> TOLLAN_COMPAT = structure("tollan_outpost_compat");

    //Universe stargate structures
    public static final ResourceKey<Structure> UNIVERSE_ISLANDS = structure("universe_outpost_end");
    public static final ResourceKey<Structure> UNIVERSE_MAIN_ISLAND = structure("universe_outpost_main_island_end");
    public static final ResourceKey<Structure> UNIVERSE_COMPAT = structure("universe_outpost_compat");

    //Abydos structures
    public static final ResourceKey<Structure> ABYDOS_BOULDER = structure("abydos_boulder");
    public static final ResourceKey<Structure> ABYDOS_CAMPFIRE = structure("abydos_camp");
    public static final ResourceKey<Structure> ABYDOS_CARTOUCHE = structure("abydos_cartouche");
    public static final ResourceKey<Structure> ABYDOS_CITY = structure("abydos_city");
    public static final ResourceKey<Structure> ABYDOS_DUNGEON = structure("abydos_dungeon");
    public static final ResourceKey<Structure> ABYDOS_MAIN_PYRAMID = structure("abydos_main_pyramid");

    private static ResourceKey<Structure> structure(String name) {
        return ResourceKey.create(Registries.STRUCTURE, JSGMapping.rl(JSG.MOD_ID, name));
    }
}
