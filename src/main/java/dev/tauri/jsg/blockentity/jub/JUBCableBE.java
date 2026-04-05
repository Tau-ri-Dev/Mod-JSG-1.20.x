package dev.tauri.jsg.blockentity.jub;

import dev.tauri.jsg.capability.JSGCapabilities;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.jub.JUBDevice;
import dev.tauri.jsg.registry.JSGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == JSGCapabilities.JUST_UNIVERSAL_BUS) {
            return LazyOptional.of(() -> jubDevice).cast();
        }
        return super.getCapability(cap);
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
