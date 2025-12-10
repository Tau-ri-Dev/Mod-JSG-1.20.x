package dev.tauri.jsg.api.block.util;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public interface WrenchRotatable {
    void onWrenchUse(BlockState state, UseOnContext context);
}
