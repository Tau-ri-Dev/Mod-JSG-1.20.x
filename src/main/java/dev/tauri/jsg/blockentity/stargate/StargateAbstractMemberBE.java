package dev.tauri.jsg.blockentity.stargate;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.config.JSGConfigUtil;
import dev.tauri.jsg.core.common.blockentity.CamouflageBE;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.multistructure.mergehelper.StargateAbstractMergeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class StargateAbstractMemberBE extends CamouflageBE implements ITickable {

    public abstract Block getBaseBlock();

    public BlockPos basePos = null;

    public void findBaseAndUpdateMergeState(Boolean force) {
        if (basePos == null && (force != null && !force)) return;
        StargateAbstractBaseBE<?, ?> be = (basePos != null ?
                (StargateAbstractBaseBE<?, ?>) Objects.requireNonNull(getLevel()).getBlockEntity(basePos)
                :
                StargateAbstractMergeHelper.findBaseTile(
                        getLevel(), getBlockPos(),
                        getCurrentBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY),
                        dev.tauri.jsg.core.common.blockstate.JSGProperties.getDirectionByVerticalFacing(getCurrentBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY)),
                        getBaseBlock()
                )
        );
        if (be == null) return;
        be.mergeHelper.updateMemberStateAndCheck(force);
    }

    public void setBaseTile(@Nullable BlockPos pos) {
        basePos = pos;
        setChanged();
    }


    public StargateAbstractMemberBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @SuppressWarnings("null")
    public BlockState getCurrentBlockState() {
        if (level == null) return getBlockState();
        return level.getBlockState(getBlockPos());
    }

    @Override
    public void tick(@NotNull Level level) {
        //JSG.LOGGER.info(String.valueOf(this.level.isClientSide()));
    }

    @Override
    protected boolean canBeUsedAsCamoBlock(BlockState blockState) {
        return JSGConfigUtil.canBeUsedAsCamoBlock(blockState);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
        if (capability == ForgeCapabilities.ENERGY) {
            if (basePos != null && level != null) {
                BlockEntity tile = level.getBlockEntity(basePos);
                if (tile instanceof StargateAbstractBaseBE<?, ?> baseTile)
                    return LazyOptional.of(() -> baseTile.getEnergyManager().getStorage()).cast();
            }
        }
        return super.getCapability(capability, facing);
    }


    /*
     * -----------------------------------
     *         NBT DATA SAVING
     * -----------------------------------
     */
    public void load(@NotNull CompoundTag compound) {
        if (compound.contains("basePos"))
            basePos = BlockPos.of(compound.getLong("basePos"));
        super.load(compound);
    }

    public void saveAdditional(@NotNull CompoundTag compound) {
        if (basePos != null)
            compound.putLong("basePos", basePos.asLong());
        super.saveAdditional(compound);
    }

    @Nullable
    public StargateAbstractBaseBE<?, ?> getBaseTile(Level world) {
        if (basePos == null) return null;
        if (world == null) return null;
        try {
            return (StargateAbstractBaseBE<?, ?>) world.getBlockEntity(basePos);
        } catch (ClassCastException e) {
            JSG.logger.warn("Error while getting base tile: ", e);
            return null;
        }
    }

    @Override
    public PacketDistributor.TargetPoint getTargetPoint() {
        var pos = getBlockPos();
        return new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 512, getLevel() == null ? Level.OVERWORLD : getLevel().dimension());
    }
}
