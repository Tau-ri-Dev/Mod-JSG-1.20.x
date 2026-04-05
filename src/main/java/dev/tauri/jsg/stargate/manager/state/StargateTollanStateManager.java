package dev.tauri.jsg.stargate.manager.state;

import dev.tauri.jsg.blockentity.stargate.StargateTollanBaseBE;
import dev.tauri.jsg.renderer.stargate.StargateTollanRendererState;

public class StargateTollanStateManager extends StargateMilkyWayStateManager {

    public StargateTollanStateManager(StargateTollanBaseBE stargate) {
        super(stargate);
    }

    @Override
    protected StargateTollanRendererState.StargateTollanRendererStateBuilder getRendererStateServer() {
        return (StargateTollanRendererState.StargateTollanRendererStateBuilder) new StargateTollanRendererState.StargateTollanRendererStateBuilder(super.getRendererStateServer());
    }

    @Override
    protected StargateTollanRendererState createRendererStateClient() {
        return new StargateTollanRendererState();
    }
}
