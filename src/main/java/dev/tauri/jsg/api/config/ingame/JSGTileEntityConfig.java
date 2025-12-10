package dev.tauri.jsg.api.config.ingame;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class JSGTileEntityConfig {

    @Nonnull
    private List<JSGConfigOption<?>> options;
    private ResourceLocation blockType;

    public JSGTileEntityConfig(@Nonnull ResourceLocation blockType) {
        this();
        this.blockType = blockType;
        reset();

    }

    public JSGTileEntityConfig(@Nonnull ByteBuf buf) {
        this();
        reset();
        this.fromBytes(buf);
    }

    public JSGTileEntityConfig() {
        this.blockType = new ResourceLocation("unknown");
        this.options = new ArrayList<>();
    }

    public void addOption(@Nonnull JSGConfigOption<?> option) {
        this.options.add(option);
    }

    protected void reset() {
        options.clear();
        options.addAll(BlockConfigOptionRegistry.get(this.blockType));
    }

    public List<JSGConfigOption<?>> getOptions() {
        return options;
    }

    public boolean has(String string) {
        return options.parallelStream().anyMatch(opt -> opt.getLabel().equals(string));
    }

    @Nonnull
    public JSGConfigOption<?> getOption(int id) {
        return options.get(id);
    }

    @Nonnull
    public JSGConfigOption<?> getOption(String label) {
        return getOptionMaybe(label)
                .orElseThrow(() -> new NoSuchElementException("No such config option with label: \"%s\"".formatted(label)));
    }

    @Nonnull
    public Optional<JSGConfigOption<?>> getOptionMaybe(String label) {
        return options.stream().filter(o -> o.getLabel().equals(label)).findFirst();
    }

    public void clearOptions() {
        options.clear();
    }

    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        options.stream().filter(
                        o -> !Optional.ofNullable(o.getValue()).map(o.defaultValue::equals).orElse(o.defaultValue != null))
                .forEach(o -> compound.put(o.label, o.valueAsNBT()));
        return compound;
    }

    public boolean deserializeNBT(CompoundTag compound) {
        return options.stream().map(o -> Optional.ofNullable(compound.get(o.label)).map(o::setValue).orElse(false))
                .parallel().reduce((a, b) -> a || b).orElse(false);
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.blockType.toString().length());
        buf.writeCharSequence(this.blockType.toString(), StandardCharsets.UTF_8);
        options.stream().forEach(o -> o.writeBytes(buf));
    }

    public void fromBytes(ByteBuf buf) {
        this.blockType = new ResourceLocation(buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString());
        options.clear();
        options.addAll(BlockConfigOptionRegistry.get(this.blockType));
        options.stream().forEach(o -> o.readBytes(buf));
    }

}
