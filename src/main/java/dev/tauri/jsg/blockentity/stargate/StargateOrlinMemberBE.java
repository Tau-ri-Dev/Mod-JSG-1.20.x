package dev.tauri.jsg.blockentity.stargate;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.registry.JSGBlockEntities;
import dev.tauri.jsg.registry.JSGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;


public class StargateOrlinMemberBE extends StargateAbstractMemberBE {
    public StargateOrlinMemberBE(BlockPos pos, BlockState state) {
        super(JSGBlockEntities.STARGATE_ORLIN_MEMBER_BE.get(), pos, state);
    }

    @Override
    public Block getBaseBlock() {
        return JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get();
    }

    private int openCount = 0;

    public boolean isBroken() {
        return openCount >= JSGConfig.Stargate.stargateOrlinMaxOpenCount.get();
    }

    public void incrementOpenCount() {
        if (level == null || level.isClientSide()) return;
        openCount++;
        if (isBroken()) {
            level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()).setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ORLIN_BROKEN, true), 3);
        }
        setChanged();
    }

    public int getOpenCount() {
        return openCount;
    }

    public void initializeFromItemStack(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag compound = stack.getTag();
            if (compound != null && compound.contains("openCount")) {
                openCount = compound.getInt("openCount");
            }
        }
    }

    // ---------------------------------------------------------------------------------
    // NBT

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        compound.putInt("openCount", openCount);

        super.saveAdditional(compound);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        openCount = compound.getInt("openCount");

        super.load(compound);
    }
}
