package dev.tauri.jsg.state.stargate;

import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public class StargateBiomeOverrideState extends State {
    public StargateBiomeOverrideState() {
    }

    public BiomeOverlayInstance biomeOverride;

    public StargateBiomeOverrideState(BiomeOverlayInstance biomeOverride) {
        this.biomeOverride = biomeOverride;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (biomeOverride != null) {
            buf.writeBoolean(true);
            new FriendlyByteBuf(buf).writeResourceLocation(biomeOverride.getId());
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            biomeOverride = BiomeOverlayInstance.byId(new FriendlyByteBuf(buf).readResourceLocation());
        }
    }
}
