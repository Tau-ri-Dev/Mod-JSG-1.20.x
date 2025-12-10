package dev.tauri.jsg.api.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilteredEditBox extends CallbackEditBox {
    public FilteredEditBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage, Consumer<String> callback, Predicate<String> filter) {
        super(pFont, pX, pY, pWidth, pHeight, pMessage, callback);
        setFilter(filter);
    }
}
