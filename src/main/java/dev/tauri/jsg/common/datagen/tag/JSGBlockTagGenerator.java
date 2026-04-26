package dev.tauri.jsg.common.datagen.tag;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.common.registry.tags.JSGBlockTags;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import dev.tauri.jsg.core.common.registry.tag.CoreBlockTags;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

public class JSGBlockTagGenerator extends BlockTagsProvider {
    public JSGBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, JSG.MOD_ID, existingFileHelper);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void addTags(HolderLookup.Provider pProvider) {

        // Minecraft
        // Mining tags
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(JSGBlocks.SG_REDSTONE_STATE_O_BLOCK.get())
                .add(JSGBlocks.SG_REDSTONE_DIALER_I_BLOCK.get())
                .add(JSGBlocks.TOASTER.get())
                .add(JSGBlocks.PRINTER.get())
                .addTag(JSGBlockTags.ALL_STARGATE_PARTS)
                .addTag(JSGBlockTags.DHD_ANY);

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(JSGBlocks.ABYDOS_SAND.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(JSGBlocks.TOASTER.get())
                .add(JSGBlocks.PRINTER.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .addTag(JSGBlockTags.SINGLE_USE_STARGATE_PARTS);

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .addTag(JSGBlockTags.CLASSIC_STARGATE_PARTS)
                .addTag(JSGBlockTags.DHD_ANY);

        //"Worldgen" Tags
        tag(BlockTags.FEATURES_CANNOT_REPLACE).addTag(JSGBlockTags.ALL_STARGATE_PARTS);

        //Crafting Tags
        tag(BlockTags.SMELTS_TO_GLASS).add(JSGBlocks.ABYDOS_SAND.get());
        Util.make(tag(BlockTags.CAULDRONS), (tag) -> tag.add(CoreFluids.MOLTEN_TRINIUM.cauldron.get())
                .add(CoreFluids.MOLTEN_TITANIUM.cauldron.get())
                .add(CoreFluids.MOLTEN_NAQUADAH_REFINED.cauldron.get())
                .add(CoreFluids.MOLTEN_NAQUADAH_ALLOY.cauldron.get())
                .add(CoreFluids.MOLTEN_NAQUADAH_RAW.cauldron.get()));


        //Mechanics Tags
        tag(BlockTags.INVALID_SPAWN_INSIDE)
                .add(JSGBlocks.IRIS_BLOCK.get());

        tag(BlockTags.SAND).add(JSGBlocks.ABYDOS_SAND.get());

        tag(BlockTags.DIRT).add(JSGBlocks.ABYDOS_SAND.get());

        //JSG TAGS

        // kawoosh invincible blocks
        tag(JSGBlockTags.KAWOOSH_INVINCIBLE);

        tag(JSGBlockTags.BLACK_HOLE_INVINCIBLE);

        // camo blacklist
        tag(CoreBlockTags.CAMO_BLACKLISTED)
                .add(Blocks.BARRIER, Blocks.STRUCTURE_BLOCK, Blocks.STRUCTURE_VOID,
                        Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK,
                        Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT
                );

        // boss immune
        tag(CoreBlockTags.BOSS_IMMUNE)
                .addTag(JSGBlockTags.ALL_STARGATE_PARTS)
                .addTag(JSGBlockTags.DHD_ANY)
                .add(JSGBlocks.IRIS_BLOCK.get());

        // wrench rotatable
        tag(CoreBlockTags.WRENCH_ROTATABLE)
                .addTag(JSGBlockTags.ALL_STARGATE_PARTS)
                .addTag(JSGBlockTags.DHD_ANY);

        // dhd
        tag(JSGBlockTags.DHD_ANY)
                .add(JSGBlocks.DHD_MILKYWAY.get())
                .add(JSGBlocks.DHD_PEGASUS.get());
        tag(JSGBlockTags.DHD_MILKYWAY_LINKABLE_BLOCKS)
                .add(JSGBlocks.STARGATE_MILKYWAY_BASE_BLOCK.get())
                .add(JSGBlocks.STARGATE_MOVIE_BASE_BLOCK.get())
                .add(JSGBlocks.STARGATE_TOLLAN_BASE_BLOCK.get());
        tag(JSGBlockTags.DHD_PEGASUS_LINKABLE_BLOCKS)
                .add(JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK.get());

        // stargate
        tag(JSGBlockTags.CLASSIC_STARGATE_BASES)
                .add(JSGBlocks.STARGATE_MILKYWAY_BASE_BLOCK.get())
                .add(JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK.get())
                .add(JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK.get())
                .add(JSGBlocks.STARGATE_TOLLAN_BASE_BLOCK.get())
                .add(JSGBlocks.STARGATE_MOVIE_BASE_BLOCK.get());
        tag(JSGBlockTags.CLASSIC_STARGATE_PARTS)
                .addTag(JSGBlockTags.CLASSIC_STARGATE_BASES)
                .add(JSGBlocks.STARGATE_MILKYWAY_CHEVRON_BLOCK.get())
                .add(JSGBlocks.STARGATE_MILKYWAY_RING_BLOCK.get())
                .add(JSGBlocks.STARGATE_PEGASUS_CHEVRON_BLOCK.get())
                .add(JSGBlocks.STARGATE_PEGASUS_RING_BLOCK.get())
                .add(JSGBlocks.STARGATE_UNIVERSE_CHEVRON_BLOCK.get())
                .add(JSGBlocks.STARGATE_UNIVERSE_RING_BLOCK.get())
                .add(JSGBlocks.STARGATE_TOLLAN_CHEVRON_BLOCK.get())
                .add(JSGBlocks.STARGATE_TOLLAN_RING_BLOCK.get())
                .add(JSGBlocks.STARGATE_MOVIE_CHEVRON_BLOCK.get())
                .add(JSGBlocks.STARGATE_MOVIE_RING_BLOCK.get());
        tag(JSGBlockTags.SINGLE_USE_STARGATE_BASES)
                .add(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get());
        tag(JSGBlockTags.SINGLE_USE_STARGATE_PARTS)
                .addTag(JSGBlockTags.SINGLE_USE_STARGATE_BASES)
                .add(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get());
        tag(JSGBlockTags.ALL_STARGATE_BASES)
                .addTag(JSGBlockTags.CLASSIC_STARGATE_BASES)
                .addTag(JSGBlockTags.SINGLE_USE_STARGATE_BASES);
        tag(JSGBlockTags.ALL_STARGATE_PARTS)
                .addTag(JSGBlockTags.CLASSIC_STARGATE_PARTS)
                .addTag(JSGBlockTags.SINGLE_USE_STARGATE_PARTS);
        tag(JSGBlockTags.STARGATE_MILKYWAY_LINKABLE_BLOCKS)
                .add(JSGBlocks.DHD_MILKYWAY.get());
        tag(JSGBlockTags.STARGATE_PEGASUS_LINKABLE_BLOCKS)
                .add(JSGBlocks.DHD_PEGASUS.get());
        tag(JSGBlockTags.STARGATE_UNIVERSE_LINKABLE_BLOCKS);

        // dialer
        tag(JSGBlockTags.DIALER_MEMORY_LINKABLE)
                .add(JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK.get());
        tag(JSGBlockTags.DIALER_NEARBY_LINKABLE)
                .add(JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK.get());
        tag(JSGBlockTags.DIALER_MANUAL_DIALING_LINKABLE)
                .add(JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK.get());

        // All tags related to ores
        tag(JSGBlockTags.DEEPSLATE_NAQUADAH_SPIRE_CAN_GROW_FROM)
                .addTag(BlockTags.BASE_STONE_OVERWORLD);
        tag(JSGBlockTags.DEEPSLATE_NAQUADAH_SPIRE_CAN_GROW_THROUGH)
                .addTag(BlockTags.BASE_STONE_OVERWORLD)
                .add(Blocks.SAND)
                .add(Blocks.SANDSTONE);
    }
}
