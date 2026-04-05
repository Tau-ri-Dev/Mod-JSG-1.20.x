package dev.tauri.jsg.renderer.stargate;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.api.stargate.iris.EnumIrisState;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.core.client.texture.ITextureLoader;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public abstract class StargateClassicRendererState extends StargateAbstractRendererState {

    public StargateClassicRendererState() {
    }

    public StargateClassicRendererState(StargateClassicRendererStateBuilder builder) {
        super(builder);
        this.biomeOverride = builder.biomeOverride;
        this.irisState = builder.irisState;
        this.irisType = builder.irisType;
        this.irisAnimation = builder.irisAnimation;
    }

    // Biome override
    // Saved
    public BiomeOverlayInstance biomeOverride;

    // Iris
    public EnumIrisType irisType;
    // Saved
    public EnumIrisState irisState;
    public long irisAnimation;

    // Heat
    public double irisHeat = 0;
    public double gateHeat = 0;

    @Override
    public BiomeOverlayInstance getBiomeOverlay() {
        if (biomeOverride != null) return biomeOverride;

        return super.getBiomeOverlay();
    }

    // ------------------------------------------------------------------------
    // Saving

    @Override
    public void toBytes(ByteBuf buf) {
        if (biomeOverride != null) {
            buf.writeBoolean(true);
            new FriendlyByteBuf(buf).writeResourceLocation(biomeOverride.getId());
        } else {
            buf.writeBoolean(false);
        }
        buf.writeByte(irisState.id);
        buf.writeByte(irisType.id);
        buf.writeLong(irisAnimation);
        buf.writeDouble(irisHeat);
        buf.writeDouble(gateHeat);
        super.toBytes(buf);
    }

    public ITextureLoader getTextureLoader() {
        return JSGApi.JSG_LOADERS_HOLDER.texture();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            biomeOverride = BiomeOverlayInstance.byId(new FriendlyByteBuf(buf).readResourceLocation());
        }
        irisState = EnumIrisState.getValue(buf.readByte());
        irisType = EnumIrisType.byId(buf.readByte());
        irisAnimation = buf.readLong();
        irisHeat = buf.readDouble();
        gateHeat = buf.readDouble();
        super.fromBytes(buf);
    }


    // ------------------------------------------------------------------------
    // Builder

    public static StargateClassicRendererStateBuilder builder() {
        return new StargateClassicRendererStateBuilder();
    }

    public static class StargateClassicRendererStateBuilder extends StargateAbstractRendererStateBuilder {

        public StargateClassicRendererStateBuilder() {
        }

        protected SymbolType<?> symbolType;

        // Biome override
        public BiomeOverlayInstance biomeOverride;

        //Iris
        public EnumIrisState irisState;
        public EnumIrisType irisType;
        public String irisCode;
        public EnumIrisMode irisMode;
        public long irisAnimation;

        public StargateClassicRendererStateBuilder(StargateAbstractRendererStateBuilder superBuilder) {
            setStargateState(superBuilder.stargateState);
        }

        public StargateClassicRendererStateBuilder setSymbolType(SymbolType<?> symbolType) {
            this.symbolType = symbolType;
            return this;
        }

        public StargateClassicRendererStateBuilder setBiomeOverride(BiomeOverlayInstance biomeOverride) {
            this.biomeOverride = biomeOverride;
            return this;
        }

        public StargateClassicRendererStateBuilder setIrisState(EnumIrisState irisState) {
            this.irisState = irisState;
            return this;
        }

        public StargateClassicRendererStateBuilder setIrisType(EnumIrisType irisType) {
            this.irisType = irisType;
            return this;
        }

        public StargateClassicRendererStateBuilder setIrisCode(String code) {
            this.irisCode = code;
            return this;
        }

        public StargateClassicRendererStateBuilder setIrisMode(EnumIrisMode mode) {
            this.irisMode = mode;
            return this;
        }

        public StargateClassicRendererStateBuilder setIrisAnimation(long irisAnimation) {
            this.irisAnimation = irisAnimation;
            return this;
        }
    }
}
