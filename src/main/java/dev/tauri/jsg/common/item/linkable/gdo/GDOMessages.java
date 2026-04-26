package dev.tauri.jsg.common.item.linkable.gdo;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author matousss
 */
public enum GDOMessages {
    OPENED(Component.translatable("item.jsg.gdo.iris_opened")),
    BUSY(Component.translatable("item.jsg.gdo.iris_busy")),
    CODE_ACCEPTED(Component.translatable("item.jsg.gdo.code_accepted")),
    CODE_REJECTED(Component.translatable("item.jsg.gdo.code_rejected")),

    CODE_NOT_SET(Component.translatable("item.jsg.gdo.code_not_set")),
    SEND_TO_COMPUTER(Component.translatable("item.jsg.gdo.computer_handled"));

    public final Component textComponent;

    GDOMessages(Component textComponent) {
        this.textComponent = textComponent;
    }

    public void sendMessageIfFailed(ServerPlayer player) {
        player.sendSystemMessage(textComponent, true);
    }
}
