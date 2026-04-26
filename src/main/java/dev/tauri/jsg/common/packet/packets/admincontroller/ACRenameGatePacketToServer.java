package dev.tauri.jsg.common.packet.packets.admincontroller;

import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

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
            buf.writeInt(name.length());
            buf.writeCharSequence(name, StandardCharsets.UTF_8);
        } else buf.writeBoolean(false);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        if (buf.readBoolean()) {
            var size = buf.readInt();
            name = buf.readCharSequence(size, StandardCharsets.UTF_8).toString();
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
