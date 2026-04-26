package dev.tauri.jsg.common.multistructure.mergehelper;

import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.registry.JSGBlocks;
import net.minecraft.world.level.block.Block;

public class StargateUniverseMergeHelper extends StargateClassicMergeHelper {

    public StargateUniverseMergeHelper(StargateAbstractBaseBE tileEntity) {
        super(tileEntity);
    }

    @Override
    public Block getRingBlock() {
        return JSGBlocks.STARGATE_UNIVERSE_RING_BLOCK.get();
    }

    @Override
    public Block getChevronBlock() {
        return JSGBlocks.STARGATE_UNIVERSE_CHEVRON_BLOCK.get();
    }
}
