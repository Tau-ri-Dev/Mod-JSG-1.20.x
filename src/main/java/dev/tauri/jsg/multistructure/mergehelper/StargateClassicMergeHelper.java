package dev.tauri.jsg.multistructure.mergehelper;

import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class StargateClassicMergeHelper extends StargateAbstractMergeHelper {

    public static final List<BlockPos> RINGS_POSITIONS = Arrays.asList(
            new BlockPos(2, 1, 0),
            new BlockPos(3, 3, 0),
            new BlockPos(3, 5, 0),
            new BlockPos(1, 7, 0),
            new BlockPos(-1, 7, 0),
            new BlockPos(-3, 5, 0),
            new BlockPos(-3, 3, 0),
            new BlockPos(-2, 1, 0)
    );

    public static final List<BlockPos> CHEVRONS_POSITIONS = Arrays.asList(
            new BlockPos(1, 0, 0),
            new BlockPos(3, 2, 0),
            new BlockPos(3, 4, 0),
            new BlockPos(2, 6, 0),
            new BlockPos(0, 7, 0), // top chevron
            new BlockPos(-2, 6, 0),
            new BlockPos(-3, 4, 0),
            new BlockPos(-3, 2, 0),
            new BlockPos(-1, 0, 0)
    );

    public StargateClassicMergeHelper(StargateAbstractBaseBE<?, ?> tileEntity) {
        super(tileEntity);
    }

    public List<BlockPos> getChevrons() {
        return CHEVRONS_POSITIONS;
    }

    @Override
    public List<BlockPos> getRings() {
        return RINGS_POSITIONS;
    }

    public abstract Block getChevronBlock();

    @Override
    public Map<BlockPos, BlockState> getBlocks(boolean offsetAndRotate) {
        Map<BlockPos, BlockState> map = super.getBlocks(offsetAndRotate);
        for (BlockPos pos : getChevrons()) {
            BlockPos newPos = pos;
            if (offsetAndRotate)
                newPos = stargate.relative(pos); //BlockPosHelper.rotate(pos, horizontalFacing, verticalFacing).offset(basePos);
            map.put(newPos, getChevronBlock().defaultBlockState()
                    .setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, horizontalFacing)
                    .setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY, dev.tauri.jsg.core.common.blockstate.JSGProperties.getVerticalFacingByDirection(verticalFacing))
                    .setValue(BlockStateProperties.WATERLOGGED, offsetAndRotate && Objects.requireNonNull(stargate.getLevel()).getFluidState(newPos).getType() == Fluids.WATER)
            );
        }
        return map;
    }

    @Nonnull
    public Block getMatchBlock(boolean chevron) {
        if (chevron) return getChevronBlock();
        return super.getMatchBlock(false);
    }
}
