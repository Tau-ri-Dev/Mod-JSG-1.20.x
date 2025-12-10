package dev.tauri.jsg.api.client.screen.widget;

import dev.tauri.jsg.api.client.screen.widget.base.JSGTextField;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class NumberOnlyTextField extends JSGTextField {
    public NumberOnlyTextField(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal(""));
    }

    @Override
    public void setMessage(@NotNull Component component) {
        super.setMessage(Component.literal(component.getString().replaceAll("\\D+", "")));
    }

    public void setEnabled(boolean enabled) {
        if (!enabled && isFocused()) setFocused(false);
        active = enabled;
    }

    public boolean isEnabled() {
        return super.isActive();
    }
}
