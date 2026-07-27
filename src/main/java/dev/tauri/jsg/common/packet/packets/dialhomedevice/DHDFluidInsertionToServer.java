package dev.tauri.jsg.common.packet.packets.dialhomedevice;

import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.core.common.packet.packets.PositionedPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class DHDFluidInsertionToServer extends PositionedPacket {
    InteractionHand hand;

    public DHDFluidInsertionToServer() {
    }

    public DHDFluidInsertionToServer(BlockPos pos, InteractionHand hand) {
        super(pos);
        this.hand = hand;
    }

    public DHDFluidInsertionToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(hand.ordinal());
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        hand = InteractionHand.values()[buf.readInt()];
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
        ctx.setPacketHandled(true);
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        Level level = player.level();
        ctx.enqueueWork(() -> {
            DHDAbstractBE dhdTile = (DHDAbstractBE) level.getBlockEntity(pos);
            if (dhdTile == null) return;
            if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, null)) {
                player.swing(InteractionHand.MAIN_HAND);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1, 1);
            }
        });
    }
}
