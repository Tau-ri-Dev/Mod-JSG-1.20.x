package dev.tauri.jsg.block.stargate;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.blockentity.stargate.StargateOrlinBaseBE;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemFilled;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class StargateOrlinBaseBlock extends StargateAbstractBaseBlock {
    public StargateOrlinBaseBlock() {
        super(STARGATE_BASE_PROPS.isRedstoneConductor((pState, level, pos) -> true));
        this.registerDefaultState(defaultBlockState().setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ORLIN_BROKEN, false));
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(dev.tauri.jsg.core.common.blockstate.JSGProperties.ORLIN_BROKEN);
    }

    @Override
    protected boolean showGateInfo(Player player, InteractionHand hand, Level world, BlockPos pos) {
        if (world.isClientSide) return false;
        if (!(world.getBlockEntity(pos) instanceof StargateOrlinBaseBE baseTile)) return false;
        var energyStorage = baseTile.getCapability(ForgeCapabilities.ENERGY).resolve();
        if (energyStorage.isEmpty()) return false;
        int energyStored = baseTile.getEnergyManager().getStorage().getEnergyStored();
        var energyString = String.format("%,d", energyStored);
        var energyNeeded = baseTile.getEnergyRequiredToDial();
        if (energyNeeded == null) return false;
        var neededString = String.format("%,d", energyNeeded.energyToOpen);
        boolean hasEnergy = (energyStored >= energyNeeded.energyToOpen);
        double secondsLeft = baseTile.getEnergyManager().getSecondsToClose();
        var secondsLeftString = String.format("%.2f", Math.max(0, secondsLeft));

        player.sendSystemMessage(Component.translatable("chat.orlins.energyStored", (hasEnergy ? "§a" : "§c") + energyString, "§2" + neededString, "§2" + secondsLeftString));
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable BlockGetter blockGetter, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        if (itemStack.hasTag()) {
            CompoundTag compound = itemStack.getOrCreateTag();

            if (compound.contains("openCount")) {
                components.add(Component.translatable("block.jsg.stargate_orlin_base_block.open_count", compound.getInt("openCount"), JSGConfig.Stargate.stargateOrlinMaxOpenCount.get()));
            }
            if (compound.contains("notebook_page")) {
                var page = (CompoundTag) compound.get("notebook_page");
                if (page != null) {
                    var name = PageNotebookItemFilled.getNameFromCompound(page);
                    components.add(Component.literal(ChatFormatting.AQUA + name));
                }
            }
        }
        super.appendHoverText(itemStack, blockGetter, components, tooltipFlag);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new StargateOrlinBaseBE(pPos, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        if (level.getBlockEntity(blockPos) instanceof StargateOrlinBaseBE baseTile) {
            baseTile.initializeFromItemStack(itemStack);
        }
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        if (pLevel.getBlockEntity(pPos) instanceof StargateOrlinBaseBE baseTile) {
            baseTile.redstonePowerUpdate(pLevel.hasNeighborSignal(pPos));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if (blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY))
            return Shapes.block();
        if (dev.tauri.jsg.core.common.blockstate.JSGProperties.getDirectionByVerticalFacing(blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY)) == null)
            return Shapes.create(0, 0, 0, 1, 0.4, 1);
        return Shapes.create(0, 0.25, 0, 1, 0.75, 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }
}
