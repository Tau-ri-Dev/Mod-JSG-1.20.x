package dev.tauri.jsg.block.jub;

import dev.tauri.jsg.blockentity.jub.JUBCableBE;
import dev.tauri.jsg.capability.JSGCapabilities;
import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.item.ITabbedItem;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class JUBCableBlock extends JUBDeviceBlock implements SimpleWaterloggedBlock, ITabbedItem {
    public JUBCableBlock() {
        super(Properties.of().dynamicShape().noOcclusion());
        this.registerDefaultState(
                defaultBlockState()
                        //.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.JUB_VARIANT, JUBCableVariant.CENTER)
                        .setValue(BlockStateProperties.WATERLOGGED, false)
        );
    }

    @Nullable
    public RegistryObject<CreativeModeTab> getTab() {
        return CoreTabs.TAB_ENERGY;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @javax.annotation.Nullable BlockGetter blockGetter, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        ItemHelper.applyGenericToolTip(this.getDescriptionId(), components, tooltipFlag);
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        //builder.add(dev.tauri.jsg.core.common.blockstate.JSGProperties.JUB_VARIANT);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull FluidState getFluidState(BlockState p_152045_) {
        return p_152045_.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_152045_);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new JUBCableBE(pPos, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        // we don't want villagers to stick on the cable
        return false;
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext ctx) {
        // get state by available connections
        var state = defaultBlockState();

        var pos = ctx.getClickedPos().immutable();
        var level = ctx.getLevel();
        var connections = new ArrayList<Direction>();
        for (var d : Direction.values()) {
            var be = level.getBlockEntity(pos.offset(d.getNormal()));
            if (be == null) continue;
            var optCap = be.getCapability(JSGCapabilities.JUST_UNIVERSAL_BUS);
            if (!optCap.isPresent() || optCap.resolve().isEmpty()) continue;
            connections.add(d);
        }
        return state;//.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.JUB_VARIANT, JUBCableVariant.fromDirections(connections));
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        // get shape by state
        return Shapes.block();
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader reader, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, reader, pos, neighbor);
        updateCableShape(state, reader, pos, neighbor);
    }

    protected void updateCableShape(BlockState state, LevelReader reader, BlockPos pos, BlockPos neighbor) {
        if (!(reader instanceof Level level)) return;
        if (level.isClientSide()) return;
        /*var be = level.getBlockEntity(neighbor);
        var connections = state.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.JUB_VARIANT).getConnections();
        var newState = state;
        var delta = neighbor.subtract(pos);
        var dir = Direction.fromDelta(delta.getX(), delta.getY(), delta.getZ());
        if (be == null || !be.getCapability(JSGCapabilities.JUST_UNIVERSAL_BUS).isPresent() || be.getCapability(JSGCapabilities.JUST_UNIVERSAL_BUS).resolve().isEmpty()) {
            if (connections.contains(dir)) {
                connections.remove(dir);
                newState = newState.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.JUB_VARIANT, JUBCableVariant.fromDirections(connections));
                level.setBlock(pos, newState, 3);
            }
        } else if (!connections.contains(dir)) {
            connections.add(dir);
            newState = newState.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.JUB_VARIANT, JUBCableVariant.fromDirections(connections));
            level.setBlock(pos, newState, 3);
        }*/
    }
}
