package dev.tauri.jsg.screen.inventory.dialhomedevice;

import dev.tauri.jsg.api.stargate.dialhomedevice.DHDReactorStateEnum;
import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;

public class DHDContainerGuiUpdate extends State {
    public DHDContainerGuiUpdate() {
    }

    public int fluidAmount;
    public int tankCapacity;
    public DHDReactorStateEnum reactorState;
    public boolean isLinked;

    public DHDContainerGuiUpdate(int fluidAmount, int tankCapacity, DHDReactorStateEnum reactorState, boolean isLinked) {
        this.fluidAmount = fluidAmount;
        this.tankCapacity = tankCapacity;
        this.reactorState = reactorState;
        this.isLinked = isLinked;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(fluidAmount);
        buf.writeInt(tankCapacity);
        buf.writeInt(reactorState.ordinal());
        buf.writeBoolean(isLinked);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        fluidAmount = buf.readInt();
        tankCapacity = buf.readInt();
        reactorState = DHDReactorStateEnum.values()[buf.readInt()];
        isLinked = buf.readBoolean();
    }

}
