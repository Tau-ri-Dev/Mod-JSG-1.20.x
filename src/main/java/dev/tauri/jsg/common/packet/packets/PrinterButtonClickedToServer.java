package dev.tauri.jsg.common.packet.packets;

import dev.tauri.jsg.common.blockentity.PrinterBE;
import dev.tauri.jsg.core.common.packet.packets.PositionedPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class PrinterButtonClickedToServer extends PositionedPacket {
    public int button;

    public PrinterButtonClickedToServer(BlockPos pos, int button) {
        super(pos);
        this.button = button;
    }

    public PrinterButtonClickedToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(button);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        button = buf.readInt();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
        ctx.setPacketHandled(true);
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        Level world = player.level();
        ctx.enqueueWork(() -> {
            var tile = (PrinterBE) world.getBlockEntity(pos);
            if (tile == null) return;
            var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (button == 4) {
                // input
                var toGive = tile.insertPage(stack, false, player.isCrouching());
                if (toGive != null && !toGive.isEmpty())
                    player.addItem(toGive);
                return;
            }
            if (button == 5) {
                // output
                var toGive = tile.takePage(false);
                if (toGive != null && !toGive.isEmpty())
                    player.addItem(toGive);
                return;
            }
            tile.buttonClick(button, player);
        });
    }
}
