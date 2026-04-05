package dev.tauri.jsg.raycaster.instances;

import dev.tauri.jsg.blockentity.PrinterBE;
import dev.tauri.jsg.core.common.raycaster.Raycaster;
import dev.tauri.jsg.core.common.raycaster.util.RayCastedButton;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import dev.tauri.jsg.packet.JSGPacketHandler;
import dev.tauri.jsg.packet.packets.PrinterButtonClickedToServer;
import dev.tauri.jsg.registry.JSGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RaycasterPrinter extends Raycaster {
    public static final ArrayList<RayCastedButton> BUTTONS = new ArrayList<>() {{
        // CONTROL BUTTONS
        add(new RayCastedButton(0, Arrays.asList(
                new Vector3f(1.85f / 16f - 0.5f, 4.85f / 16f - 0.5f, 10.3f / 16f - 0.5f),
                new Vector3f(4.55f / 16f - 0.5f, 4.85f / 16f - 0.5f, 10.3f / 16f - 0.5f),
                new Vector3f(4.55f / 16f - 0.5f, 3.25f / 16f - 0.5f, 11 / 16f - 0.5f),
                new Vector3f(1.85f / 16f - 0.5f, 3.25f / 16f - 0.5f, 11 / 16f - 0.5f)
        ), true));
        add(new RayCastedButton(1, Arrays.asList(
                new Vector3f(5.1f / 16f - 0.5f, 4.85f / 16f - 0.5f, 10.3f / 16f - 0.5f),
                new Vector3f(7.8f / 16f - 0.5f, 4.85f / 16f - 0.5f, 10.3f / 16f - 0.5f),
                new Vector3f(7.8f / 16f - 0.5f, 3.25f / 16f - 0.5f, 11 / 16f - 0.5f),
                new Vector3f(5.1f / 16f - 0.5f, 3.25f / 16f - 0.5f, 11 / 16f - 0.5f)
        ), true));
        add(new RayCastedButton(2, Arrays.asList(
                new Vector3f(8.35f / 16f - 0.5f, 4.85f / 16f - 0.5f, 10.3f / 16f - 0.5f),
                new Vector3f(11.05f / 16f - 0.5f, 4.85f / 16f - 0.5f, 10.3f / 16f - 0.5f),
                new Vector3f(11.05f / 16f - 0.5f, 3.25f / 16f - 0.5f, 11 / 16f - 0.5f),
                new Vector3f(8.35f / 16f - 0.5f, 3.25f / 16f - 0.5f, 11 / 16f - 0.5f)
        ), true));
        add(new RayCastedButton(3, Arrays.asList(
                new Vector3f(11.6f / 16f - 0.5f, 4.85f / 16f - 0.5f, 10.3f / 16f - 0.5f),
                new Vector3f(14.3f / 16f - 0.5f, 4.85f / 16f - 0.5f, 10.3f / 16f - 0.5f),
                new Vector3f(14.3f / 16f - 0.5f, 3.25f / 16f - 0.5f, 11 / 16f - 0.5f),
                new Vector3f(11.6f / 16f - 0.5f, 3.25f / 16f - 0.5f, 11 / 16f - 0.5f)
        ), true));

        // INPUT
        add(new RayCastedButton(4, Arrays.asList(
                new Vector3f(4.5f / 16f - 0.5f, 8.85f / 16f - 0.5f, 0.6f / 16f - 0.5f),
                new Vector3f(11.4f / 16f - 0.5f, 8.85f / 16f - 0.5f, 0.6f / 16f - 0.5f),
                new Vector3f(11.4f / 16f - 0.5f, 3.45f / 16f - 0.5f, 2.9f / 16f - 0.5f),
                new Vector3f(4.5f / 16f - 0.5f, 3.45f / 16f - 0.5f, 2.9f / 16f - 0.5f)
        ), true));
        // OUTPUT
        add(new RayCastedButton(5, Arrays.asList(
                new Vector3f(3.5f / 16f - 0.5f, 1 / 16f - 0.5f, 15.4f / 16f - 0.5f),
                new Vector3f(3.5f / 16f - 0.5f, 1 / 16f - 0.5f, 11f / 16f - 0.5f),
                new Vector3f(4f / 16f - 0.5f, 1 / 16f - 0.5f, 11f / 16f - 0.5f),
                new Vector3f(4f / 16f - 0.5f, 1 / 16f - 0.5f, 9f / 16f - 0.5f),
                new Vector3f(11.9f / 16f - 0.5f, 1 / 16f - 0.5f, 9f / 16f - 0.5f),
                new Vector3f(11.9f / 16f - 0.5f, 1 / 16f - 0.5f, 11f / 16f - 0.5f),
                new Vector3f(12.4f / 16f - 0.5f, 1 / 16f - 0.5f, 11f / 16f - 0.5f),
                new Vector3f(12.4f / 16f - 0.5f, 1 / 16f - 0.5f, 15.4f / 16f - 0.5f)
        ), true));
    }};

    @Override
    public boolean testBlockState(BlockState blockState) {
        return blockState.is(JSGBlocks.PRINTER.get());
    }

    @Override
    protected List<RayCastedButton> getButtons() {
        return BUTTONS;
    }

    @Override
    public float getRotation(Level world, BlockPos pos, Player player) {
        return -world.getBlockState(pos).getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY).getOpposite().toYRot();
    }

    @Override
    public Vector3f getTranslation(Level world, BlockPos pos) {
        return new Vector3f(0.5f, 0.5f, 0.5f);
    }

    @Override
    protected boolean buttonClicked(Level world, Player player, int button, BlockPos pos, InteractionHand hand) {
        if (button != -1 && hand == InteractionHand.MAIN_HAND) {
            player.swing(hand);
            if (world.isClientSide) {
                var tile = (PrinterBE) world.getBlockEntity(pos);
                if (tile != null) {
                    JSGPacketHandler.sendToServer(new PrinterButtonClickedToServer(pos, button));
                    return true;
                }
            }
        }
        return false;
    }
}
