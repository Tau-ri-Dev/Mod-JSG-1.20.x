package dev.tauri.jsg.common.block.stargate;

import dev.tauri.jsg.api.block.stargate.IStargateRingBlock;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.common.blockentity.stargate.StargateOrlinBaseBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateOrlinMemberBE;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
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
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class StargateOrlinMemberBlock extends StargateAbstractMemberBlock implements IStargateRingBlock {
    public StargateOrlinMemberBlock() {
        super(STARGATE_MEMBER_PROPS);
        this.registerDefaultState(defaultBlockState().setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ORLIN_BROKEN, false));
    }

    @Nullable
    @Override
    public RegistryObject<CreativeModeTab> getTab() {
        return CoreTabs.TAB_TRANSPORTATION;
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(dev.tauri.jsg.core.common.blockstate.JSGProperties.ORLIN_BROKEN);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new StargateOrlinMemberBE(pPos, pState);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable BlockGetter blockGetter, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        if (itemStack.hasTag()) {
            CompoundTag compound = itemStack.getTag();

            if (compound != null && compound.contains("openCount")) {
                components.add(Component.translatable("block.jsg.stargate_orlin_base_block.open_count", compound.getInt("openCount"), JSGConfig.Stargate.stargateOrlinMaxOpenCount.get()));
            }
        }
        super.appendHoverText(itemStack, blockGetter, components, tooltipFlag);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        if (level.getBlockEntity(blockPos) instanceof StargateOrlinMemberBE tile) {
            tile.initializeFromItemStack(itemStack);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if (blockState.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY))
            return Shapes.block();
        var gateShape = StargateAbstractBaseBlock.getGateBlockShapeAABB(blockState);
        if (blockGetter.getBlockEntity(blockPos.below(2)) instanceof StargateOrlinBaseBE) {
            // this member is on top of the stargate and stargate is vertical
            gateShape = gateShape.mul(16);
            return Shapes.create(new JSGAxisAlignedBB(gateShape.getMinBlockPos().above(10), gateShape.getMaxBlockPos()).mul(1f / 16f));
        }
        return Shapes.create(gateShape);
    }
}
