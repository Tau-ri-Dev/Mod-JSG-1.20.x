package dev.tauri.jsg.common.state.stargate;

import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.common.stargate.manager.StargateConnection;
import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public class StargateContainerGuiUpdate extends State {
    public StargateContainerGuiUpdate(StargateConnection stargateConnection) {
        this.stargateConnection = stargateConnection;
    }

    public int energyStored;
    public int transferredLastTick;
    public double secondsToClose;
    public EnumIrisMode irisMode;
    public String irisCode;
    public StargateConnection stargateConnection;
    public double gateTemp;
    public double irisTemp;
    public int pageProgress;

    public StargateContainerGuiUpdate(int energyStored, int transferredLastTick, double secondsToClose, EnumIrisMode irisMode, String irisCode, StargateConnection stargateConnection, double gateTemp, double irisTemp, int pageProgress) {
        this.energyStored = energyStored;
        this.transferredLastTick = transferredLastTick;
        this.secondsToClose = secondsToClose;
        this.irisMode = irisMode;
        this.irisCode = irisCode;
        this.stargateConnection = stargateConnection;
        this.gateTemp = gateTemp;
        this.irisTemp = irisTemp;
        this.pageProgress = pageProgress;
    }

    @Override
    public void toBytes(ByteBuf buff) {
        var buf = new FriendlyByteBuf(buff);
        buf.writeInt(energyStored);
        buf.writeInt(transferredLastTick);
        buf.writeDouble(secondsToClose);
        buf.writeByte(irisMode.id);
        buf.writeUtf(irisCode);
        stargateConnection.toBytes(new FriendlyByteBuf(buf));
        buf.writeDouble(gateTemp);
        buf.writeDouble(irisTemp);
        buf.writeInt(pageProgress);
    }

    @Override
    public void fromBytes(ByteBuf buff) {
        var buf = new FriendlyByteBuf(buff);
        energyStored = buf.readInt();
        transferredLastTick = buf.readInt();
        secondsToClose = buf.readDouble();
        irisMode = EnumIrisMode.getValue(buf.readByte());
        irisCode = buf.readUtf();
        stargateConnection.fromBytes(new FriendlyByteBuf(buf));
        gateTemp = buf.readDouble();
        irisTemp = buf.readDouble();
        pageProgress = buf.readInt();
    }
}
