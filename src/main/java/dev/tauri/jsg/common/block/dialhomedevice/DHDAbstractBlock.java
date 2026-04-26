package dev.tauri.jsg.common.block.dialhomedevice;

import dev.tauri.jsg.client.screen.inventory.dialhomedevice.DHDContainer;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.core.common.block.TickableBEBlock;
import dev.tauri.jsg.core.common.block.util.IHighlightBlock;
import dev.tauri.jsg.core.common.block.util.IItemBlock;
import dev.tauri.jsg.core.common.blockentity.ILinkable;
import dev.tauri.jsg.core.common.blockstate.JSGProperties;
import dev.tauri.jsg.core.common.helper.BlockPosHelper;
import dev.tauri.jsg.core.common.helper.ItemHandlerHelper;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import dev.tauri.jsg.core.common.util.RotationUtil;
import dev.tauri.jsg.core.common.util.math.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
public abstract class DHDAbstractBlock extends TickableBEBlock implements IHighlightBlock, IItemBlock {
    protected static final Properties DHD_PROPS = Properties.of()
            .explosionResistance(30f)
            .destroyTime(3f)
            .isRedstoneConductor((BlockState state, BlockGetter getter, BlockPos pos) -> true)
            .isViewBlocking((BlockState state, BlockGetter getter, BlockPos pos) -> false)
            .requiresCorrectToolForDrops()
            .sound(SoundType.METAL)
            .noOcclusion();

    public DHDAbstractBlock() {
        super(DHD_PROPS);
        this.registerDefaultState(
                defaultBlockState()
                        .setValue(JSGProperties.ROTATION_PROPERTY, 0)
                        .setValue(BlockStateProperties.WATERLOGGED, false)
        );
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(JSGProperties.ROTATION_PROPERTY, BlockPosHelper.rotateDHDDir(blockState.getValue(JSGProperties.ROTATION_PROPERTY), rotation));
    }

    @Override
    public boolean renderHighlight(BlockState blockState) {
        return false;
    }

    // ------------------------------------------------------------------------


    @Override
    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        // Server side
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof DHDAbstractBE dhd) {
            dhd.updateLinkStatus(level, pos);
        }
    }


    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(JSGProperties.ROTATION_PROPERTY);
        builder.add(JSGProperties.SNOWY);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull FluidState getFluidState(BlockState p_152045_) {
        return p_152045_.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_152045_);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var state = defaultBlockState();
        var level = ctx.getLevel();
        var pos = ctx.getClickedPos();
        var player = ctx.getPlayer();
        boolean snowy = isSnowAroundBlock(level, pos);
        if (player != null) {
            int facing = MathHelper.floor((double) (player.getYHeadRot() * 16.0F / 360.0F) + 0.5D) & 0x0F;
            state = state.setValue(JSGProperties.ROTATION_PROPERTY, facing).setValue(JSGProperties.SNOWY, snowy);
        }
        return state.setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
    }

    public final static BlockMatchTest SNOW_MATCHER = new BlockMatchTest(Blocks.SNOW);

    public static boolean isSnowAroundBlock(Level world, BlockPos inPos) {

        // Check if 4 adjacent blocks are snow layers
        for (Direction facing : JSGProperties.FACING_HORIZONTAL_PROPERTY.getPossibleValues()) {
            BlockPos pos = inPos.offset(facing.getNormal());
            if (!SNOW_MATCHER.test(world.getBlockState(pos), world.random)) {
                return false;
            }
        }

        return true;
    }

    @NotNull
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.FAIL;
        var playerRot = player.getYRot();
        var dhdRot = ((float) state.getValue(JSGProperties.ROTATION_PROPERTY)) * 22.5f;
        boolean backActivation = RotationUtil.getClosestAngleDistance(playerRot, dhdRot, false) >= (180 - 45);
        if (!backActivation || player.isShiftKeyDown()) return InteractionResult.FAIL;
        if (!(level.getBlockEntity(pos) instanceof DHDAbstractBE dhd)) return InteractionResult.FAIL;
        if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, null)) {
            if (!level.isClientSide())
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1, 1);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (!dhd.tryInsertUpgrade(player, hand)) {
            if (player instanceof ServerPlayer sp) {
                NetworkHooks.openScreen(sp, new SimpleMenuProvider((id, pInv, p) -> new DHDContainer(id, pInv, dhd), Component.empty()), pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("all")
    public void playerWillDestroy(Level level, BlockPos pos, BlockState blockState, Player player) {
        super.playerWillDestroy(level, pos, blockState, player);
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DHDAbstractBE dhdTile) {
                ItemHandlerHelper.dropInventoryItems(level, pos, dhdTile.getCapability(ForgeCapabilities.ITEM_HANDLER, null).resolve().get());
                if (dhdTile.getLinkedDevice() instanceof ILinkable<?> linkable) linkable.setLinkedDevice(null);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("all")
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DHDAbstractBE dhdTile) {
                ItemHandlerHelper.dropInventoryItems(level, pos, dhdTile.getCapability(ForgeCapabilities.ITEM_HANDLER, null).resolve().get());
                if (dhdTile.getLinkedDevice() instanceof ILinkable<?> linkable) linkable.setLinkedDevice(null);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        int rotation = (int) (blockState.getValue(JSGProperties.ROTATION_PROPERTY) * 22.5f);

        AABB aabb;

        if (rotation % 90 == 0)
            aabb = new JSGAxisAlignedBB(-0.5, 0, -0.25, 0.5, 1.3, 0.25).rotate(rotation).offset(0.5, 0, 0.5);
        else
            aabb = new JSGAxisAlignedBB(0.25, 0, 0.25, 0.75, 1.3, 0.75);

        return Shapes.create(aabb);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }
}
