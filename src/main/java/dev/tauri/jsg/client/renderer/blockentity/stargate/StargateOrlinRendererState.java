package dev.tauri.jsg.client.renderer.blockentity.stargate;

public class StargateOrlinRendererState extends StargateAbstractRendererState {
    public StargateOrlinRendererState() {
    }

    public StargateOrlinRendererState(StargateOrlinRendererStateBuilder builder) {
        super(builder);
    }

    // ------------------------------------------------------------------------
    // Builder

    public static StargateOrlinRendererState.StargateOrlinRendererStateBuilder builder() {
        return new StargateOrlinRendererState.StargateOrlinRendererStateBuilder();
    }

    public static class StargateOrlinRendererStateBuilder extends StargateAbstractRendererStateBuilder {
        @Override
        public StargateOrlinRendererState build() {
            return new StargateOrlinRendererState(this);
        }
    }
}
