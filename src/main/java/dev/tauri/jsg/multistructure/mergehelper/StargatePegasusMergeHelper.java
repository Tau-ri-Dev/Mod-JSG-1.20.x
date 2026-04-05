package dev.tauri.jsg.multistructure.mergehelper;

import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.registry.JSGBlocks;
import net.minecraft.world.level.block.Block;

public class StargatePegasusMergeHelper extends StargateClassicMergeHelper {

    public StargatePegasusMergeHelper(StargateAbstractBaseBE tileEntity) {
        super(tileEntity);
    }

    @Override
    public Block getRingBlock() {
        return JSGBlocks.STARGATE_PEGASUS_RING_BLOCK.get();
    }

    @Override
    public Block getChevronBlock() {
        return JSGBlocks.STARGATE_PEGASUS_CHEVRON_BLOCK.get();
    }
}
