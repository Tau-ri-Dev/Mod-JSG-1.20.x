package dev.tauri.jsg.api.config.ingame;

import dev.tauri.jsg.api.JSGApi;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents options for in-game configs e.g. Point of Origin for a Milkyway gate
 *
 * @author MR_Spagetty
 */
public abstract class JSGConfigOption<T> {
    protected static final int X = -25;

    public final List<String> comment;
    protected T value;
    public final T defaultValue;
    protected final String label;

    /**
     * creates a new config option object with the value set to the default value
     *
     * @param label        the label of the config option
     * @param defaultValue
     * @param comment
     */
    public JSGConfigOption(String label, @Nonnull T defaultValue, String... comment) {
        this(label, defaultValue, defaultValue, comment);
    }

    public JSGConfigOption(String label, @Nonnull T defaultValue, @Nonnull T value, String... comment) {
        this.label = label;
        this.defaultValue = defaultValue;
        this.value = value;
        this.comment = Arrays.asList(comment);
    }

    /**
     * creates the the widget for modifying this config option in the gui
     *
     * @param y the y coordinate to place this widget at in the gui
     * @return the widget created
     */
    @OnlyIn(Dist.CLIENT)
    public abstract AbstractWidget createGUIComponent(int y, int id);

    /**
     * Retrieves the label associated with this configuration option.
     *
     * @return The label of this configuration option.
     */
    public String getLabel() {
        return label;
    }

    /**
     * gets the default value for this config option
     *
     * @return the default value
     */
    protected T getDefaultValue() {
        return defaultValue;
    }

    /**
     * any additional info to be in the comment in the gui e.g.
     *
     * @return the additional info as a list where each item is a new line
     */
    protected List<String> additionalCommentInfo() {
        return List.of();
    }

    /**
     * gets the config option's comment with the minimum and maximum integer values if applicable and
     * the default value
     *
     * @return the expanded comment as a List of components
     */
    public List<Component> getCommentToRender() {
        List<String> c = new ArrayList<>(getComment());
        c.add("---------------------------------");
        c.addAll(additionalCommentInfo());
        c.add("Default: " + getDefaultValue());
        c.add("---------------------------------");
        List<Component> cc = new ArrayList<>();
        for (String s : c) cc.add(Component.literal(s));
        return cc;
    }

    /**
     * gets the current comment
     *
     * @return the current comment
     */
    public List<String> getComment() {
        return comment;
    }

    /**
     * sets the value of the config option
     *
     * @param value the new value
     * @return whether the value was changed
     * @throws IllegalArgumentException if the given value is not allowed (e.g. outside of range)
     */
    public abstract boolean setValue(String value);

    /**
     * directly sets the value of the config option if the new value is different
     *
     * @param newValue the new value
     * @return whether the value was changed
     */
    public boolean setValue(T newValue) {
        if (!this.value.equals(newValue)) {
            this.value = newValue;
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean setValue(Tag newValue) {
        if (newValue == null) {
            return setValue(this.defaultValue);
        } else if (newValue instanceof IntTag i && this.defaultValue instanceof Integer) {
            return setValue((T) (Object) i.getAsInt());
        } else if (newValue instanceof ByteTag b && this.defaultValue instanceof Boolean) {
            return setValue((T) (Object) (b.getAsByte() == 1));
        } else if (newValue instanceof StringTag s) {
            return setValue(s.getAsString());
        }
        JSGApi.logger.warn("invalid tag read for config option %s expected tag containing %s, got tag of %s type with value \"%s\""
                .formatted(this.label, this.defaultValue.getClass().getSimpleName(), newValue.getType(), newValue.getAsString()));
        return false;
    }

    /**
     * Retrieves the current value of this configuration option.
     *
     * @return The current value of this configuration option.
     */
    @Nonnull
    public T getValue() {
        return this.value;
    }

    /**
     * gets the value of this config option as a NBT tag
     *
     * @return the NBT tag
     */
    public Tag valueAsNBT() {
        if (this.value instanceof Integer i) {
            return IntTag.valueOf(i);
        } else if (this.value instanceof Boolean b) {
            return ByteTag.valueOf(b);
        }
        return StringTag.valueOf(this.value.toString());
    }

    /**
     * writes the value of this config to the given Byte buffer
     *
     * @param buf the buffer to write to
     */
    public void writeBytes(ByteBuf buf) {
        if (this.value instanceof Integer i) {
            buf.writeInt(i);
        } else if (this.value instanceof Boolean b) {
            buf.writeByte(b ? 1 : 0);
        } else {
            String val = this.value.toString();
            buf.writeInt(val.length());
            buf.writeCharSequence(val, StandardCharsets.UTF_8);
        }
    }

    @SuppressWarnings("unchecked")
    public void readBytes(ByteBuf buf) {
        if (this.defaultValue instanceof Integer) {
            this.value = (T) (Object) buf.readInt();
        } else if (this.defaultValue instanceof Boolean) {
            this.value = (T) (Object) (buf.readByte() == 1);
        } else {
            String val = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
            setValue(val);
        }
    }
}
