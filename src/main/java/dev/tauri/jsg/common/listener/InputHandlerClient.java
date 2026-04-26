package dev.tauri.jsg.common.listener;

import com.mojang.blaze3d.platform.InputConstants;
import dev.tauri.jsg.client.screen.gui.DialerVirtualGui;
import dev.tauri.jsg.client.screen.gui.GDOVirtualGui;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerClientActionEnum;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.packet.packets.linkable.GDOCodeKeyPressedToServer;
import dev.tauri.jsg.common.packet.packets.linkable.UniverseDialerActionPacketToServer;
import dev.tauri.jsg.common.packet.packets.linkable.UniverseDialerKeyPressedToServer;
import dev.tauri.jsg.common.registry.JSGItems;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

import static com.mojang.blaze3d.platform.InputConstants.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class InputHandlerClient {
    // Common bindings
    private static final KeyMapping MODE_SCROLL = new KeyMapping("config.jsg.mode_scroll", KEY_LCONTROL, "key.categories.jsg");
    private static final KeyMapping ADDRESS_SCROLL = new KeyMapping("config.jsg.address_scroll", KEY_LSHIFT, "key.categories.jsg");

    private static final KeyMapping MODE_UP = new KeyMapping("config.jsg.mode_up", KEY_NUMPAD6, "key.categories.jsg");
    private static final KeyMapping MODE_DOWN = new KeyMapping("config.jsg.mode_down", KEY_NUMPAD4, "key.categories.jsg");
    private static final KeyMapping ADDRESS_UP = new KeyMapping("config.jsg.address_up", KEY_NUMPAD8, "key.categories.jsg");
    private static final KeyMapping ADDRESS_DOWN = new KeyMapping("config.jsg.address_down", KEY_NUMPAD2, "key.categories.jsg");
    private static final KeyMapping RENAME_ENTRY = new KeyMapping("config.jsg.rename_entry", KEY_INSERT, "key.categories.jsg");

    public static final KeyMapping[] KEY_BINDINGS = {
            MODE_SCROLL,
            ADDRESS_SCROLL,

            MODE_UP,
            MODE_DOWN,
            ADDRESS_UP,
            ADDRESS_DOWN,
            RENAME_ENTRY
    };

    @SubscribeEvent
    public static void onMouseEvent(InputEvent.MouseScrollingEvent event) {
        boolean next = event.getScrollDelta() < 0;
        if (checkForItem(JSGItems.UNIVERSE_DIALER.get())) {
            var hand = getHand(JSGItems.UNIVERSE_DIALER.get());
            UniverseDialerClientActionEnum action = null;


            if (hand != null) {
                if (MODE_SCROLL.isDown())
                    action = UniverseDialerClientActionEnum.MODE_CHANGE;

                else if (ADDRESS_SCROLL.isDown())
                    action = UniverseDialerClientActionEnum.ADDRESS_CHANGE;


                // ---------------------------------------------
                if (action != null) {
                    event.setCanceled(true);
                    JSGPacketHandler.sendToServer(new UniverseDialerActionPacketToServer(action, hand, next));
                }
            }
        }
    }


    @SubscribeEvent
    public static void onKeyboardEvent(InputEvent.Key event) {
        if (event.getAction() != PRESS && event.getAction() != REPEAT) return;
        var player = Minecraft.getInstance().player;

        if (player == null) return;

        if (checkForItem(JSGItems.UNIVERSE_DIALER.get())) {
            var hand = getHand(JSGItems.UNIVERSE_DIALER.get());
            UniverseDialerClientActionEnum action = null;
            boolean next = false;
            if (Minecraft.getInstance().screen instanceof DialerVirtualGui) {
                return;
            }

            if (RENAME_ENTRY.isDown()) {
                Minecraft.getInstance().setScreen(new DialerVirtualGui());
                return;
            }

            if (MODE_UP.isDown()) {
                action = UniverseDialerClientActionEnum.MODE_CHANGE;
            } else if (MODE_DOWN.isDown()) {
                action = UniverseDialerClientActionEnum.MODE_CHANGE;
                next = true;
            } else if (ADDRESS_UP.isDown()) {
                action = UniverseDialerClientActionEnum.ADDRESS_CHANGE;
            } else if (ADDRESS_DOWN.isDown()) {
                action = UniverseDialerClientActionEnum.ADDRESS_CHANGE;
                next = true;
            }

            // ---------------------------------------------
            if (action != null) {
                JSGPacketHandler.sendToServer(new UniverseDialerActionPacketToServer(action, hand, next));
            }
        } else if (checkForItem(JSGItems.GDO.get()) && Minecraft.getInstance().screen instanceof GDOVirtualGui) {
            var hand = getHand(JSGItems.GDO.get());
            var key = InputConstants.getKey(event.getKey(), event.getScanCode());
            var numeric = key.getNumericKeyValue();
            if (numeric.isPresent()) {
                var number = numeric.getAsInt();
                JSGPacketHandler.sendToServer(new GDOCodeKeyPressedToServer(hand, number));
            } else if (key.getValue() == KEY_RETURN) {
                Minecraft.getInstance().setScreen(null);
                JSGPacketHandler.sendToServer(new GDOCodeKeyPressedToServer(hand, -1));
            } else if (key.getValue() == KEY_BACKSPACE) {
                JSGPacketHandler.sendToServer(new GDOCodeKeyPressedToServer(hand, -2));
            }
        }
    }

    // Get hand holding item
    @Nullable
    public static InteractionHand getHand(Item item) {
        var player = Minecraft.getInstance().player;
        return UniverseDialerKeyPressedToServer.getHand(player, item);
    }

    @Nullable
    public static ItemStack getItemStack(Player player, Item item) {
        InteractionHand hand = getHand(item);

        if (hand != null) {
            return player.getItemInHand(hand);
        }

        return null;
    }

    // Check for item in both hands
    public static boolean checkForItem(Item item) {
        return getHand(item) != null;
    }
}
