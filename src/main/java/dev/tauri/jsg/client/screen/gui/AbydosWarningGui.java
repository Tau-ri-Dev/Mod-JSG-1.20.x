package dev.tauri.jsg.client.screen.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

public class AbydosWarningGui extends ConfirmScreen {
    public AbydosWarningGui(BooleanConsumer callback) {
        super(callback,
                Component.translatable("joinWorld.jsg.abydos_update.title"),
                Component.translatable("joinWorld.jsg.abydos_update.desc"),
                Component.translatable("joinWorld.jsg.abydos_update.btn.proceed"), Component.translatable("joinWorld.jsg.abydos_update.btn.cancel")
        );
    }
}
