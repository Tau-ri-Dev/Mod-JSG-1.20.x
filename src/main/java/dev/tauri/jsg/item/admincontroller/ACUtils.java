package dev.tauri.jsg.item.admincontroller;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ACUtils {
    public static Component getSuccess(Component message) {
        return Component.empty()
                .append(Component.translatable("gui.admincontroller.response.success").withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN))
                .append(Component.literal(" "))
                .append(message);
    }

    public static Component getError(Component message) {
        return Component.empty()
                .append(Component.translatable("gui.admincontroller.response.error").withStyle(ChatFormatting.BOLD, ChatFormatting.RED))
                .append(Component.literal(" "))
                .append(message);
    }
}
