package dev.tauri.jsg.common.packet.packets.stargate;

import dev.tauri.jsg.api.item.IDHDPartItem;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.core.common.packet.packets.PositionedPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import dev.tauri.jsg.core.common.packet.NetworkDirection;
import dev.tauri.jsg.core.common.packet.PacketContext;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class DHDAssemblyClickToServer extends PositionedPacket {
    IDHDPartItem part;
    boolean disassemble;

    public DHDAssemblyClickToServer() {
    }

    @ParametersAreNonnullByDefault
    public DHDAssemblyClickToServer(BlockPos pos, IDHDPartItem part, boolean disassemble) {
        super(pos);
        this.part = part;
        this.disassemble = disassemble;
    }

    public DHDAssemblyClickToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeResourceLocation(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(part.self())));
        buf.writeBoolean(disassemble);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        part = (IDHDPartItem) BuiltInRegistries.ITEM.get(buf.readResourceLocation());
        disassemble = buf.readBoolean();
    }

    @Override
    public void handle(PacketContext ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
        ctx.setPacketHandled(true);
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        Level world = player.level();
        ctx.enqueueWork(() -> {
            DHDAbstractBE dhdTile = (DHDAbstractBE) world.getBlockEntity(pos);
            if (dhdTile == null) return;
            var itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            player.swing(InteractionHand.MAIN_HAND);
            dhdTile.handleAssembleRequestFromClient(part, disassemble, player, itemStack);
        });
    }
}
