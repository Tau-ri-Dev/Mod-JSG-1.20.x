package dev.tauri.jsg.api.config.ingame.option;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.ingame.JSGConfigOption;
import dev.tauri.jsg.api.client.screen.widget.NumberOnlyTextField;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents a config option whos value is an intger that may fall within a limited range (between 2 values inclusive no less than a single value or no greater than a single value)
 *
 * @author MR_Spagetty
 */
public class JSGIntRangeConfigOption extends JSGConfigOption<Integer> {

    public final Optional<Integer> min;
    public final Optional<Integer> max;

    public JSGIntRangeConfigOption(String label, @Nullable Integer min, @Nullable Integer max, @Nonnull Integer defaultValue, @Nonnull Integer value, String... comment) {
        super(label, defaultValue, value, comment);
        this.min = Optional.ofNullable(min);
        this.max = Optional.ofNullable(max);
    }

    public JSGIntRangeConfigOption(String label, @Nullable Integer min, @Nullable Integer max, @Nonnull Integer defaultValue, String... comment) {
        this(label, min, max, defaultValue, defaultValue, comment);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AbstractWidget createGUIComponent(int y, int id) {
        EditBox field = new NumberOnlyTextField(X, y, 90, 15);
        field.setValue(this.value.toString());
        return field;
    }

    @Override
    public boolean setValue(String value) {
        try {
            return setValue(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            JSGApi.logger.warn("\"%s\" is not a valid integer as required by %s".formatted(value, this.label));
            return false;
        }
    }

    @Override
    public boolean setValue(Integer newValue) {
        if ((min.isPresent() && newValue < min.get()) || (max.isPresent() && newValue > max.get())) {
            JSGApi.logger.warn("\"%d\" is not within the valid range of %d to %d for %s".formatted(newValue, min.orElse(Integer.MIN_VALUE), max.orElse(Integer.MAX_VALUE), this.label));
            return false;
        }
        return super.setValue(newValue);
    }

    @Override
    protected List<String> additionalCommentInfo() {
        final List<String> addComment = Stream.concat(min.map(i -> "Min: " + i).stream(), max.map(i -> "Max " + i).stream()).toList();
        return addComment;
    }

    @Override
    public Tag valueAsNBT() {
        return IntTag.valueOf(this.value);
    }

    @Override
    public void writeBytes(ByteBuf buf) {
        buf.writeInt(this.value);
    }
}
