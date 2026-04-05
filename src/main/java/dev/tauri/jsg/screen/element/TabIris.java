package dev.tauri.jsg.screen.element;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.core.client.screen.tab.tabs.Tab;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.screen.widget.ModeButton;
import dev.tauri.jsg.core.client.screen.widget.NumberOnlyTextField;
import dev.tauri.jsg.core.common.integration.Integrations;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author matousss
 */
public class TabIris extends Tab {
    protected static final ResourceLocation MODES_ICONS = JSGMapping.rl(JSG.MOD_ID, "textures/gui/iris_mode.png");


    protected NumberOnlyTextField inputField = new NumberOnlyTextField(
            guiLeft + 6, guiTop + defaultY + 25,
            64, 16);

    public String code;
    public EnumIrisMode irisMode;
    protected ModeButton buttonChangeMode = new ModeButton(
            1, inputField.getX() + inputField.getWidth() + 5, guiTop + defaultY + 25, 16, MODES_ICONS,
            80, 32, 5);

    protected boolean isUniverse;


    protected TabIris(TabIrisBuilder builder) {
        super(builder);
        this.irisMode = builder.irisMode;
        this.isUniverse = builder.isUniverse;
        code = builder.code;
        buttonChangeMode.setCurrentState(irisMode.id);
        inputField.setMaxLength(JSGConfig.Stargate.irisCodeLength.get());
        inputField.setValue(code);
        inputField.setEnabled(buttonChangeMode.getCurrentState() == EnumIrisMode.AUTO.id);
    }

    public void updateValue(EnumIrisMode irisMode) {
        buttonChangeMode.setCurrentState(irisMode.id);
        inputField.setEnabled(buttonChangeMode.getCurrentState() == EnumIrisMode.AUTO.id);
    }

    public void updateValue(String irisCode) {
        inputField.setValue(irisCode);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isVisible()) return;
        super.render(graphics, mouseX, mouseY);
        buttonChangeMode.setX(guiLeft + currentOffsetX + 64 + 11);
        buttonChangeMode.drawButton(graphics, mouseX, mouseY);
        inputField.setX(guiLeft + 6 + currentOffsetX);
        inputField.render(graphics, mouseX, mouseY, 0);
    }

    @Override
    public void renderFg(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderFg(graphics, mouseX, mouseY);
        if (isVisible() && isOpen()) {
            if (GuiHelper.isPointInRegion(buttonChangeMode.getX(), buttonChangeMode.getY(), buttonChangeMode.getWidth(), buttonChangeMode.getHeight(), mouseX, mouseY)) {
                List<Component> text = new ArrayList<>();
                text.add(Component.literal(Component.translatable("gui.stargate.iris.help_title").getString() + " " + Component.translatable("gui.stargate.iris." + getIrisMode().name().toLowerCase()).getString()));
                text.add(Component.translatable("gui.stargate.iris." + getIrisMode().name()
                        .toLowerCase() + "_help").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
                );
                if (buttonChangeMode.getCurrentState() == 2) {
                    text.add(Component.translatable("gui.stargate.iris.auto1_help").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                }
                graphics.renderTooltip(Minecraft.getInstance().font, text, Optional.empty(), mouseX - guiLeft, mouseY - guiTop);
            }
        }
    }

    public static TabIrisBuilder builder() {
        return new TabIrisBuilder();
    }

    public static class TabIrisBuilder extends TabBuilder {
        private EnumIrisMode irisMode = EnumIrisMode.OPENED;

        private String code = "";
        private boolean isUniverse = false;


        public TabIrisBuilder setCode(String code) {
            this.code = code;
            return this;
        }

        public TabIrisBuilder setIsUniverse(boolean is) {
            isUniverse = is;
            return this;
        }

        public TabIrisBuilder setIrisMode(EnumIrisMode irisMode) {
            this.irisMode = irisMode;
            return this;
        }

        @Override
        public TabIris build() {
            return new TabIris(this);
        }
    }

    /*
     * left = 0
     * right = 1
     * middle = 2
     * */

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        inputField.mouseClicked(mouseX, mouseY, mouseButton);
        if (GuiHelper.isPointInRegion(buttonChangeMode.getX(), buttonChangeMode.getY(),
                buttonChangeMode.getWidth(), buttonChangeMode.getHeight(), mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    buttonChangeMode.nextState();
                    if (!(Integrations.CCT.isLoaded() || Integrations.OC2.isLoaded()) && buttonChangeMode.getCurrentState() == 3) {
                        buttonChangeMode.nextState();
                    }
                    if (!isUniverse && buttonChangeMode.getCurrentState() == 4) {
                        buttonChangeMode.nextState();
                    }
                    break;
                case 1:
                    buttonChangeMode.previousState();
                    if (!isUniverse && buttonChangeMode.getCurrentState() == 4) {
                        buttonChangeMode.previousState();
                    }
                    if (!(Integrations.CCT.isLoaded() || Integrations.OC2.isLoaded()) && buttonChangeMode.getCurrentState() == 3) {
                        buttonChangeMode.previousState();
                    }
                    break;
                case 2:
                    buttonChangeMode.setCurrentState(0);
                    break;

            }

            inputField.setEnabled(buttonChangeMode.getCurrentState() == EnumIrisMode.AUTO.id);
            buttonChangeMode.playDownSound(Minecraft.getInstance().getSoundManager());
        }

    }

    public EnumIrisMode getIrisMode() {
        return EnumIrisMode.getValue((byte) buttonChangeMode.getCurrentState());
    }

    public String getCode() {
        return !inputField.getValue().isEmpty() ? inputField.getValue() : "";
    }


    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        return inputField.keyPressed(typedChar, keyCode, 0);
    }

    @Override
    public boolean charTyped(char character, int intChar) {
        return inputField.charTyped(character, intChar);
    }

    private Runnable onTabClose = null;

    public void setOnTabClose(Runnable onTabClose) {
        this.onTabClose = onTabClose;
    }

    @Override
    public void closeTab() {
        if (onTabClose != null) onTabClose.run();

        super.closeTab();

    }
}
