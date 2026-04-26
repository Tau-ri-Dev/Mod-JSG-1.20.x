package dev.tauri.jsg.common.state.stargate;

import dev.tauri.jsg.common.stargate.manager.StargateSoundManager;
import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;

public class StargateSoundUpdateState extends State {
    public StargateSoundManager<?> soundManager;

    public StargateSoundUpdateState(StargateSoundManager<?> soundManager) {
        this.soundManager = soundManager;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        soundManager.toBytes(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        soundManager.fromBytes(buf);
    }
}
