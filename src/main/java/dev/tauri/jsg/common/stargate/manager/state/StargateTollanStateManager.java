package dev.tauri.jsg.common.stargate.manager.state;

import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateTollanRendererState;
import dev.tauri.jsg.common.blockentity.stargate.StargateTollanBaseBE;

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
