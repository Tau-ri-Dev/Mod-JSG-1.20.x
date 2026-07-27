package dev.tauri.jsg.common.packet.packets.dialhomedevice;

import dev.tauri.jsg.core.common.packet.packets.PositionedPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class DHDUpgradeAssemblyClickToServer extends PositionedPacket {
    int slot;
    InteractionHand hand;
    boolean disassemble;

    public DHDUpgradeAssemblyClickToServer() {
    }

    public DHDUpgradeAssemblyClickToServer(BlockPos pos, int slot, InteractionHand hand, boolean disassemble) {
        super(pos);
        this.slot = slot;
        this.hand = hand;
    }

    public DHDUpgradeAssemblyClickToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(slot);
        buf.writeInt(hand.ordinal());
        buf.writeBoolean(disassemble);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        slot = buf.readInt();
        hand = InteractionHand.values()[buf.readInt()];
        disassemble = buf.readBoolean();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
        ctx.setPacketHandled(true);
        var player = ctx.getSender();
        if (player == null) return;
        var world = player.level();
        ctx.enqueueWork(() -> {
            // TODO: Logic to assemble and disassemble upgrade crystals
        });
    }
}
