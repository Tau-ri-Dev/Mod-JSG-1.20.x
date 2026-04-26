package dev.tauri.jsg.client.renderer.activation;

import dev.tauri.jsg.core.common.symbol.SymbolInterface;

public class DHDActivation extends dev.tauri.jsg.core.client.renderer.Activation<SymbolInterface> {

    public DHDActivation(SymbolInterface textureKey, long stateChange, boolean dim) {
        super(textureKey, stateChange, dim);
    }

    @Override
    protected float getMaxStage() {
        return 5;
    }

    @Override
    protected float getTickMultiplier() {
        return (textureKey.origin() && !dim) ? 1 : 2;
    }
}
