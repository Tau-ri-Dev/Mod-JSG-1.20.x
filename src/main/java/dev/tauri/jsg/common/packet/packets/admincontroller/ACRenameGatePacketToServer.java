package dev.tauri.jsg.common.packet.packets.admincontroller;

import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ACRenameGatePacketToServer extends ACPacketToServer {
    @Nullable
    String name;

    public ACRenameGatePacketToServer(@NotNull StargatePos gatePos, @Nullable String name) {
        super(gatePos);
        this.name = name;
    }

    public ACRenameGatePacketToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        if (name != null) {
            buf.writeBoolean(true);
            buf.writeUtf(name);
        } else buf.writeBoolean(false);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        if (buf.readBoolean()) {
            name = buf.readUtf();
        }
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            var sgnEntry = StargateNetwork.INSTANCE.getAll().get(gatePos);
            if (sgnEntry == null) return;
            var stargate = gatePos.getStargate();
            stargate.renameStargatePos(name);
        });
    }
}
