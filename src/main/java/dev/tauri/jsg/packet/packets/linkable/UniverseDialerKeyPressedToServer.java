package dev.tauri.jsg.packet.packets.linkable;

import dev.tauri.jsg.core.common.packet.packets.JSGPacket;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.item.linkable.dialer.UniverseDialerItem;
import dev.tauri.jsg.item.linkable.dialer.UniverseDialerMode;
import dev.tauri.jsg.registry.JSGItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

public class UniverseDialerKeyPressedToServer extends JSGPacket {
    char codePoint;
    boolean shiftPressed;
    boolean altPressed;
    boolean ctrlPressed;
    boolean backspace;

    public UniverseDialerKeyPressedToServer(char codePoint, boolean backspace, boolean shiftPressed, boolean altPressed, boolean ctrlPressed) {
        this.codePoint = codePoint;
        this.shiftPressed = shiftPressed;
        this.altPressed = altPressed;
        this.ctrlPressed = ctrlPressed;
        this.backspace = backspace;
    }

    public UniverseDialerKeyPressedToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeChar(codePoint);
        buf.writeBoolean(shiftPressed);
        buf.writeBoolean(altPressed);
        buf.writeBoolean(ctrlPressed);
        buf.writeBoolean(backspace);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        codePoint = buf.readChar();
        shiftPressed = buf.readBoolean();
        altPressed = buf.readBoolean();
        ctrlPressed = buf.readBoolean();
        backspace = buf.readBoolean();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        var player = ctx.getSender();
        if (player == null) return;
        ctx.enqueueWork(() -> {
            var hand = getHand(player, JSGItems.UNIVERSE_DIALER.get());
            var stack = player.getItemInHand(hand);
            if (stack.getItem() != JSGItems.UNIVERSE_DIALER.get()) return;
            if (!stack.hasTag()) return;
            var compound = stack.getOrCreateTag();
            var mode = UniverseDialerMode.valueOf(JSGMapping.rl(compound.getString(UniverseDialerItem.C_MODE))).orElse(UniverseDialerMode.getDefault());
            mode.keyPressed(mode.getTag(compound), stack, player.level(), player, hand, codePoint, backspace, shiftPressed, altPressed, ctrlPressed);
        });
    }

    public static InteractionHand getHand(Player player, Item item) {
        InteractionHand hand = null;
        if (player == null)
            return null;
        if (player.getMainHandItem().getItem() == item)
            hand = InteractionHand.MAIN_HAND;
        else if (player.getOffhandItem().getItem() == item)
            hand = InteractionHand.OFF_HAND;

        return hand;
    }
}
