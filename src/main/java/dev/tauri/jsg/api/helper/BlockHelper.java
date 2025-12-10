package dev.tauri.jsg.api.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BlockHelper {
    public static boolean isBlockDirectlyUnderSky(Level world, BlockPos pos) {
        int blocksCount = 0;
        int maxBlocks = 3;
        while (pos.getY() < 255) {
            pos = pos.above();

            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (!world.getBlockState(pos).isAir() && !(block instanceof LeavesBlock) && !state.canBeReplaced())
                blocksCount++;

            if (blocksCount > maxBlocks) return false;
        }

        return true;
    }

    /**
     * Returns {@link BlockPos} with largest Y-coord value.
     *
     * @param list List of positions.
     * @return largest Y-coord {@link BlockPos}. {@code null} if list empty.
     */
    public static BlockPos getHighest(List<BlockPos> list) {
        int maxy = Integer.MIN_VALUE;
        BlockPos top = null;

        for (BlockPos pos : list) {
            if (pos.getY() > maxy) {
                maxy = pos.getY();
                top = pos.immutable();
            }
        }

        return top;
    }

    public static BlockPos getHighestWithXZCords(List<BlockPos> list, int x, int z) {
        int maxy = Integer.MIN_VALUE;
        BlockPos top = null;

        for (BlockPos pos : list) {
            if (pos.getY() > maxy && pos.getX() == x & pos.getZ() == z) {
                maxy = pos.getY();
                top = pos.immutable();
            }
        }

        return top;
    }

    public static String blockPosToBetterString(BlockPos pos) {
        if (pos == null) return "null";
        return pos.getX() + " " + pos.getY() + " " + pos.getZ();
    }
}
