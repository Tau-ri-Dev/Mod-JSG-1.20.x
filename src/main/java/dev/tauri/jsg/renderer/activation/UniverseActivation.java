package dev.tauri.jsg.renderer.activation;

import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

public class UniverseActivation extends dev.tauri.jsg.core.client.renderer.Activation<SymbolInterface> {

    public UniverseActivation(SymbolInterface textureKey, long stateChange, boolean dim) {
        super(textureKey, stateChange, dim);
    }

    public UniverseActivation(SymbolInterface textureKey, ByteBuf buf) {
        super(textureKey, buf);
    }

    public UniverseActivation(SymbolInterface textureKey, CompoundTag compoundTag) {
        super(textureKey, compoundTag);
    }

    @Override
    protected float getMaxStage() {
        return 0.75f;
    }

    @Override
    protected float getTickMultiplier() {
        return 0.2f;
    }

}
