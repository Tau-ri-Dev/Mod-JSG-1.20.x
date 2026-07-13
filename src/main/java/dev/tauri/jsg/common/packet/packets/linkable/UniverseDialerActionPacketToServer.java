package dev.tauri.jsg.common.packet.packets.linkable;

import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerClientActionEnum;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerItem;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerMode;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.core.common.packet.packets.JSGPacket;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import dev.tauri.jsg.core.common.packet.PacketContext;

public class UniverseDialerActionPacketToServer extends JSGPacket {

    public UniverseDialerClientActionEnum action;
    public InteractionHand hand;
    public boolean next;

    public UniverseDialerActionPacketToServer(UniverseDialerClientActionEnum action, InteractionHand hand, boolean next) {
        this.action = action;
        this.hand = hand;
        this.next = next;
    }

    public UniverseDialerActionPacketToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(action.ordinal());
        buf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
        buf.writeBoolean(next);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        action = UniverseDialerClientActionEnum.values()[buf.readInt()];
        hand = buf.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        next = buf.readBoolean();
    }

    @Override
    public void handle(PacketContext ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            var player = ctx.getSender();
            if (player == null) return;
            var stack = player.getItemInHand(hand);
            if (stack.getItem() != JSGItems.UNIVERSE_DIALER.get()) return;
            if (!ItemNBT.hasTag(stack)) return;

            if (stack.getItem() == JSGItems.UNIVERSE_DIALER.get() && ItemNBT.hasTag(stack)) {
                var compound = ItemNBT.getTag(stack);
                if (compound != null) {
                    var mode = UniverseDialerMode.valueOf(JSGMapping.rl(compound.getString(UniverseDialerItem.C_MODE))).orElse(UniverseDialerMode.getDefault());
                    // ItemNBT tags are copies of the custom_data component - mutations must be written back
                    var modeTag = mode.getTag(compound);

                    if (action == UniverseDialerClientActionEnum.MODE_CHANGE) {
                        if (next) // message.offset < 0
                            mode = mode.next();
                        else
                            mode = mode.prev();

                        compound.putString(UniverseDialerItem.C_MODE, mode.id.toString());
                        ItemNBT.setTag(stack, compound);
                        JSGSoundHelper.playSoundToPlayer(player, JSGSoundEvents.UNIVERSE_DIALER_MODE_CHANGE, player.blockPosition());
                        return;
                    }
                    mode.handlePacketToServer(action, modeTag, this, ctx);
                    ItemNBT.setTag(stack, compound);
                }
            }
        });
    }
}
