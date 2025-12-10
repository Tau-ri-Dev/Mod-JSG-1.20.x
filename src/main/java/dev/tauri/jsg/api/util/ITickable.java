package dev.tauri.jsg.api.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import org.jetbrains.annotations.NotNull;

public interface ITickable {
    void tick(@NotNull Level level);

    static <T extends BlockEntity> BlockEntityTicker<T> getTickerHelper() {
        return (level0, pos0, state0, be0) -> {
            if (be0 instanceof ITickable tickable) {
                if (be0.getLevel() == null)
                    return;
                tickable.tick(be0.getLevel());
            }
        };
    }

    default void onLoad(@NotNull Level level) {
    }
}
