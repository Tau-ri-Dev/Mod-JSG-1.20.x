package dev.tauri.jsg.block.stargate;

import dev.tauri.jsg.api.block.stargate.IStargateBlock;
import dev.tauri.jsg.blockentity.stargate.StargateAbstractMemberBE;
import dev.tauri.jsg.core.common.block.TickableBEBlock;
import dev.tauri.jsg.core.common.blockentity.CamouflageBE;
import dev.tauri.jsg.core.common.helper.BlockPosHelper;
import dev.tauri.jsg.core.common.helper.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static dev.tauri.jsg.block.stargate.StargateAbstractBaseBlock.getNextDirections;

public abstract class StargateAbstractMemberBlock extends TickableBEBlock implements IStargateBlock, dev.tauri.jsg.core.common.block.util.IHighlightBlock, SimpleWaterloggedBlock, dev.tauri.jsg.core.common.block.util.WrenchRotatable {
    protected static final Properties STARGATE_MEMBER_PROPS = Properties.of()
            .strength(2.5f, 30f)
            .isRedstoneConductor((BlockState state, BlockGetter getter, BlockPos pos) -> true)
            .isViewBlocking((BlockState state, BlockGetter getter, BlockPos pos) -> false)
            .noOcclusion()
            .requiresCorrectToolForDrops()
            .pushReaction(PushReaction.BLOCK)
            .sound(SoundType.METAL);

    public StargateAbstractMemberBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                defaultBlockState()
                        .setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY, true)
                        .setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, Direction.NORTH)
                        .setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY, 0)
                        .setValue(BlockStateProperties.WATERLOGGED, false)
        );
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, BlockPosHelper.rotateDir(blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY), rotation));
    }

    @Override
    public void onWrenchUse(BlockState state, UseOnContext context) {
        var be = context.getLevel().getBlockEntity(context.getClickedPos());
        if (be instanceof StargateAbstractMemberBE member) {
            var gate = member.getBaseTile(context.getLevel());
            if (gate != null) {
                if (gate.isMerged()) return;
                var facing = gate.getFacing();
                var vertical = gate.getFacingVertical();
                var next = getNextDirections(facing, vertical);
                state = state.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, next.left()).setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY, dev.tauri.jsg.core.common.blockstate.JSGProperties.getVerticalFacingByDirection(next.right()));
                context.getLevel().setBlock(context.getClickedPos(), state, 3);
                gate.getMergeHelper().updateMemberStateAndCheck(null);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, BlockPosHelper.flipDir(blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY), mirror));
    }

    @NotNull
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean shift = player.isShiftKeyDown();
        boolean guiDisplayed = false;
        boolean camoChanged = false;
        if (!level.isClientSide) {
            if (!shift) {
                if (level.getBlockEntity(pos) instanceof StargateAbstractMemberBE tile) {
                    if (tile.basePos != null && hand == InteractionHand.MAIN_HAND) {
                        if (level.getBlockState(tile.basePos).getBlock() instanceof StargateAbstractBaseBlock block) {
                            camoChanged = tile.setCamoBlockByHeldItem(player.getItemInHand(hand), player, new BlockPlaceContext(level, player, hand, player.getItemInHand(hand), hitResult));
                            if (!camoChanged)
                                guiDisplayed = block.showGateInfo(player, hand, level, tile.basePos);
                        }
                    }
                }
            }
        } else return InteractionResult.sidedSuccess(true);
        return (!shift && (guiDisplayed || camoChanged)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof StargateAbstractMemberBE member) {
                member.dropCamo();
            }
        }
    }

    @Override
    public boolean renderHighlight(BlockState blockState, BlockGetter level, BlockPos pos) {
        var tile = level.getBlockEntity(pos);
        if (tile instanceof CamouflageBE camoBE && !camoBE.getCamoBlock().isAir()) return true;
        return blockState.hasProperty(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY) && blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable BlockGetter blockGetter, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        ItemHelper.applyGenericToolTip(this.getDescriptionId(), components, tooltipFlag);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        if (level.isClientSide) return;
        BlockEntity e = level.getBlockEntity(blockPos);
        if (e instanceof StargateAbstractMemberBE) {
            ((StargateAbstractMemberBE) e).findBaseAndUpdateMergeState(null);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void playerWillDestroy(Level level, BlockPos pos, BlockState blockState, Player player) {
        super.playerWillDestroy(level, pos, blockState, player);
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof StargateAbstractMemberBE e) {
                e.findBaseAndUpdateMergeState(false);
                e.dropCamo();
            }
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (player.isCreative()) {
            if (!level.isClientSide()) {
                if (level.getBlockEntity(pos) instanceof StargateAbstractMemberBE e) {
                    var camoChanged = e.setCamoBlockByHeldItem(ItemStack.EMPTY, player, null);
                    if (camoChanged) {
                        return false;
                    }
                }
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!player.isCreative()) {
            if (!level.isClientSide()) {
                if (level.getBlockEntity(pos) instanceof StargateAbstractMemberBE e) {
                    e.setCamoBlockByHeldItem(ItemStack.EMPTY, player, null);
                }
            }
        }
        super.attack(state, level, pos, player);
    }

    /*_
    IDK WHY THIS IS HERE... CAUSING TROUBLES WHEN MERGIN/UNMERGIN GATES -> DO *NOT* UNCOMMENT!!!!!!!
    @Override
    @ParametersAreNonnullByDefault
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof StargateAbstractMemberBE e) {
                e.findBaseAndUpdateMergeState(null);
            }
        }
    }*/

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY);
        builder.add(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY);
        builder.add(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY);
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
        float pitch = 0;
        if (ctx.getPlayer() != null)
            pitch = ctx.getPlayer().getXRot();
        return defaultBlockState()
                .setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, ctx.getHorizontalDirection().getOpposite())
                .setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY, dev.tauri.jsg.core.common.blockstate.JSGProperties.getVerticalFacingByDirection(dev.tauri.jsg.core.common.blockstate.JSGProperties.getVerticalDirectionByPitch(pitch).getOpposite()))
                .setValue(BlockStateProperties.WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public RenderShape getRenderShape(BlockState blockState) {
        return (blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY) ? RenderShape.MODEL : RenderShape.ENTITYBLOCK_ANIMATED);
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return (blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY) ? Shapes.block() : StargateAbstractBaseBlock.getGateBlockShape(blockState, blockGetter, blockPos));
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getVisualShape(BlockState blockState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return (blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY) ? Shapes.block() : Shapes.empty());
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }
}
