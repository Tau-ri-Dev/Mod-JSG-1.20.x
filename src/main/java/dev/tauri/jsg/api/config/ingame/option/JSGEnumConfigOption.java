package dev.tauri.jsg.api.config.ingame.option;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.ingame.JSGConfigOption;
import dev.tauri.jsg.api.client.screen.widget.EnumButton;
import dev.tauri.jsg.api.client.screen.widget.ModeButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;

/**
 * Represents config option that ahs a limited set of explicit options
 *
 * @author MR_Spagetty
 */
public class JSGEnumConfigOption<T> extends JSGConfigOption<T> {
    public final List<T> allowedValues;

    public JSGEnumConfigOption(String label, List<T> allowedValues, T defaultValue, T value, String... comment) {
        super(label, defaultValue, value, comment);
        this.allowedValues = allowedValues;
    }

    public JSGEnumConfigOption(String label, List<T> allowedValues, T defaultValue, String... comment) {
        this(label, allowedValues, defaultValue, defaultValue, comment);
    }

    public JSGEnumConfigOption(String label, List<T> allowedValues, int defaultValueIndex, String... comment) {
        this(label, allowedValues, allowedValues.get(defaultValueIndex), comment);
    }

    public JSGEnumConfigOption(String label, List<T> allowedValues, String... comment) {
        this(label, allowedValues, 0, comment);
    }

    public JSGEnumConfigOption(String label, T[] allowedValues, T defaultValue, T value, String... comment) {
        this(label, Arrays.asList(allowedValues), defaultValue, value, comment);
    }

    public JSGEnumConfigOption(String label, T[] allowedValues, T defaultValue, String... comment) {
        this(label, allowedValues, defaultValue, defaultValue, comment);
    }

    public JSGEnumConfigOption(String label, T[] allowedValues, int defaultValueIndex, String... comment) {
        this(label, allowedValues, allowedValues[defaultValueIndex], comment);
    }

    public JSGEnumConfigOption(String label, T[] allowedValues, String... comment) {
        this(label, allowedValues, 0, comment);
    }

    @Override
    public boolean setValue(String value) {
        return allowedValues.parallelStream().filter(av -> av.toString().equals(value))
                .reduce((a, b) -> {
                    throw null;
                }).map(super::setValue).orElseGet(() -> {
                    JSGApi.logger.warn("\"%s\" is not an allowed value for %s".formatted(value, this.label));
                    return false;
                });
    }

    @Override
    public boolean setValue(T newValue) {
        if (allowedValues.parallelStream().noneMatch(av -> av.equals(newValue))) {
            JSGApi.logger.warn("\"%s\" is not an allowed value for %s".formatted(value, this.label));
            return false;
        }
        return super.setValue(newValue);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AbstractWidget createGUIComponent(int y, int id) {
        ModeButton button = new EnumButton<T>(id, X, y, allowedValues);
        return button;
    }
}
