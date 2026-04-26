package dev.tauri.jsg.common.item.linkable.dialer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

public interface IUniverseDialerUseHandler {
    @ParametersAreNonnullByDefault
    boolean use(CompoundTag compoundTag, Level world, Player player, InteractionHand hand);
}
