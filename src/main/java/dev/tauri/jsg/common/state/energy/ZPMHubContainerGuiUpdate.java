package dev.tauri.jsg.common.state.energy;

import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;

public class ZPMHubContainerGuiUpdate extends State {
    public ZPMHubContainerGuiUpdate() {
    }

    public long energyStored;
    public long energyTransferredLastTick;

    public ZPMHubContainerGuiUpdate(long energyStored, long energyTransferredLastTick) {
        this.energyStored = energyStored;
        this.energyTransferredLastTick = energyTransferredLastTick;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(energyStored);
        buf.writeLong(energyTransferredLastTick);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        energyStored = buf.readLong();
        energyTransferredLastTick = buf.readLong();
    }
}
