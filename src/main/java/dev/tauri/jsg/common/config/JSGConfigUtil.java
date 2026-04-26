package dev.tauri.jsg.common.config;

import dev.tauri.jsg.common.block.dialhomedevice.DHDAbstractBlock;
import dev.tauri.jsg.common.block.stargate.StargateAbstractBaseBlock;
import dev.tauri.jsg.common.block.stargate.StargateAbstractMemberBlock;
import dev.tauri.jsg.common.block.stargate.redstone.AbstractStargateRedstoneIO;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.common.registry.tags.JSGBlockTags;
import dev.tauri.jsg.core.common.block.capacitor.CapacitorBlock;
import dev.tauri.jsg.core.common.block.cartouche.CartoucheBlock;
import dev.tauri.jsg.core.common.block.core.InvisibleBlock;
import dev.tauri.jsg.core.common.registry.tag.CoreBlockTags;
import net.minecraft.world.level.block.state.BlockState;

public class JSGConfigUtil {

    public static boolean canBlackHoleDestroyBlock(BlockState state) {
        if (state == null) return false;
        if (state.getBlock() instanceof InvisibleBlock) return false;
        if (state.getBlock() instanceof StargateAbstractBaseBlock) return false;
        if (state.getBlock() instanceof StargateAbstractMemberBlock) return false;

        return !state.is(JSGBlockTags.BLACK_HOLE_INVINCIBLE);
    }

    public static boolean canKawooshDestroyBlock(BlockState state) {
        if (state == null) return false;
        if (state.getBlock() == JSGBlocks.IRIS_BLOCK.get()) return false;
        if (state.getBlock() instanceof InvisibleBlock) return false;
        if (state.getBlock() instanceof DHDAbstractBlock) return false;
        if (state.getBlock() instanceof StargateAbstractBaseBlock) return false;
        if (state.getBlock() instanceof StargateAbstractMemberBlock) return false;

        return !state.is(JSGBlockTags.KAWOOSH_INVINCIBLE);
    }

    public static boolean canBeUsedAsCamoBlock(BlockState state) {
        if (state == null) return false;
        if (state.getBlock() == JSGBlocks.IRIS_BLOCK.get()) return false;
        if (state.getBlock() instanceof InvisibleBlock) return false;
        if (state.getBlock() instanceof StargateAbstractBaseBlock) return false;
        if (state.getBlock() instanceof StargateAbstractMemberBlock) return false;
        if (state.getBlock() instanceof CapacitorBlock) return false;
        if (state.getBlock() instanceof AbstractStargateRedstoneIO) return false;
        if (state.getBlock() instanceof CartoucheBlock) return false;

        return !state.is(CoreBlockTags.CAMO_BLACKLISTED);
    }
}
