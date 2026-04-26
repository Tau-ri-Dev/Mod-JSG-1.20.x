package dev.tauri.jsg.common.packet.packets.stargate;

import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.PositionedPacket;
import dev.tauri.jsg.core.common.packet.packets.StateUpdatePacketToClient;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.StandardCharsets;

public class SaveIrisCodeToServer extends PositionedPacket {
    String code;
    EnumIrisMode mode;

    public SaveIrisCodeToServer(BlockPos pos, String code, EnumIrisMode mode) {
        super(pos);

        this.code = code;
        this.mode = mode;
    }

    public SaveIrisCodeToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);

        buf.writeInt(code.length());
        buf.writeCharSequence(code, StandardCharsets.UTF_8);
        buf.writeByte(mode.id);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);

        int codeSize = buf.readInt();
        code = buf.readCharSequence(codeSize, StandardCharsets.UTF_8).toString();
        mode = EnumIrisMode.getValue(buf.readByte());
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
        ctx.setPacketHandled(true);
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        Level world = player.level();

        ctx.enqueueWork(() -> {
            if (world.getBlockEntity(pos) instanceof StargateClassicBaseBE<?> te) {
                te.getIrisManager().setIrisCode(code);
                te.getIrisManager().setIrisMode(mode);
                JSGCorePacketHandler.sendTo(new StateUpdatePacketToClient(pos, CoreStateTypes.GUI_STATE, te.getState(CoreStateTypes.GUI_STATE.get())), player);
            }
        });
    }
}
