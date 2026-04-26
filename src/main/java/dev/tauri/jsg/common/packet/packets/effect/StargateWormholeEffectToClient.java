package dev.tauri.jsg.common.packet.packets.effect;

import dev.tauri.jsg.common.effect.StargateWormholeEffect;
import dev.tauri.jsg.common.effect.StargateWormholeType;
import dev.tauri.jsg.core.common.packet.packets.JSGPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class StargateWormholeEffectToClient extends JSGPacket {

    public StargateWormholeEffectToClient() {
    }

    // Int, because Java is retarded!
    private int stop;
    private int length;
    private StargateWormholeType type;

    public StargateWormholeEffectToClient(boolean stop, StargateWormholeType type, int length) {
        this.stop = stop ? 10 : 5;
        this.type = type;
        this.length = length;
    }

    public StargateWormholeEffectToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(stop);
        buf.writeInt(type.ordinal());
        buf.writeInt(length);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        stop = buf.readInt();
        type = StargateWormholeType.values()[buf.readInt()];
        length = buf.readInt();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(NetworkEvent.Context ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
        ctx.enqueueWork(() -> {
            ctx.setPacketHandled(true);
            if (stop == 10) {
                StargateWormholeEffect.stop();
            } else if (stop == 5) {
                StargateWormholeEffect.play(type, length + 20);
            }
        });
    }
}
