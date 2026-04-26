package dev.tauri.jsg.common.block.stargate.redstone;

import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicMemberBE;
import dev.tauri.jsg.core.common.block.JSGBlock;
import dev.tauri.jsg.core.common.blockstate.JSGProperties;
import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.item.ITabbedItem;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public abstract class AbstractStargateRedstoneIO extends JSGBlock implements ITabbedItem {

    protected static final Properties IO_PROPERTIES = BlockBehaviour.Properties.of().noOcclusion().isRedstoneConductor((BlockState pState, BlockGetter pLevel, BlockPos pPos) -> true);

    public AbstractStargateRedstoneIO() {
        super(IO_PROPERTIES);
        this.registerDefaultState(defaultBlockState().setValue(JSGProperties.FACING_HORIZONTAL_PROPERTY, Direction.SOUTH).setValue(BlockStateProperties.LIT, false));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    public StargateClassicBaseBE<?> getGate(BlockGetter level, BlockPos pos, BlockState state) {
        if (level == null) return null;
        if (state == null) return null;
        if (pos == null) return null;
        var facing = state.getValue(JSGProperties.FACING_HORIZONTAL_PROPERTY).getOpposite();
        var tile = level.getBlockEntity(pos.offset(facing.getNormal()));
        if (tile instanceof StargateClassicBaseBE<?> sg)
            return sg;
        if (tile instanceof StargateClassicMemberBE member && member.basePos != null && level.getBlockEntity(member.basePos) instanceof StargateClassicBaseBE<?> sg)
            return sg;
        return null;
    }


    @Nullable
    public RegistryObject<CreativeModeTab> getTab() {
        return CoreTabs.TAB_TRANSPORTATION;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable BlockGetter blockGetter, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        ItemHelper.applyGenericToolTip(this.getDescriptionId(), components, tooltipFlag);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (!pLevel.isClientSide()) {
            var sg = getGate(pLevel, pPos, pState);
            if (sg != null) {
                sg.addRedstoneDevice(pPos);
                pLevel.setBlock(pPos, pState.setValue(BlockStateProperties.LIT, true), 3);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlacer) {
        super.playerWillDestroy(pLevel, pPos, pState, pPlacer);
        if (!pLevel.isClientSide()) {
            var sg = getGate(pLevel, pPos, pState);
            if (sg != null) {
                sg.removeRedstoneDevice(pPos);
            }
        }
    }

    public abstract boolean shouldUpdateNeighbours(BlockState state, ServerLevel level, BlockPos pos, StargateClassicBaseBE<?> gateTile);

    @ParametersAreNonnullByDefault
    public void tickFromStargate(BlockState state, ServerLevel level, BlockPos pos, StargateClassicBaseBE<?> gateTile) {
        if (!shouldUpdateNeighbours(state, level, pos, gateTile)) return;
        updateNeighbours(level, pos, state);
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(JSGProperties.FACING_HORIZONTAL_PROPERTY);
        builder.add(BlockStateProperties.LIT);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(JSGProperties.FACING_HORIZONTAL_PROPERTY, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        var gateTile = getGate(pLevel, pPos, pState);
        if (gateTile == null) return 0;
        return getOutputSignal(pState, pLevel, pPos, pDirection, gateTile);
    }

    public static void updateNeighbours(Level pLevel, BlockPos pPos, BlockState pState) {
        Block block = pState.getBlock();
        pLevel.updateNeighborsAt(pPos, block);
        pLevel.updateNeighborsAt(pPos.below(), block);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        var gateTile = getGate(pLevel, pPos, pState);
        if (gateTile == null) return;
        var blockDirection = pState.getValue(JSGProperties.FACING_HORIZONTAL_PROPERTY);
        var signals = new HashMap<Direction, Integer>();
        for (var direction : Direction.values()) {
            var directionRotated = Direction.fromYRot(blockDirection.toYRot() + direction.toYRot());
            if (blockDirection.getAxis() == Direction.Axis.Z) directionRotated = directionRotated.getOpposite();
            signals.put(direction.getAxis() == Direction.Axis.Y ? direction : directionRotated, pLevel.getSignal(pPos.offset(direction.getNormal()), direction));
        }
        processInputSignal(pState, pLevel, pPos, pFromPos, signals, gateTile);
    }


    @ParametersAreNonnullByDefault
    public abstract int getOutputSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction, StargateClassicBaseBE<?> gateTile);

    @ParametersAreNonnullByDefault
    public abstract void processInputSignal(BlockState state, BlockGetter level, BlockPos pos, BlockPos changedPos, Map<Direction, Integer> signals, StargateClassicBaseBE<?> gateTile);
}
