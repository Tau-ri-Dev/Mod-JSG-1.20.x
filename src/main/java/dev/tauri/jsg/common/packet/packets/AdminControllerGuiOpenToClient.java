package dev.tauri.jsg.common.packet.packets;

import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.core.common.packet.packets.JSGPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class AdminControllerGuiOpenToClient extends JSGPacket {
    public AdminControllerGuiOpenToClient() {
    }

    protected StargateNetwork network;
    protected BlockPos gatePos;
    protected boolean displayGui;

    public AdminControllerGuiOpenToClient(BlockPos gatePos, StargateNetwork network) {
        this.gatePos = gatePos;
        this.network = network;
        this.displayGui = true;
    }

    public AdminControllerGuiOpenToClient(StargateNetwork network) {
        this.network = network;
        this.displayGui = true;
    }

    public AdminControllerGuiOpenToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        if (gatePos != null) {
            buf.writeBoolean(true);
            buf.writeBlockPos(gatePos);
        } else buf.writeBoolean(false);
        if (network != null) {
            buf.writeBoolean(true);
            network.toBytes(buf);
        } else buf.writeBoolean(false);
        buf.writeBoolean(displayGui);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        if (buf.readBoolean())
            gatePos = buf.readBlockPos();
        if (buf.readBoolean()) {
            network = new StargateNetwork();
            network.fromBytes(buf);
        }
        displayGui = buf.readBoolean();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            if (displayGui) {
                AdminControllerGuiOpenHandler.openGui(gatePos, network);
            }
        });
    }
}
