package dev.tauri.jsg.common.blockentity.jub;

import dev.tauri.jsg.common.capability.JSGCapabilities;
import dev.tauri.jsg.common.jub.JUBDevice;
import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class JUBCableBE extends BlockEntity implements ITickable {
    public JUBCableBE(BlockPos pPos, BlockState pBlockState) {
        super(JSGBlockEntities.JUB_CABLE.get(), pPos, pBlockState);
    }

    @Override
    public void tick(@NotNull Level level) {

    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    // ----------------------------------------------
    // JUB
    public JUBDevice jubDevice = new JUBDevice(this) {
        @Override
        public void onChanged() {
            setChanged();
        }

        @Override
        protected void packetReceived(String name, Object data, JUBDevice sender) {
            //JSG.logger.info("Got packet {}!", name);
        }
    };
}
