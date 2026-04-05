package dev.tauri.jsg.block.dialhomedevice;

import dev.tauri.jsg.core.common.item.JSGBlockItem;
import dev.tauri.jsg.item.stargate.dialhomedevice.DHDMilkyWayItem;
import dev.tauri.jsg.registry.JSGBlockEntities;
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
