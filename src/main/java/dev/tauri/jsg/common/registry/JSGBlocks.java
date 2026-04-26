package dev.tauri.jsg.common.registry;

import dev.tauri.jsg.Constants;
import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.common.block.PrinterBlock;
import dev.tauri.jsg.common.block.ToasterBlock;
import dev.tauri.jsg.common.block.dialhomedevice.DHDMilkyWayBlock;
import dev.tauri.jsg.common.block.dialhomedevice.DHDPegasusBlock;
import dev.tauri.jsg.common.block.invisible.IrisBlock;
import dev.tauri.jsg.common.block.stargate.*;
import dev.tauri.jsg.common.block.stargate.redstone.StargateRedstoneDialerI;
import dev.tauri.jsg.common.block.stargate.redstone.StargateRedstoneStateO;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class JSGBlocks {
    private static final DeferredRegister<Block> REGISTER = JSGApi.REGISTRY_HELPER.block();

    /**
     * INVISIBLE
     */
    public static final RegistryObject<Block> IRIS_BLOCK = REGISTER.register("iris_block", IrisBlock::new);

    /**
     * STARGATE BLOCKS
     */
    public static final RegistryObject<Block> STARGATE_ORLIN_BASE_BLOCK = REGISTER.register("stargate_orlin_base_block", StargateOrlinBaseBlock::new);
    public static final RegistryObject<Block> STARGATE_ORLIN_MEMBER_BLOCK = REGISTER.register("stargate_orlin_member_block", StargateOrlinMemberBlock::new);

    public static final RegistryObject<Block> STARGATE_MILKYWAY_BASE_BLOCK = REGISTER.register("stargate_milkyway_base_block", StargateMilkyWayBaseBlock::new);
    public static final RegistryObject<Block> STARGATE_MILKYWAY_CHEVRON_BLOCK = REGISTER.register("stargate_milkyway_chevron_block", StargateMilkyWayMemberBlock.StargateMilkyWayChevronBlock::new);
    public static final RegistryObject<Block> STARGATE_MILKYWAY_RING_BLOCK = REGISTER.register("stargate_milkyway_ring_block", StargateMilkyWayMemberBlock.StargateMilkyWayRingBlock::new);
    public static final RegistryObject<Block> STARGATE_PEGASUS_BASE_BLOCK = REGISTER.register("stargate_pegasus_base_block", StargatePegasusBaseBlock::new);
    public static final RegistryObject<Block> STARGATE_PEGASUS_CHEVRON_BLOCK = REGISTER.register("stargate_pegasus_chevron_block", StargatePegasusMemberBlock.StargatePegasusChevronBlock::new);
    public static final RegistryObject<Block> STARGATE_PEGASUS_RING_BLOCK = REGISTER.register("stargate_pegasus_ring_block", StargatePegasusMemberBlock.StargatePegasusRingBlock::new);
    public static final RegistryObject<Block> STARGATE_UNIVERSE_BASE_BLOCK = REGISTER.register("stargate_universe_base_block", StargateUniverseBaseBlock::new);
    public static final RegistryObject<Block> STARGATE_UNIVERSE_CHEVRON_BLOCK = REGISTER.register("stargate_universe_chevron_block", StargateUniverseMemberBlock.StargateUniverseChevronBlock::new);
    public static final RegistryObject<Block> STARGATE_UNIVERSE_RING_BLOCK = REGISTER.register("stargate_universe_ring_block", StargateUniverseMemberBlock.StargateUniverseRingBlock::new);
    public static final RegistryObject<Block> STARGATE_TOLLAN_BASE_BLOCK = REGISTER.register("stargate_tollan_base_block", StargateTollanBaseBlock::new);
    public static final RegistryObject<Block> STARGATE_TOLLAN_CHEVRON_BLOCK = REGISTER.register("stargate_tollan_chevron_block", StargateTollanMemberBlock.StargateTollanChevronBlock::new);
    public static final RegistryObject<Block> STARGATE_TOLLAN_RING_BLOCK = REGISTER.register("stargate_tollan_ring_block", StargateTollanMemberBlock.StargateTollanRingBlock::new);
    public static final RegistryObject<Block> STARGATE_MOVIE_BASE_BLOCK = REGISTER.register("stargate_movie_base_block", StargateMovieBaseBlock::new);
    public static final RegistryObject<Block> STARGATE_MOVIE_CHEVRON_BLOCK = REGISTER.register("stargate_movie_chevron_block", StargateMovieMemberBlock.StargateMovieChevronBlock::new);
    public static final RegistryObject<Block> STARGATE_MOVIE_RING_BLOCK = REGISTER.register("stargate_movie_ring_block", StargateMovieMemberBlock.StargateMovieRingBlock::new);

    /**
     * DHD
     */
    public static final RegistryObject<Block> DHD_MILKYWAY = REGISTER.register("dhd_milkyway", DHDMilkyWayBlock::new);
    public static final RegistryObject<Block> DHD_PEGASUS = REGISTER.register("dhd_pegasus", DHDPegasusBlock::new);

    /**
     * ENERGY
     */

    //public static final RegistryObject<Block> ORBAN_NAQUADAH_GENERATOR_BLOCK = REGISTER.register("orban_naquadah_generator_block", OrbanNaquadahGeneratorBlock::new);


    /**
     * REDSTONE IO
     */
    public static final RegistryObject<Block> SG_REDSTONE_STATE_O_BLOCK = REGISTER.register("sg_redstone_state_output_block", StargateRedstoneStateO::new);
    public static final RegistryObject<Block> SG_REDSTONE_DIALER_I_BLOCK = REGISTER.register("sg_redstone_dialer_input_block", StargateRedstoneDialerI::new);

    /**
     * Natural Blocks
     */
    public static final RegistryObject<Block> ABYDOS_SAND = Constants.JSG_BLOCK_HELPER.builder("abydos_sand").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.SAND).mapColor(MapColor.COLOR_YELLOW)).buildFallingGeneric();
    /**
     * MISC
     */
    public static final RegistryObject<Block> TOASTER = REGISTER.register("toaster", ToasterBlock::new);
    public static final RegistryObject<Block> PRINTER = REGISTER.register("printer", PrinterBlock::new);
    //public static final RegistryObject<Block> JUB_CABLE = REGISTER.register("jub_cable", JUBCableBlock::new);

    public static void init() {
    }
}
