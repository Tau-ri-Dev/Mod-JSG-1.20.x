package dev.tauri.jsg.common.item.linkable.dialer.modes;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.client.renderer.item.dialer.IUniverseDialerScreen;
import dev.tauri.jsg.client.renderer.item.dialer.screen.UDStatusScreen;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerMode;
import dev.tauri.jsg.common.registry.tags.JSGBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class UDStatusMode extends UniverseDialerMode {
    public static final String C_STATUS = "gateStatus";
    public static final String C_OPEN_TIME = "gateOpenTime";
    public static final String C_IRIS = "gateIrisState";
    public static final String C_SYMBOL = "gateLastSymbol";

    public UDStatusMode() {
        super(JSG.rl("status"), "item.jsg.universe_dialer.mode_info", JSGBlockTags.CLASSIC_STARGATE_BASES, (level, pos) -> (level.getBlockEntity(pos) instanceof StargateClassicBaseBE<?> gate && gate.isMerged()));
    }

    private final UDStatusScreen screen = new UDStatusScreen();

    @Override
    public @NotNull IUniverseDialerScreen getScreen() {
        return screen;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void inventoryTick(ItemStack stack, CompoundTag compound, Level world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, compound, world, entity, itemSlot, isSelected);

        if (!compound.contains(C_LINKED_POS)) return;
        var linkedPos = BlockPos.of(compound.getLong(C_LINKED_POS));
        var gateTile = (StargateClassicBaseBE<?>) world.getBlockEntity(linkedPos);
        if (gateTile == null) return;

        compound.putInt(C_STATUS, gateTile.getDialingManager().getStargateState().ordinal());
        compound.putString(C_OPEN_TIME, gateTile.getDialingManager().getConnection().getSecondsOpen() > 0 ? gateTile.getOpenedSecondsToDisplayAsMinutes() : "CLOSED");
        compound.putString(C_IRIS, gateTile.getIrisManager().hasIris() ? gateTile.getIrisManager().getIrisState().toString() : "MISSING");
        compound.putString(C_SYMBOL, (gateTile.getDialingManager().getDialedAddressSize() > 0) ? gateTile.getDialingManager().getDialedAddress().get(gateTile.getDialingManager().getDialedAddress().size() - 1).toString() + " (" + gateTile.getDialingManager().getDialedAddress().size() + ")" : "-- (0)");

        if (gateTile.getDialingManager().getStargateState().notInitiating())
            compound.putString(C_SYMBOL, "INCOMING");
    }
}
