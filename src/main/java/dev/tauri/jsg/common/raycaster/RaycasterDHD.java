package dev.tauri.jsg.common.raycaster;

import dev.tauri.jsg.api.item.IDHDPartItem;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.packet.packets.stargate.DHDAssemblyClickToServer;
import dev.tauri.jsg.common.packet.packets.stargate.DHDButtonClickedToServer;
import dev.tauri.jsg.common.packet.packets.stargate.DHDFluidInsertionToServer;
import dev.tauri.jsg.core.common.raycaster.Raycaster;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ForgeCapabilities;

public abstract class RaycasterDHD extends Raycaster {

    public static final int CONTROL_CRYSTAL_IMM_BUTTON_ID = 101;
    public static final int BUTTONS_IMM_BUTTON_ID = 100;
    public static final int UPGRADE_SLOT_TL_IMM_BUTTON_ID = 102;
    public static final int UPGRADE_SLOT_TR_IMM_BUTTON_ID = 103;
    public static final int UPGRADE_SLOT_ML_IMM_BUTTON_ID = 104;
    public static final int UPGRADE_SLOT_MR_IMM_BUTTON_ID = 105;
    public static final int UPGRADE_SLOT_B_IMM_BUTTON_ID = 106;
    public static final int FLUID_TANK_IMM_BUTTON_ID = 107;
    public static final int UPGRADE_COVER_IMM_BUTTON_ID = 108;

    private boolean isSneaking = false;

    @Override
    public float getRotation(Level world, BlockPos pos, Player player) {
        return world.getBlockState(pos).getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ROTATION_PROPERTY) * -22.5f;
    }

    @Override
    public boolean onActivated(Level world, BlockPos pos, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return false;
        this.isSneaking = player.isShiftKeyDown();
        return super.onActivated(world, pos, player, hand);
    }

    public abstract IDHDPartItem getDHDButtonsConsolePart();

    @Override
    public boolean isButtonEnabled(Level level, Player player, int buttonId, BlockPos pos, InteractionHand hand) {
        var dhdTile = (DHDAbstractBE) level.getBlockEntity(pos);
        if (dhdTile != null) {
            if (!dhdTile.isAssembled(getDHDButtonsConsolePart()) && buttonId < 100) return false;
            var item = player.getItemInHand(hand);
            var fluidCap = item.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
            if (item.getItem() instanceof IDHDPartItem dhdPart) {
                if (dhdTile.isAssembled(dhdPart))
                    return false;
                if (!dhdTile.getAllParts().contains(dhdPart))
                    return false;
                var neededBefore = dhdPart.getPartsNeededAssembledBeforeAssembly();
                if (!neededBefore.isEmpty() && neededBefore.stream()
                        .filter(p -> dhdTile.getAllParts().contains(p))
                        .anyMatch(part -> !dhdTile.isAssembled(part)))
                    return false;
                var neededRemovedBefore = dhdPart.getPartsNeededRemovedBeforeAssembly();
                if (!neededRemovedBefore.isEmpty() && neededRemovedBefore.stream()
                        .filter(p -> dhdTile.getAllParts().contains(p))
                        .anyMatch(dhdTile::isAssembled))
                    return false;
                return dhdPart.getRaycasterButtonID() == buttonId;
            }
            if (item.is(CoreItems.JSG_SCREWDRIVER.get()) || (fluidCap.isPresent() && buttonId == dhdTile.getFluidTankItemPart().getRaycasterButtonID())) {
                return dhdTile.getAllParts().stream()
                        .filter(dhdTile::isAssembled)
                        .filter(dhdPart -> dhdPart.getPartsNeededToRemoveBeforeRemoval().stream()
                                .filter(p -> dhdTile.getAllParts().contains(p))
                                .noneMatch(dhdTile::isAssembled))
                        .anyMatch(dhdPart -> dhdPart.getRaycasterButtonID() == buttonId);
            }
            return buttonId < 100 || buttonId >= 102;
        }
        return super.isButtonEnabled(level, player, buttonId, pos, hand);
    }

    private static final Vector3f TRANSLATION = new Vector3f(0.5f, 0, 0.5f);

    @Override
    public Vector3f getTranslation(Level world, BlockPos pos) {
        return TRANSLATION;
    }


    @Override
    protected boolean buttonClicked(Level world, Player player, int buttonId, BlockPos pos, InteractionHand hand) {
        if (world.isClientSide) {
            var dhdTile = (DHDAbstractBE) world.getBlockEntity(pos);
            if (dhdTile != null) {
                var item = player.getItemInHand(hand);
                var fluidCap = item.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
                if (item.getItem() instanceof IDHDPartItem part) {
                    if (!dhdTile.getAllParts().contains(part)) return false;
                    if (part.getRaycasterButtonID() == buttonId) {
                        if (part.getPartsNeededAssembledBeforeAssembly().stream()
                                .filter(p -> dhdTile.getAllParts().contains(p))
                                .allMatch(dhdTile::isAssembled)) {
                            if (part.getPartsNeededRemovedBeforeAssembly().stream()
                                    .filter(p -> dhdTile.getAllParts().contains(p))
                                    .noneMatch(dhdTile::isAssembled)) {
                                JSGPacketHandler.sendToServer(new DHDAssemblyClickToServer(pos, part, false));
                                return true;
                            }
                        }
                    }
                }
                if (item.is(CoreItems.JSG_SCREWDRIVER.get()) || (fluidCap.isPresent() && buttonId == dhdTile.getFluidTankItemPart().getRaycasterButtonID())) {
                    var part = dhdTile.getAllParts().stream()
                            .filter(dhdPart -> dhdPart.getRaycasterButtonID() == buttonId)
                            .filter(dhdPart -> dhdPart.getPartsNeededToRemoveBeforeRemoval().stream()
                                    .filter(p -> dhdTile.getAllParts().contains(p))
                                    .noneMatch(dhdTile::isAssembled))
                            .findFirst();
                    if (part.isPresent()) {
                        if (part.get() == dhdTile.getFluidTankItemPart() && fluidCap.isPresent()) {
                            if (!player.isShiftKeyDown()) {
                                JSGPacketHandler.sendToServer(new DHDFluidInsertionToServer(pos, hand));
                                player.swing(hand);
                            }
                            return false; // do not cancel click event
                        }
                        JSGPacketHandler.sendToServer(new DHDAssemblyClickToServer(pos, part.get(), true));
                        return true;
                    }
                }
                if (dhdTile.isAssembled() && buttonId != -1 && buttonId < 100) {
                    SymbolInterface symbol = dhdTile.getSymbolType().valueOf(buttonId);
                    JSGPacketHandler.sendToServer(new DHDButtonClickedToServer(pos, symbol, isSneaking && symbol.brb()));
                    return true;
                }
            }
        }
        return false;
    }
}
