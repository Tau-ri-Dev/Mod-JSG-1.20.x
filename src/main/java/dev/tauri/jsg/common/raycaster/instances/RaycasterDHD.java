package dev.tauri.jsg.common.raycaster.instances;

import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.packet.packets.stargate.DHDButtonClickedToServer;
import dev.tauri.jsg.core.common.raycaster.Raycaster;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class RaycasterDHD extends Raycaster {
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

    private static final Vector3f TRANSLATION = new Vector3f(0.5f, 0, 0.5f);

    @Override
    public Vector3f getTranslation(Level world, BlockPos pos) {
        return TRANSLATION;
    }


    @Override
    protected boolean buttonClicked(Level world, Player player, int button, BlockPos pos, InteractionHand hand) {
        if (button != -1 && button < 100) {
            if (world.isClientSide) {
                var dhdTile = (DHDAbstractBE) world.getBlockEntity(pos);
                if (dhdTile != null) {
                    SymbolInterface symbol = dhdTile.getSymbolType().valueOf(button);
                    JSGPacketHandler.sendToServer(new DHDButtonClickedToServer(pos, symbol, isSneaking && symbol.brb()));
                    return true;
                }
            }
        }
        return false;
    }
}
