package dev.tauri.jsg.block.stargate;

import dev.tauri.jsg.registry.JSGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public class StargatePegasusBaseBlock extends StargateClassicBaseBlock {
    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return JSGBlockEntities.STARGATE_PEGASUS_BASE_BE.get().create(pos, state);
    }
}
