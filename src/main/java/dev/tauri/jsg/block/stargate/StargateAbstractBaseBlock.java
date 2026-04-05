package dev.tauri.jsg.block.stargate;

import dev.tauri.jsg.api.block.stargate.IStargateBlock;
import dev.tauri.jsg.api.block.stargate.IStargateChevronBlock;
import dev.tauri.jsg.api.block.stargate.IStargateRingBlock;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.core.common.block.TickableBEBlock;
import dev.tauri.jsg.core.common.blockentity.CamouflageBE;
import dev.tauri.jsg.core.common.helper.BlockPosHelper;
import dev.tauri.jsg.core.common.helper.ItemHandlerHelper;
import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.item.ITabbedItem;
import dev.tauri.jsg.core.common.item.JSGBlockItem;
import dev.tauri.jsg.core.common.multistructure.merging.IMergeHelper;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import dev.tauri.jsg.item.stargate.StargateBaseItem;
import dev.tauri.jsg.multistructure.mergehelper.StargateAbstractMergeHelper;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public abstract class StargateAbstractBaseBlock extends TickableBEBlock implements dev.tauri.jsg.core.common.block.util.IItemBlock, IStargateBlock, dev.tauri.jsg.core.common.block.util.IHighlightBlock, SimpleWaterloggedBlock, ITabbedItem, dev.tauri.jsg.core.common.block.util.WrenchRotatable {
    protected static final Properties STARGATE_BASE_PROPS = Properties.of()
            .strength(2.5F, 30.0F)
            .isRedstoneConductor((BlockState state, BlockGetter getter, BlockPos pos) -> true)
            .isViewBlocking((BlockState state, BlockGetter getter, BlockPos pos) -> false)
            .emissiveRendering((BlockState state, BlockGetter getter, BlockPos pos) -> false)
            .lightLevel((s) -> 0)
            .noOcclusion()
            .sound(SoundType.METAL)
            .pushReaction(PushReaction.BLOCK)
            .requiresCorrectToolForDrops();


    public StargateAbstractBaseBlock(Properties properties) {
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
    public JSGBlockItem getItemBlock() {
        return new StargateBaseItem(this, new Item.Properties(), getTabs());
    }

    @Nullable
    public RegistryObject<CreativeModeTab> getTab() {
        return CoreTabs.TAB_TRANSPORTATION;
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
        if (be instanceof Stargate<?> gate) {
            if (gate.isMerged()) return;
            var facing = gate.getFacing();
            var vertical = gate.getFacingVertical();
            var next = getNextDirections(facing, vertical);
            state = state.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, next.left()).setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY, dev.tauri.jsg.core.common.blockstate.JSGProperties.getVerticalFacingByDirection(next.right()));
            context.getLevel().setBlock(context.getClickedPos(), state, 3);
            gate.getMergeHelper().updateMemberStateAndCheck(null);
        }
    }

    public static Pair<Direction, Direction> getNextDirections(Direction facing, Direction facingVertical) {
        if (facingVertical == Direction.UP)
            return Pair.of(facing, Direction.DOWN);
        if (facingVertical == Direction.DOWN)
            return Pair.of(facing.getClockWise(), Direction.SOUTH);
        return Pair.of(facing, Direction.UP);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, BlockPosHelper.flipDir(blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY), mirror));
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


    @NotNull
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean shift = player.isShiftKeyDown();
        boolean autoBuild = false;
        boolean guiDisplayed = false;
        boolean camoChanged = false;
        if (!level.isClientSide) {
            if (!shift) {
                autoBuild = tryAutoBuild(player, level, pos, hand);
                if (!autoBuild && hand == InteractionHand.MAIN_HAND) {
                    if (level.getBlockEntity(pos) instanceof CamouflageBE tile) {
                        camoChanged = tile.setCamoBlockByHeldItem(player.getItemInHand(hand), player, new BlockPlaceContext(level, player, hand, player.getItemInHand(hand), hitResult));
                        if (!camoChanged)
                            guiDisplayed = showGateInfo(player, hand, level, pos);
                    }
                }
            }
        } else return InteractionResult.sidedSuccess(true);
        return (!shift && (autoBuild || guiDisplayed || camoChanged)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    protected abstract boolean showGateInfo(Player player, InteractionHand hand, Level world, BlockPos pos);

    protected boolean tryAutoBuild(Player player, Level world, BlockPos basePos, InteractionHand hand) {
        final var gateTile = (Stargate<?>) world.getBlockEntity(basePos);
        if (gateTile == null) {
            return false;
        }
        IMergeHelper mergeHelper = gateTile.getMergeHelper();
        ItemStack stack = player.getItemInHand(hand);

        if (!gateTile.isMerged()) {

            // This check ensures that stack represents matching member block.
            boolean chevron = (Block.byItem(stack.getItem()) instanceof IStargateChevronBlock);
            boolean ring = (Block.byItem(stack.getItem()) instanceof IStargateRingBlock);
            if (!chevron && !ring) {
                return false;
            }

            List<StargateAbstractMergeHelper.MemberAutoBuildBlock> posList = mergeHelper.getAbsentBlockPositions(world, chevron);

            if (!posList.isEmpty()) {
                BlockPos pos = posList.get(0).pos;
                BlockState state = posList.get(0).state;

                world.setBlock(pos, state, 3);
                SoundType soundType = state.getSoundType();
                world.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

                if (!player.isCreative()) stack.shrink(1);

                if (posList.size() == 1)
                    mergeHelper.updateMemberStateAndCheck(null);

                return true;
            }
        }

        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        if (level.isClientSide) return;
        BlockEntity e = level.getBlockEntity(blockPos);
        if (e instanceof Stargate<?> stargate) {
            stargate.getMergeHelper().updateMemberStateAndCheck(null);
            stargate.refresh();
            stargate.updateContainerItemsByItemStack(itemStack);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void playerWillDestroy(Level level, BlockPos pos, BlockState blockState, Player player) {
        super.playerWillDestroy(level, pos, blockState, player);
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof Stargate<?> stargate) {
                stargate.getMergeHelper().updateMemberStateAndCheck(false);
                if (player.isCreative()) {
                    var drop = stargate.getDropBaseBlock((ServerPlayer) player);
                    ItemHandlerHelper.spawnItemStack(level, pos, drop);
                }
                stargate.onGateBroken();
            }
        }
    }

    /*_
    IDK WHY THIS IS HERE... CAUSING TROUBLES WHEN MERGIN/UNMERGIN GATES -> DO *NOT* UNCOMMENT!!!!!!!

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof StargateAbstractBaseBE gateTile) {
                gateTile.mergeHelper.updateMemberStateAndCheck(null);
            }
        }
    }*/

    @Override
    @ParametersAreNonnullByDefault
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof Stargate<?> gateTile) {
                gateTile.getMergeHelper().updateMemberStateAndCheck(false);
                gateTile.onGateBroken();
            }
        }
    }

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


    public static VoxelShape getGateBlockShape(BlockState blockState, BlockGetter level, BlockPos pos) {
        if (blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY))
            return Shapes.block();
        return Shapes.create(getGateBlockShapeAABB(blockState, level, pos));
    }

    public static JSGAxisAlignedBB getGateBlockShapeAABB(BlockState blockState, BlockGetter level, BlockPos pos) {
        var tile = level.getBlockEntity(pos);
        if (tile instanceof CamouflageBE camoBE && !camoBE.getCamoBlock().isAir())
            return JSGAxisAlignedBB.block();
        return getGateBlockShapeAABB(blockState);
    }

    public static JSGAxisAlignedBB getGateBlockShapeAABB(BlockState blockState) {
        if (blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY))
            return JSGAxisAlignedBB.block();

        BlockPos min = new BlockPos(0, 0, 4);
        BlockPos max = new BlockPos(16, 16, 12);

        Direction horDir = blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY);
        Direction verDir = dev.tauri.jsg.core.common.blockstate.JSGProperties.getDirectionByVerticalFacing(blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY));

        if (verDir != null) {
            min = new BlockPos(0, 4, 0);
            max = new BlockPos(16, 12, 16);
        } else {
            switch (horDir) {
                case EAST:
                case WEST:
                    min = new BlockPos(4, 0, 0);
                    max = new BlockPos(12, 16, 16);
                    break;
                default:
                    break;
            }
        }

        return new JSGAxisAlignedBB(
                Math.min(Math.abs(min.getX() / 16D), Math.abs(max.getX() / 16D)), Math.min(Math.abs(min.getY() / 16D), Math.abs(max.getY() / 16D)), Math.min(Math.abs(min.getZ() / 16D), Math.abs(max.getZ() / 16D)),
                Math.max(Math.abs(min.getX() / 16D), Math.abs(max.getX() / 16D)), Math.max(Math.abs(min.getY() / 16D), Math.abs(max.getY() / 16D)), Math.max(Math.abs(min.getZ() / 16D), Math.abs(max.getZ() / 16D))
        );
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }
}
