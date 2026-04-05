package dev.tauri.jsg.packet.packets.stargate;

import dev.tauri.jsg.core.common.packet.packets.PositionedPacket;
import dev.tauri.jsg.packet.JSGPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class StargatePlayerMotionRequestToClient extends PositionedPacket {

    private boolean wrongWayTravel;

    public StargatePlayerMotionRequestToClient(BlockPos gatePos, boolean wrongWayTravel) {
        super(gatePos);
        this.wrongWayTravel = wrongWayTravel;
    }

    public StargatePlayerMotionRequestToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeBoolean(wrongWayTravel);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        wrongWayTravel = buf.readBoolean();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        var player = Minecraft.getInstance().player;
        ctx.enqueueWork(() -> {
            if (player != null) {
                JSGPacketHandler.sendToServer(new StargatePlayerMotionToServer(player.getId(), pos, player.getDeltaMovement(), wrongWayTravel));
            }
        });
    }
}
