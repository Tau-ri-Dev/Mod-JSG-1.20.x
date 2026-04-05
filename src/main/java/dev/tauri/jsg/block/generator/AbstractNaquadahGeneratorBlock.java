package dev.tauri.jsg.block.generator;

import dev.tauri.jsg.core.common.block.TickableBEBlock;
import dev.tauri.jsg.core.common.block.util.IHighlightBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class AbstractNaquadahGeneratorBlock extends TickableBEBlock implements IHighlightBlock {
    public AbstractNaquadahGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean renderHighlight(BlockState state) {
        return false;
    }
}
