package dev.tauri.jsg.common.registry.tags;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class JSGBlockTags {
    public static TagKey<Block> KAWOOSH_INVINCIBLE = tag("kawoosh_invincible");
    public static TagKey<Block> BLACK_HOLE_INVINCIBLE = tag("black_hole_invincible");
    public static TagKey<Block> ALL_STARGATE_BASES = tag("stargate/stargate_bases");
    public static TagKey<Block> ALL_STARGATE_PARTS = tag("stargate/stargate_parts");
    public static TagKey<Block> CLASSIC_STARGATE_BASES = tag("stargate/classic_stargate_bases");
    public static TagKey<Block> CLASSIC_STARGATE_PARTS = tag("stargate/classic_stargate_parts");
    public static TagKey<Block> SINGLE_USE_STARGATE_BASES = tag("stargate/single_use_stargate_bases");
    public static TagKey<Block> SINGLE_USE_STARGATE_PARTS = tag("stargate/single_use_stargate_parts");
    public static TagKey<Block> STARGATE_MILKYWAY_LINKABLE_BLOCKS = tag("stargate/stargate_milkyway_linkable");
    public static TagKey<Block> STARGATE_UNIVERSE_LINKABLE_BLOCKS = tag("stargate/stargate_universe_linkable");
    public static TagKey<Block> STARGATE_PEGASUS_LINKABLE_BLOCKS = tag("stargate/stargate_pegasus_linkable");
    public static TagKey<Block> DHD_ANY = tag("dhd/dhd_any");
    public static TagKey<Block> DHD_MILKYWAY_LINKABLE_BLOCKS = tag("dhd/dhd_milkyway_linkable");
    public static TagKey<Block> DHD_PEGASUS_LINKABLE_BLOCKS = tag("dhd/dhd_pegasus_linkable");
    public static TagKey<Block> DIALER_MEMORY_LINKABLE = tag("dialer/memory_linkable");
    public static TagKey<Block> DIALER_NEARBY_LINKABLE = tag("dialer/nearby_linkable");
    public static TagKey<Block> DIALER_MANUAL_DIALING_LINKABLE = tag("dialer/manual_dialing_linkable");

    public static TagKey<Block> DEEPSLATE_NAQUADAH_SPIRE_CAN_GROW_FROM = tag("worldgen/deepslate_naquadah_spire_can_grow_from");
    public static TagKey<Block> DEEPSLATE_NAQUADAH_SPIRE_CAN_GROW_THROUGH = tag("worldgen/deepslate_naquadah_spire_can_protrude_through");


    private static TagKey<Block> tag(String name) {
        return BlockTags.create(JSGMapping.rl(JSG.MOD_ID, name));
    }
}
