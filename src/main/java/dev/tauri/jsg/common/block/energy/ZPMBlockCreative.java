package dev.tauri.jsg.common.block.energy;

import dev.tauri.jsg.common.blockentity.energy.ZPMCreativeBE;
import dev.tauri.jsg.common.item.energy.ZPMItemBlock;
import dev.tauri.jsg.core.common.item.JSGBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public class ZPMBlockCreative extends ZPMBlock {
    public ZPMBlockCreative() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_ORANGE)
                .sound(SoundType.GLASS)
                .strength(-1.0f, 3600000.0f)
                .noLootTable()
                .noOcclusion());
    }

    @Override
    public JSGBlockItem getItemBlock() {
        return new ZPMItemBlock(this, true);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ZPMCreativeBE(pos, state);
    }
}
