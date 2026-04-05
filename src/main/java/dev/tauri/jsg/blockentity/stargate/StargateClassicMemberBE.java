package dev.tauri.jsg.blockentity.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class StargateClassicMemberBE extends StargateAbstractMemberBE {
    public StargateClassicMemberBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }
}
