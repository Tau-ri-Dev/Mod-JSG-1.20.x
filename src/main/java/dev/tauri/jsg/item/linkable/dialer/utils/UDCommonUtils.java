package dev.tauri.jsg.item.linkable.dialer.utils;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.core.common.helper.LinkingHelper;
import dev.tauri.jsg.item.linkable.dialer.UniverseDialerMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class UDCommonUtils {
    public static BlockPos getNearestLinkable(Level world, BlockPos pos, ArrayList<BlockPos> blacklist, UniverseDialerMode mode) {
        return LinkingHelper.findClosestPos(world, pos, new BlockPos(JSGConfig.DialHomeDevice.universeDialerReach.get(), 40, JSGConfig.DialHomeDevice.universeDialerReach.get()), mode.matchBlocks, blacklist);
    }
}
