package dev.tauri.jsg.api.config.ingame.option;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.ingame.JSGConfigOption;
import dev.tauri.jsg.api.client.screen.widget.ModeButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class JSGBooleanConfigOption extends JSGConfigOption<Boolean> {

    public JSGBooleanConfigOption(String label, Boolean defaultValue, Boolean value, String... comment) {
        super(label, defaultValue, value, comment);
    }

    public JSGBooleanConfigOption(String label, Boolean defaultValue, String... comment) {
        this(label, defaultValue, defaultValue, comment);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AbstractWidget createGUIComponent(int y, int id) {
        ModeButton button =
                new ModeButton(
                        id,
                        X,
                        y,
                        16,
                        new ResourceLocation(JSGApi.MOD_ID, "textures/gui/config/boolean_modes.png"),
                        32,
                        32,
                        2);
        button.setCurrentState(this.value ? 1 : 0);
        return button;
    }

    @Override
    public boolean setValue(String value) {
        return setValue(value.equals("true") || value.equals("1"));
    }

    @Override
    public Tag valueAsNBT() {
        return ByteTag.valueOf(this.value);
    }
}
