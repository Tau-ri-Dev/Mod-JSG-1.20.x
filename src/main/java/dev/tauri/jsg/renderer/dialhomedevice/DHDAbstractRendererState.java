package dev.tauri.jsg.renderer.dialhomedevice;

import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public abstract class DHDAbstractRendererState extends State {
    public DHDAbstractRendererState() {
    }

    public DHDAbstractRendererState(StargateAddressDynamic addressDialed, boolean brbActive, BiomeOverlayInstance biomeOverride, boolean stargateIsConnected, BEConfig gateConfig) {
        this.addressDialed = addressDialed;
        this.brbActive = brbActive;
        this.biomeOverride = biomeOverride;
        this.stargateIsConnected = stargateIsConnected;
    }

    public DHDAbstractRendererState initClient(BlockPos pos, BiomeOverlayInstance biomeOverlay, boolean stargateIsConnected) {
        this.pos = pos;
        this.biomeOverlay = biomeOverlay;
        this.stargateIsConnected = stargateIsConnected;
        return this;
    }

    public void setIsConnected(boolean connected) {
        stargateIsConnected = connected;
    }

    public BlockPos pos;
    protected BiomeOverlayInstance biomeOverlay;
    public boolean stargateIsConnected;
    public StargateAddressDynamic addressDialed;
    public boolean brbActive;
    public BiomeOverlayInstance biomeOverride;

    public BiomeOverlayInstance getBiomeOverlay() {
        if (biomeOverride != null)
            return biomeOverride;

        return biomeOverlay;
    }

    public void setBiomeOverlay(BiomeOverlayInstance biomeOverlay) {
        this.biomeOverlay = biomeOverlay;
    }

    public abstract void iterate(Level world, double partialTicks);

    public abstract boolean isButtonActive(SymbolInterface symbol);

    public abstract int getActivatedButtons();

    public abstract SymbolType<?> getSymbolType();


    public void toBytes(ByteBuf buf) {
        addressDialed.toBytes(buf);
        buf.writeBoolean(brbActive);

        if (biomeOverride != null) {
            buf.writeBoolean(true);
            new FriendlyByteBuf(buf).writeResourceLocation(biomeOverride.getId());
        } else {
            buf.writeBoolean(false);
        }

        buf.writeBoolean(stargateIsConnected);
    }

    public void fromBytes(ByteBuf buf) {
        addressDialed = new StargateAddressDynamic(getSymbolType());
        addressDialed.fromBytes(buf);
        brbActive = buf.readBoolean();

        if (buf.readBoolean()) {
            biomeOverride = BiomeOverlayInstance.byId(new FriendlyByteBuf(buf).readResourceLocation());
        }
        stargateIsConnected = buf.readBoolean();
    }
}