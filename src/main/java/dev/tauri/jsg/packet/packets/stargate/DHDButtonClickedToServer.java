package dev.tauri.jsg.packet.packets.stargate;

import dev.tauri.jsg.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.core.common.packet.packets.PositionedPacket;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class DHDButtonClickedToServer extends PositionedPacket {
    public DHDButtonClickedToServer() {
    }

    public SymbolInterface symbol;
    public SymbolType<?> symbolType;
    public boolean force;

    public DHDButtonClickedToServer(BlockPos pos, SymbolInterface symbol) {
        super(pos);
        this.symbol = symbol;
        this.symbolType = symbol.getSymbolType();
    }

    public DHDButtonClickedToServer(BlockPos pos, SymbolInterface symbol, boolean force) {
        this(pos, symbol);
        this.force = force;
    }

    public DHDButtonClickedToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);

        buf.writeResourceLocation(symbolType.getId());
        buf.writeInt(symbol.getId());
        buf.writeBoolean(force);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);

        symbolType = SymbolType.byId(buf.readResourceLocation());
        symbol = symbolType.valueOf(buf.readInt());
        force = buf.readBoolean();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
        ctx.setPacketHandled(true);
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        Level world = player.level();
        ctx.enqueueWork(() -> {
            if (!symbol.canBePressed()) {
                player.sendSystemMessage(Component.translatable("block.jsg.dhd_pegasus.unknown_buttons"), true);
                return;
            }
            DHDAbstractBE dhdTile = (DHDAbstractBE) world.getBlockEntity(pos);
            if (dhdTile == null) return;
            player.swing(InteractionHand.MAIN_HAND);
            dhdTile.pushSymbolButton(symbol, player, force);
        });
    }
}
