package dev.tauri.jsg.common.block.energy;

import dev.tauri.jsg.common.blockentity.energy.ZPMHubBE;
import dev.tauri.jsg.common.container.ZPMHubContainer;
import dev.tauri.jsg.common.registry.JSGTabs;
import dev.tauri.jsg.core.common.block.TickableBEBlock;
import dev.tauri.jsg.core.common.block.util.IHighlightBlock;
import dev.tauri.jsg.core.common.blockstate.JSGProperties;
import dev.tauri.jsg.core.common.helper.BlockPosHelper;
import dev.tauri.jsg.core.common.helper.ItemHandlerHelper;
import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import dev.tauri.jsg.core.common.registry.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public class ZPMHubBlock extends TickableBEBlock implements ITabbedItem, IHighlightBlock {
    public ZPMHubBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .sound(SoundType.METAL)
                .strength(3.0f)
                .noOcclusion());
        registerDefaultState(defaultBlockState()
                .setValue(JSGProperties.FACING_HORIZONTAL_PROPERTY, Direction.NORTH));
    }

    @Override
    @ParametersAreNonnullByDefault
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (!level.isClientSide && player instanceof ServerPlayer sp && level.getBlockEntity(pos) instanceof ZPMHubBE hub) {
            sp.openMenu(new SimpleMenuProvider((id, inv, p) -> createMenu(id, inv, hub), Component.translatable(getDescriptionId())), pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    protected AbstractContainerMenu createMenu(int containerID, Inventory playerInventory, BlockEntity blockEntity) {
        return new ZPMHubContainer(containerID, playerInventory, blockEntity);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos pos, BlockState blockState, Player player) {
        BlockState result = super.playerWillDestroy(level, pos, blockState, player);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ZPMHubBE hub) {
            var handler = hub.getItemHandler();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty())
                    ItemHandlerHelper.spawnItemStack(level, pos, stack);
            }
        }
        return result;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ZPMHubBE(pos, state);
    }

    @Override
    public @Nullable RegistryObject<CreativeModeTab> getTab() {
        return JSGTabs.TAB_MACHINES;
    }

    @Override
    public boolean renderHighlight(BlockState blockState) {
        return false;
    }

    // ------------------------------------------------------------------------
    // Block states

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(JSGProperties.FACING_HORIZONTAL_PROPERTY);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext ctx) {
        return defaultBlockState()
                .setValue(JSGProperties.FACING_HORIZONTAL_PROPERTY, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(JSGProperties.FACING_HORIZONTAL_PROPERTY, BlockPosHelper.rotateDir(blockState.getValue(JSGProperties.FACING_HORIZONTAL_PROPERTY), rotation));
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.setValue(JSGProperties.FACING_HORIZONTAL_PROPERTY, BlockPosHelper.flipDir(blockState.getValue(JSGProperties.FACING_HORIZONTAL_PROPERTY), mirror));
    }
}
