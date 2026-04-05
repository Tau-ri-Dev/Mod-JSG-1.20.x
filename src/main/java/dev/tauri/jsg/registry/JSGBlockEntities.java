package dev.tauri.jsg.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.blockentity.PrinterBE;
import dev.tauri.jsg.blockentity.ToasterBE;
import dev.tauri.jsg.blockentity.dialhomedevice.DHDMilkyWayBE;
import dev.tauri.jsg.blockentity.dialhomedevice.DHDPegasusBE;
import dev.tauri.jsg.blockentity.generator.OrbanNaquadahGeneratorBE;
import dev.tauri.jsg.blockentity.jub.JUBCableBE;
import dev.tauri.jsg.blockentity.stargate.*;
import dev.tauri.jsg.core.common.registry.helper.RegistryHelper;
import dev.tauri.jsg.renderer.dialhomedevice.DHDMilkyWayRenderer;
import dev.tauri.jsg.renderer.dialhomedevice.DHDPegasusRenderer;
import dev.tauri.jsg.renderer.machine.PrinterRenderer;
import dev.tauri.jsg.renderer.stargate.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class JSGBlockEntities {
    public static final RegistryObject<BlockEntityType<StargateOrlinBaseBE>> STARGATE_ORLIN_BASE_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_orlin_base_block", RegistryHelper.beSupplier(StargateOrlinBaseBE::new, JSGBlocks.STARGATE_ORLIN_BASE_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateOrlinMemberBE>> STARGATE_ORLIN_MEMBER_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_orlin_member_block", RegistryHelper.beSupplier(StargateOrlinMemberBE::new, JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateMilkyWayBaseBE>> STARGATE_MILKYWAY_BASE_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_milkyway_base_block", RegistryHelper.beSupplier(StargateMilkyWayBaseBE::new, JSGBlocks.STARGATE_MILKYWAY_BASE_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateMilkyWayMemberBE.StargateMilkyWayChevronBE>> STARGATE_MILKYWAY_CHEVRON_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_milkyway_chevron_block", RegistryHelper.beSupplier(StargateMilkyWayMemberBE.StargateMilkyWayChevronBE::new, JSGBlocks.STARGATE_MILKYWAY_CHEVRON_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateMilkyWayMemberBE.StargateMilkyWayRingBE>> STARGATE_MILKYWAY_RING_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_milkyway_ring_block", RegistryHelper.beSupplier(StargateMilkyWayMemberBE.StargateMilkyWayRingBE::new, JSGBlocks.STARGATE_MILKYWAY_RING_BLOCK));
    public static final RegistryObject<BlockEntityType<StargatePegasusBaseBE>> STARGATE_PEGASUS_BASE_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_pegasus_base_block", RegistryHelper.beSupplier(StargatePegasusBaseBE::new, JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK));
    public static final RegistryObject<BlockEntityType<StargatePegasusMemberBE.StargatePegasusChevronBE>> STARGATE_PEGASUS_CHEVRON_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_pegasus_chevron_block", RegistryHelper.beSupplier(StargatePegasusMemberBE.StargatePegasusChevronBE::new, JSGBlocks.STARGATE_PEGASUS_CHEVRON_BLOCK));
    public static final RegistryObject<BlockEntityType<StargatePegasusMemberBE.StargatePegasusRingBE>> STARGATE_PEGASUS_RING_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_pegasus_ring_block", RegistryHelper.beSupplier(StargatePegasusMemberBE.StargatePegasusRingBE::new, JSGBlocks.STARGATE_PEGASUS_RING_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateUniverseBaseBE>> STARGATE_UNIVERSE_BASE_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_universe_base_block", RegistryHelper.beSupplier(StargateUniverseBaseBE::new, JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateUniverseMemberBE.StargateUniverseChevronBE>> STARGATE_UNIVERSE_CHEVRON_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_universe_chevron_block", RegistryHelper.beSupplier(StargateUniverseMemberBE.StargateUniverseChevronBE::new, JSGBlocks.STARGATE_UNIVERSE_CHEVRON_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateUniverseMemberBE.StargateUniverseRingBE>> STARGATE_UNIVERSE_RING_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_universe_ring_block", RegistryHelper.beSupplier(StargateUniverseMemberBE.StargateUniverseRingBE::new, JSGBlocks.STARGATE_UNIVERSE_RING_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateTollanBaseBE>> STARGATE_TOLLAN_BASE_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_tollan_base_block", RegistryHelper.beSupplier(StargateTollanBaseBE::new, JSGBlocks.STARGATE_TOLLAN_BASE_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateTollanMemberBE.StargateTollanChevronBE>> STARGATE_TOLLAN_CHEVRON_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_tollan_chevron_block", RegistryHelper.beSupplier(StargateTollanMemberBE.StargateTollanChevronBE::new, JSGBlocks.STARGATE_TOLLAN_CHEVRON_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateTollanMemberBE.StargateTollanRingBE>> STARGATE_TOLLAN_RING_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_tollan_ring_block", RegistryHelper.beSupplier(StargateTollanMemberBE.StargateTollanRingBE::new, JSGBlocks.STARGATE_TOLLAN_RING_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateMovieBaseBE>> STARGATE_MOVIE_BASE_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_movie_base_block", RegistryHelper.beSupplier(StargateMovieBaseBE::new, JSGBlocks.STARGATE_MOVIE_BASE_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateMovieMemberBE.StargateMovieChevronBE>> STARGATE_MOVIE_CHEVRON_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_movie_chevron_block", RegistryHelper.beSupplier(StargateMovieMemberBE.StargateMovieChevronBE::new, JSGBlocks.STARGATE_MOVIE_CHEVRON_BLOCK));
    public static final RegistryObject<BlockEntityType<StargateMovieMemberBE.StargateMovieRingBE>> STARGATE_MOVIE_RING_BE = JSGApi.REGISTRY_HELPER.be().register("stargate_movie_ring_block", RegistryHelper.beSupplier(StargateMovieMemberBE.StargateMovieRingBE::new, JSGBlocks.STARGATE_MOVIE_RING_BLOCK));

    public static final RegistryObject<BlockEntityType<DHDMilkyWayBE>> DHD_MILKYWAY = JSGApi.REGISTRY_HELPER.be().register("dhd_milkyway", RegistryHelper.beSupplier(DHDMilkyWayBE::new, JSGBlocks.DHD_MILKYWAY));
    public static final RegistryObject<BlockEntityType<DHDPegasusBE>> DHD_PEGASUS = JSGApi.REGISTRY_HELPER.be().register("dhd_pegasus", RegistryHelper.beSupplier(DHDPegasusBE::new, JSGBlocks.DHD_PEGASUS));

    public static final RegistryObject<BlockEntityType<OrbanNaquadahGeneratorBE>> ORBAN_NAQUADAH_GENERATOR = null; //registerBE("orban_naquadah_generator", OrbanNaquadahGeneratorBE::new, BlockRegistry.ORBAN_NAQUADAH_GENERATOR_BLOCK);
    public static final RegistryObject<BlockEntityType<ToasterBE>> TOASTER = JSGApi.REGISTRY_HELPER.be().register("toaster", RegistryHelper.beSupplier(ToasterBE::new, JSGBlocks.TOASTER));
    public static final RegistryObject<BlockEntityType<PrinterBE>> PRINTER = JSGApi.REGISTRY_HELPER.be().register("printer", RegistryHelper.beSupplier(PrinterBE::new, JSGBlocks.PRINTER));

    public static final RegistryObject<BlockEntityType<JUBCableBE>> JUB_CABLE = null; //registerBE("jub_cable", JUBCableBE::new, BlockRegistry.JUB_CABLE);

    public static void init() {
        JSGApi.REGISTRY_HELPER.beRenderers(() -> List.of(
                new RegistryHelper.BlockEntityRendererPair<>(STARGATE_ORLIN_BASE_BE.get(), StargateOrlinRenderer::new),
                new RegistryHelper.BlockEntityRendererPair<>(STARGATE_MILKYWAY_BASE_BE.get(), StargateMilkyWayRenderer::new),
                new RegistryHelper.BlockEntityRendererPair<>(STARGATE_PEGASUS_BASE_BE.get(), StargatePegasusRenderer::new),
                new RegistryHelper.BlockEntityRendererPair<>(STARGATE_UNIVERSE_BASE_BE.get(), StargateUniverseRenderer::new),
                new RegistryHelper.BlockEntityRendererPair<>(STARGATE_TOLLAN_BASE_BE.get(), StargateTollanRenderer::new),
                new RegistryHelper.BlockEntityRendererPair<>(STARGATE_MOVIE_BASE_BE.get(), StargateMovieRenderer::new),
                new RegistryHelper.BlockEntityRendererPair<>(DHD_MILKYWAY.get(), DHDMilkyWayRenderer::new),
                new RegistryHelper.BlockEntityRendererPair<>(DHD_PEGASUS.get(), DHDPegasusRenderer::new),
                new RegistryHelper.BlockEntityRendererPair<>(PRINTER.get(), PrinterRenderer::new)
                //new RegistryHelper.BlockEntityRendererPair<>(ORBAN_NAQUADAH_GENERATOR.get(), OrbanNaquadahGeneratorRenderer::new)
        ));
    }
}
