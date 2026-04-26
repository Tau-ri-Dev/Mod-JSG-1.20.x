package dev.tauri.jsg.client.renderer.blockentity.stargate;

public class StargatePegasusRendererState extends StargateClassicRendererState {
    public StargatePegasusRendererState() {
    }

    private StargatePegasusRendererState(StargatePegasusRendererStateBuilder builder) {
        super(builder);
    }


    // ------------------------------------------------------------------------
    // Builder

    public static StargatePegasusRendererStateBuilder builder() {
        return new StargatePegasusRendererStateBuilder();
    }

    public static class StargatePegasusRendererStateBuilder extends StargateClassicRendererState.StargateClassicRendererStateBuilder {
        public StargatePegasusRendererStateBuilder() {
        }

        public StargatePegasusRendererStateBuilder(StargateClassicRendererStateBuilder superBuilder) {
            super(superBuilder);
            setSymbolType(superBuilder.symbolType);
            setBiomeOverride(superBuilder.biomeOverride);
            setIrisState(superBuilder.irisState);
            setIrisType(superBuilder.irisType);
            setIrisAnimation(superBuilder.irisAnimation);
        }

        @Override
        public StargatePegasusRendererState build() {
            return new StargatePegasusRendererState(this);
        }
    }
}