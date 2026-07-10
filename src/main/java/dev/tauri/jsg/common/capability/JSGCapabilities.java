package dev.tauri.jsg.common.capability;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.jub.JUBDevice;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class JSGCapabilities {
    public static final BlockCapability<JUBDevice, @Nullable Direction> JUST_UNIVERSAL_BUS =
            BlockCapability.createSided(JSGMapping.rl(JSG.MOD_ID, "jub_device"), JUBDevice.class);

    @Nullable
    public static JUBDevice getJUB(@Nullable BlockEntity be) {
        if (be == null || be.getLevel() == null) return null;
        return be.getLevel().getCapability(JUST_UNIVERSAL_BUS, be.getBlockPos(), null);
    }
}
