package dev.tauri.jsg.renderer.activation;


import dev.tauri.jsg.api.stargate.ChevronEnum;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

public class StargateActivation extends dev.tauri.jsg.core.client.renderer.Activation<ChevronEnum> {

    public StargateActivation(ChevronEnum textureKey, CompoundTag compoundTag) {
        super(textureKey, compoundTag);
    }

    public StargateActivation(ChevronEnum textureKey, ByteBuf buf) {
        super(textureKey, buf);
    }

    public StargateActivation(ChevronEnum textureKey, long stateChange, boolean dim) {
        super(textureKey, stateChange, dim);
    }

    @Override
    protected float getMaxStage() {
        return 10;
    }

    @Override
    protected float getTickMultiplier() {
        return 1.5f;
    }
}
