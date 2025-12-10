package dev.tauri.jsg.api.config.ingame;


import dev.tauri.jsg.api.state.State;
import dev.tauri.jsg.api.state.StateType;

/**
 * Specifies that TileEntity is configuratable trough GUI
 */
public interface ITileConfig {
    JSGTileEntityConfig getConfig();

    void setConfig(JSGTileEntityConfig config);

    void setConfigAndUpdate(JSGTileEntityConfig config);

    State getState(StateType stateType);
}
