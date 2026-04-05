package dev.tauri.jsg.renderer.stargate;

import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;

public class StargateAbstractRendererState extends State {

    public StargateAbstractRendererState() {
    }

    protected StargateAbstractRendererState(StargateAbstractRendererStateBuilder builder) {
        if (builder.stargateState.engaged() || builder.stargateState == EnumStargateState.UNSTABLE_OPENING) {
            doEventHorizonRender = true;
            vortexState = StargateAbstractRenderer.EnumVortexState.STILL;
        }
    }

    public StargateAbstractRendererState initClient(BlockPos pos) {
        this.pos = pos;

        return this;
    }

    // Global
    // Not saved
    public BlockPos pos;
    private BiomeOverlayInstance biomeOverlay = CoreBiomeOverlays.NORMAL.get();

    // Gate
    // Saved
    public boolean doEventHorizonRender = false;
    public StargateAbstractRenderer.EnumVortexState vortexState = StargateAbstractRenderer.EnumVortexState.FORMING;

    // Event horizon
    // Not saved
    public StargateRendererStatic.QuadStrip backStrip;
    public StargateRendererStatic.QuadStrip frontStrip;
    public boolean backStripClamp;
    public boolean frontStripClamp;
    public Float whiteOverlayAlpha;
    public long gateWaitStart = 0;
    public long gateWaitClose = 0;
    public boolean zeroAlphaSet;
    public boolean horizonUnstable = false;

    public boolean noxDialing = false;
    public float backVortexDepth = 0;

    public void openGate(long totalWorldTime, boolean noxDialing) {
        gateWaitStart = totalWorldTime;

        zeroAlphaSet = false;
        backStripClamp = true;
        frontStripClamp = true;
        whiteOverlayAlpha = 1.0f;

        vortexState = StargateAbstractRenderer.EnumVortexState.FORMING;
        doEventHorizonRender = true;

        this.noxDialing = noxDialing;
    }

    public void updateBackVortex(float backVortexDepth) {
        this.backVortexDepth = backVortexDepth;
    }

    public void closeGate(long totalWorldTime) {
        gateWaitClose = totalWorldTime;
        vortexState = StargateAbstractRenderer.EnumVortexState.CLOSING;
    }

    public BiomeOverlayInstance getBiomeOverlay() {
        return biomeOverlay;
    }

    public void setBiomeOverlay(BiomeOverlayInstance biomeOverlay) {
        this.biomeOverlay = biomeOverlay;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(doEventHorizonRender);
        buf.writeInt(vortexState.index);
        buf.writeFloat(backVortexDepth);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        doEventHorizonRender = buf.readBoolean();
        vortexState = StargateAbstractRenderer.EnumVortexState.valueOf(buf.readInt());
        backVortexDepth = buf.readFloat();
    }


    // ------------------------------------------------------------------------
    // Builder

    public static StargateAbstractRendererStateBuilder builder() {
        return new StargateAbstractRendererStateBuilder();
    }

    public static class StargateAbstractRendererStateBuilder {

        // Gate
        protected EnumStargateState stargateState;

        public StargateAbstractRendererStateBuilder setStargateState(EnumStargateState stargateState) {
            this.stargateState = stargateState;
            return this;
        }

        public StargateAbstractRendererState build() {
            return new StargateAbstractRendererState(this);
        }
    }
}
