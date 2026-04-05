package dev.tauri.jsg.renderer.stargate;

public class StargateUniverseRendererState extends StargateClassicRendererState {
    public StargateUniverseRendererState() {
    }

    public StargateUniverseRendererState(StargateUniverseRendererStateBuilder builder) {
        super(builder);
    }


    // ------------------------------------------------------------------------
    // Builder

    public static StargateUniverseRendererStateBuilder builder() {
        return new StargateUniverseRendererStateBuilder();
    }

    public static class StargateUniverseRendererStateBuilder extends StargateClassicRendererStateBuilder {
        public StargateUniverseRendererStateBuilder() {
        }

        public StargateUniverseRendererStateBuilder(StargateClassicRendererStateBuilder superBuilder) {
            super(superBuilder);
            setSymbolType(superBuilder.symbolType);
            setBiomeOverride(superBuilder.biomeOverride);
            setIrisState(superBuilder.irisState);
            setIrisType(superBuilder.irisType);
            setIrisAnimation(superBuilder.irisAnimation);
        }

        @Override
        public StargateAbstractRendererState build() {
            return new StargateUniverseRendererState(this);
        }
    }
}
