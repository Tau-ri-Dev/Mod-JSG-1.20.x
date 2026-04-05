package dev.tauri.jsg.helpers;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.dialhomedevice.StargateDHD;
import dev.tauri.jsg.core.common.blockentity.ILinkableBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class StargateLinkingHelper {

    /**
     * Returns proper DHD range.
     *
     * @return DHD range.
     */
    public static BlockPos getDhdRange() {
        int xz = JSGConfig.DialHomeDevice.rangeFlat.get();
        int y = JSGConfig.DialHomeDevice.rangeVertical.get();

        return new BlockPos(xz, y, xz);
    }

    public static void updateLinkedGate(Level world, BlockPos gatePos, BlockPos dhdPos) {
        var gateTile = (Stargate<?>) world.getBlockEntity(gatePos);
        var dhdTile = world.getBlockEntity(dhdPos);

        if (dhdTile instanceof StargateDHD dhd && gateTile instanceof ILinkableBE<?>) {
            dhd.setLinkedDevice(gatePos);
            ((ILinkableBE<?>) gateTile).setLinkedDevice(dhdPos);
        }
    }
}
