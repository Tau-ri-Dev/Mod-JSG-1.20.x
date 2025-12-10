package dev.tauri.jsg.api.client.screen.widget.base;

import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

public class JSGTextField extends JSGTextFieldClassic {

    private JSGButton.ActionCallback actionCallback;
    private Component originalContent;
    private boolean numbersOnly;

    public boolean performActionOnKeyUp = false;

    public JSGTextField(int x, int y, int width, int height, String originalContent) {
        this(-1, x, y, width, height, originalContent);
    }

    public JSGTextField(int id, int x, int y, int width, int height, String originalContent) {
        this(id, x, y, width, height, Component.literal(originalContent));
    }

    public JSGTextField(int x, int y, int width, int height, Component originalContent) {
        this(-1, x, y, width, height, originalContent);
    }

    public JSGTextField(int id, int x, int y, int width, int height, Component originalContent) {
        super(id, x, y, width, height, originalContent);
        this.originalContent = originalContent;
        setMessage(originalContent);
    }

    public JSGTextField setMaxStringLengthBetter(int maxNameLength) {
        super.setMaxLength(maxNameLength);
        return this;
    }

    public JSGTextField setActionCallback(JSGButton.ActionCallback callback) {
        actionCallback = callback;
        return this;
    }

    public JSGTextField setNumbersOnly() {
        this.numbersOnly = true;
        return this;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setMessage(Component comToWrite) {
        if (numbersOnly) {
            String textToWrite = comToWrite.getString();
            textToWrite = textToWrite.replaceAll("\\D+", "");
            super.setMessage(Component.literal(textToWrite));
        }
        else
            super.setMessage(comToWrite);

        if (performActionOnKeyUp) {
            if (isFocused() && actionCallback != null) {
                //originalContent = getMessage();
                actionCallback.performAction();
            }
        }
    }

    @Override
    public void deleteChars(int num) {
        super.deleteChars(num);

        if (performActionOnKeyUp) {
            if (isFocused() && actionCallback != null) {
                //originalContent = getMessage();
                actionCallback.performAction();
            }
        }
    }

    @Override
    public void setFocused(boolean focused) {
        if (isFocused() && !focused) {
            // Unfocused and changed name
            originalContent = getMessage();
            if(actionCallback != null)
                actionCallback.performAction();
        } else if (!isFocused() && focused) {
            originalContent = getMessage();
        }

        super.setFocused(focused);
    }

    @Override
    public boolean charTyped(char c, int i) {
        boolean a = super.charTyped(c, i);
        if(a && performActionOnKeyUp && actionCallback != null)
            actionCallback.performAction();

        return a;
    }
}
