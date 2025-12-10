package dev.tauri.jsg.api.config.values;

import dev.tauri.jsg.api.config.screen.entry.*;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Optional;

public abstract class JSGConfigValue {
    public final List<String> comment;

    public JSGConfigValue(List<String> comment) {
        this.comment = comment;
    }

    public abstract AbstractConfigEntry getGuiEntry(int width);

    public abstract String getPath();

    public static class BooleanValue extends JSGConfigValue {
        public ForgeConfigSpec.BooleanValue booleanValue;

        public BooleanValue(ForgeConfigSpec.Builder builder, String path, boolean defaultValue, String... comment) {
            super(List.of(comment));
            this.booleanValue = builder
                    .comment(comment)
                    .define(path, defaultValue);
        }

        public void set(boolean value) {
            booleanValue.set(value);
            booleanValue.save();
        }

        public boolean get() {
            return booleanValue.get();
        }

        public boolean getDefault() {
            return booleanValue.getDefault();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public AbstractConfigEntry getGuiEntry(int width) {
            return new BooleanConfigEntry(Component.literal(booleanValue.getPath().get(booleanValue.getPath().size() - 1)), width, this);
        }

        @Override
        public String getPath() {
            return String.join(".", booleanValue.getPath());
        }
    }

    public static class IntValue extends JSGConfigValue {
        public ForgeConfigSpec.IntValue intValue;
        protected int min;
        protected int max;

        public IntValue(ForgeConfigSpec.Builder builder, String path, int defaultValue, int min, int max, String... comment) {
            super(List.of(comment));
            this.intValue = builder
                    .comment(comment)
                    .defineInRange(path, defaultValue, min, max);
            this.min = min;
            this.max = max;
        }

        public void set(int value) {
            intValue.set(value);
            intValue.save();
        }

        public int get() {
            return intValue.get();
        }

        public int getDefault() {
            return intValue.getDefault();
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public AbstractConfigEntry getGuiEntry(int width) {
            return new IntConfigEntry(Component.literal(intValue.getPath().get(intValue.getPath().size() - 1)), width, this);
        }

        @Override
        public String getPath() {
            return String.join(".", intValue.getPath());
        }
    }

    public static class DoubleValue extends JSGConfigValue {
        public ForgeConfigSpec.DoubleValue value;
        protected boolean slider;
        protected double min;
        protected double max;

        public DoubleValue(ForgeConfigSpec.Builder builder, String path, double defaultValue, double min, double max, boolean hasSlider, String... comment) {
            this(builder, path, defaultValue, min, max, comment);
            this.slider = hasSlider;
        }

        public DoubleValue(ForgeConfigSpec.Builder builder, String path, double defaultValue, double min, double max, String... comment) {
            super(List.of(comment));
            this.slider = false;
            this.value = builder
                    .comment(comment)
                    .defineInRange(path, defaultValue, min, max);
            this.min = min;
            this.max = max;
        }

        public void set(double value) {
            this.value.set(value);
            this.value.save();
        }

        public double get() {
            return value.get();
        }

        public boolean hasSlider() {
            return slider;
        }

        public double getDefault() {
            return value.getDefault();
        }

        public double getMin() {
            return this.min;
        }

        public double getMax() {
            return this.max;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public AbstractConfigEntry getGuiEntry(int width) {
            if (hasSlider())
                return new SliderConfigEntry(Component.literal(value.getPath().get(value.getPath().size() - 1)), Component.empty(), width, this);
            return new DoubleConfigEntry(Component.literal(value.getPath().get(value.getPath().size() - 1)), width, this);
        }

        @Override
        public String getPath() {
            return String.join(".", value.getPath());
        }
    }

    public static class LongValue extends JSGConfigValue {
        public ForgeConfigSpec.LongValue longValue;

        public LongValue(ForgeConfigSpec.Builder builder, String path, long defaultValue, long min, long max, String... comment) {
            super(List.of(comment));
            this.longValue = builder
                    .comment(comment)
                    .defineInRange(path, defaultValue, min, max);
        }

        public void set(long value) {
            longValue.set(value);
            longValue.save();
        }

        public long get() {
            return longValue.get();
        }

        public long getDefault() {
            return longValue.getDefault();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public AbstractConfigEntry getGuiEntry(int width) {
            return new LongConfigEntry(Component.literal(longValue.getPath().get(longValue.getPath().size() - 1)), width, this);
        }

        @Override
        public String getPath() {
            return String.join(".", longValue.getPath());
        }
    }

    @SuppressWarnings("all")
    public static class RGBAValue extends JSGConfigValue {
        public Component path;
        public String sPath;
        public ForgeConfigSpec.IntValue redValue;
        public ForgeConfigSpec.IntValue greenValue;
        public ForgeConfigSpec.IntValue blueValue;
        public Optional<ForgeConfigSpec.IntValue> alphaValue = Optional.empty();

        public RGBAValue(ForgeConfigSpec.Builder builder, String path, int red, int blue, int green, String... comment) {
            super(List.of(comment));
            this.sPath = String.join(".", path);
            this.path = Component.literal(sPath);
            builder.comment(comment);
            this.redValue = builder
                    .defineInRange(path + ".red", red, 0, 255);
            this.greenValue = builder
                    .defineInRange(path + ".green", blue, 0, 255);
            this.blueValue = builder
                    .defineInRange(path + ".blue", green, 0, 255);
        }

        public RGBAValue(ForgeConfigSpec.Builder builder, String path, int red, int blue, int green, int alpha, String... comment) {
            super(List.of(comment));
            builder.comment(comment);
            this.redValue = builder.defineInRange(path + ".red", red, 0, 255);
            this.greenValue = builder.defineInRange(path + ".green", blue, 0, 255);
            this.blueValue = builder.defineInRange(path + ".blue", green, 0, 255);
            this.alphaValue = Optional.of(builder.defineInRange(path + ".alpha", alpha, 0, 255));
        }

        public int get() {
            return (hasAlpha() ? getAlpha() << 24 : 0) | (getRed() << 16 | getGreen() << 8 | getBlue());
        }

        public void setRed(int value) {
            redValue.set(value);
            redValue.save();
        }

        public void setGreen(int value) {
            greenValue.set(value);
            greenValue.save();
        }

        public void setBlue(int value) {
            blueValue.set(value);
            blueValue.save();
        }

        public void setAlpha(int value) {
            if (alphaValue.isEmpty()) return;
            alphaValue.get().set(value);
            alphaValue.get().save();
        }

        public boolean hasAlpha() {
            return alphaValue.isPresent();
        }

        public int getRed() {
            return redValue.get();
        }

        public int getGreen() {
            return greenValue.get();
        }

        public int getBlue() {
            return blueValue.get();
        }

        public int getAlpha() {
            if (alphaValue.isEmpty()) return 0xff;
            return alphaValue.get().get();
        }

        public int getRedDefault() {
            return redValue.getDefault();
        }

        public int getGreenDefault() {
            return greenValue.getDefault();
        }

        public int getBlueDefault() {
            return blueValue.getDefault();
        }

        public int getAlphaDefault() {
            if (alphaValue.isEmpty()) return 0xff;
            return alphaValue.get().getDefault();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public AbstractConfigEntry getGuiEntry(int width) {
            return null;
        }

        @Override
        public String getPath() {
            return sPath;
        }
    }

    public static class EnumValue<T extends Enum<T>> extends JSGConfigValue {
        public ForgeConfigSpec.EnumValue<T> enumValue;

        public EnumValue(ForgeConfigSpec.Builder builder, String path, T defaultValue, String... comment) {
            super(List.of(comment));
            this.enumValue = builder
                    .comment(comment)
                    .defineEnum(path, defaultValue);
        }

        public void set(T value) {
            enumValue.set(value);
            enumValue.save();
        }

        public T get() {
            return enumValue.get();
        }

        public T getDefault() {
            return enumValue.getDefault();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public AbstractConfigEntry getGuiEntry(int width) {
            return new EnumConfigEntry<>(Component.literal(enumValue.getPath().get(enumValue.getPath().size() - 1)), width, this);
        }

        @Override
        public String getPath() {
            return String.join(".", enumValue.getPath());
        }
    }

    public static class ListStringValue extends JSGConfigValue {
        public ForgeConfigSpec.ConfigValue<List<? extends String>> value;

        public ListStringValue(ForgeConfigSpec.Builder builder, String path, List<? extends String> defaultValue, String... comment) {
            super(List.of(comment));
            this.value = builder
                    .comment(comment)
                    .define(path, defaultValue);
        }

        public void set(List<? extends String> value) {
            this.value.set(value);
            this.value.save();
        }

        public List<? extends String> get() {
            return value.get();
        }

        public List<? extends String> getDefault() {
            return value.getDefault();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public AbstractConfigEntry getGuiEntry(int width) {
            return null;
        }

        @Override
        public String getPath() {
            return String.join(".", value.getPath());
        }
    }
}
