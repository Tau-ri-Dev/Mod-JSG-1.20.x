package dev.tauri.jsg.common.packet.packets;

import dev.tauri.jsg.common.blockentity.energy.ZPMHubBE;
import dev.tauri.jsg.core.common.packet.NetworkDirection;
import dev.tauri.jsg.core.common.packet.PacketContext;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.PositionedPacket;
import dev.tauri.jsg.core.common.packet.packets.StateUpdatePacketToClient;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class ZPMHubAnimationToServer extends PositionedPacket {
    public ZPMHubAnimationToServer(BlockPos pos) {
        super(pos);
    }

    public ZPMHubAnimationToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(PacketContext ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
        ctx.setPacketHandled(true);
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        Level world = player.level();
        ctx.enqueueWork(() -> {
            if (world.getBlockEntity(pos) instanceof ZPMHubBE hub) {
                hub.startAnimation();
                JSGCorePacketHandler.sendTo(new StateUpdatePacketToClient(pos, CoreStateTypes.RENDERER_UPDATE, hub.getState(CoreStateTypes.RENDERER_UPDATE.get())), player);
            }
        });
    }
}
