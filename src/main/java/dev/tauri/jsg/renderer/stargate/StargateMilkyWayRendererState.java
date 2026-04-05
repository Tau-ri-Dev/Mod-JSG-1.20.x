package dev.tauri.jsg.renderer.stargate;

public class StargateMilkyWayRendererState extends StargateClassicRendererState {
    public StargateMilkyWayRendererState() {
    }

    protected StargateMilkyWayRendererState(StargateMilkyWayRendererStateBuilder builder) {
        super(builder);
    }


    // ------------------------------------------------------------------------
    // Builder

    public static StargateMilkyWayRendererStateBuilder builder() {
        return new StargateMilkyWayRendererStateBuilder();
    }

    public static class StargateMilkyWayRendererStateBuilder extends StargateClassicRendererStateBuilder {
        public StargateMilkyWayRendererStateBuilder() {
        }

        public StargateMilkyWayRendererStateBuilder(StargateClassicRendererStateBuilder superBuilder) {
            super(superBuilder);
            setSymbolType(superBuilder.symbolType);
            setBiomeOverride(superBuilder.biomeOverride);
            setIrisState(superBuilder.irisState);
            setIrisType(superBuilder.irisType);
            setIrisCode(superBuilder.irisCode);
            setIrisMode(superBuilder.irisMode);
            setIrisAnimation(superBuilder.irisAnimation);
        }

        @Override
        public StargateMilkyWayRendererState build() {
            return new StargateMilkyWayRendererState(this);
        }
    }
}