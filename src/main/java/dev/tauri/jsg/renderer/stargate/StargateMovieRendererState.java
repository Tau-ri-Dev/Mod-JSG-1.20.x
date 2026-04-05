package dev.tauri.jsg.renderer.stargate;

public class StargateMovieRendererState extends StargateMilkyWayRendererState {
    public StargateMovieRendererState() {
        super();
    }

    public StargateMovieRendererState(StargateMovieRendererStateBuilder stargateTollanRendererStateBuilder) {
        super(stargateTollanRendererStateBuilder);
    }

    // ------------------------------------------------------------------------
    // Builder

    public static StargateMovieRendererStateBuilder builder() {
        return new StargateMovieRendererStateBuilder();
    }

    public static class StargateMovieRendererStateBuilder extends StargateMilkyWayRendererStateBuilder {
        public StargateMovieRendererStateBuilder() {
        }

        public StargateMovieRendererStateBuilder(StargateClassicRendererStateBuilder superBuilder) {
            super(superBuilder);
        }

        @Override
        public StargateMovieRendererState build() {
            return new StargateMovieRendererState(this);
        }
    }
}
