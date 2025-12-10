package dev.tauri.jsg.api.multistructure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IMultiStructureBE<T extends IMultiStructure> {
    T getMergeHelper();
    BlockPos getBlockPos();
    Level getLevel();
}
