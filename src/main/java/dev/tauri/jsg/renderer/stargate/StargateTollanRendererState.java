package dev.tauri.jsg.renderer.stargate;

public class StargateTollanRendererState extends StargateMilkyWayRendererState {
    public StargateTollanRendererState() {
        super();
    }

    public StargateTollanRendererState(StargateTollanRendererStateBuilder stargateTollanRendererStateBuilder) {
        super(stargateTollanRendererStateBuilder);
    }

    // ------------------------------------------------------------------------
    // Builder

    public static StargateTollanRendererStateBuilder builder() {
        return new StargateTollanRendererStateBuilder();
    }

    public static class StargateTollanRendererStateBuilder extends StargateMilkyWayRendererStateBuilder {
        public StargateTollanRendererStateBuilder() {
        }

        public StargateTollanRendererStateBuilder(StargateClassicRendererStateBuilder superBuilder) {
            super(superBuilder);
        }

        @Override
        public StargateTollanRendererState build() {
            return new StargateTollanRendererState(this);
        }
    }
}
