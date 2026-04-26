package dev.tauri.jsg.common.block.dialhomedevice;

import dev.tauri.jsg.common.item.stargate.dialhomedevice.DHDMilkyWayItem;
import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.core.common.item.JSGBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class DHDMilkyWayBlock extends DHDAbstractBlock {
    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return JSGBlockEntities.DHD_MILKYWAY.get().create(pos, state);
    }

    @Override
    public JSGBlockItem getItemBlock() {
        return new DHDMilkyWayItem(this);
    }
}
