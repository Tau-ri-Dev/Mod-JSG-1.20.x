package dev.tauri.jsg.common.packet.packets.linkable;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.stargate.iris.codesender.PlayerCodeSender;
import dev.tauri.jsg.common.advancements.JSGCriterions;
import dev.tauri.jsg.common.item.linkable.gdo.GDOItem;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.core.common.packet.packets.JSGPacket;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

public class GDOCodeKeyPressedToServer extends JSGPacket {
    int number;
    InteractionHand hand;

    public GDOCodeKeyPressedToServer(InteractionHand hand, int number) {
        this.hand = hand;
        this.number = number;
    }

    public GDOCodeKeyPressedToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(number);
        buf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        number = buf.readInt();
        hand = buf.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        var player = ctx.getSender();
        if (player == null) return;
        ctx.enqueueWork(() -> {
            var stack = player.getItemInHand(hand);
            if (stack.getItem() != JSGItems.GDO.get()) return;
            if (!stack.hasTag()) return;
            var compound = stack.getOrCreateTag();
            var code = compound.getString("entered_code");
            if (number >= 0 && number <= 9) {
                code += String.valueOf(number);
                if (code.length() > JSGConfig.Stargate.irisCodeLength.get()) return;
                compound.putString("entered_code", code);
                stack.setTag(compound);
                JSGSoundHelper.playSoundToPlayer(player, JSGSoundEvents.GDO_BUTTON_CLICK, player.blockPosition());
                return;
            }
            if (number == -1 && !code.isEmpty()) {
                JSGSoundHelper.playSoundToPlayer(player, JSGSoundEvents.GDO_BUTTON_CLICK, player.blockPosition());
                if (((GDOItem) JSGItems.GDO.get()).sendCode(stack, new PlayerCodeSender(player)))
                    JSGCriterions.GDO_USED.trigger(player);
                compound.putString("entered_code", "");
                stack.setTag(compound);
            }
            if (number == -2 && !code.isEmpty()) {
                JSGSoundHelper.playSoundToPlayer(player, JSGSoundEvents.GDO_BUTTON_CLICK, player.blockPosition());
                compound.putString("entered_code", code.substring(0, code.length() - 1));
                stack.setTag(compound);
            }
        });
    }
}
