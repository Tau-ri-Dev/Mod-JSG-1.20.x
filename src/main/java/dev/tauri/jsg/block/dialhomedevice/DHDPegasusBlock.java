package dev.tauri.jsg.block.dialhomedevice;

import dev.tauri.jsg.core.common.item.JSGBlockItem;
import dev.tauri.jsg.item.stargate.dialhomedevice.DHDPegasusItem;
import dev.tauri.jsg.registry.JSGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class DHDPegasusBlock extends DHDAbstractBlock {
    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return JSGBlockEntities.DHD_PEGASUS.get().create(pos, state);
    }

    @Override
    public JSGBlockItem getItemBlock() {
        return new DHDPegasusItem(this);
    }
}
