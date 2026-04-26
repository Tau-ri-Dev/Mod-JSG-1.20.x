package dev.tauri.jsg.common.item.admincontroller;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.result.StargateConnectionStatus;
import dev.tauri.jsg.common.stargate.manager.StargateLogManager;
import dev.tauri.jsg.core.common.blockentity.IBELogManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;

public class ACStargateData {
    public StargatePos stargatePos;

    // data
    public EnumStargateState stargateState;
    public StargateConnectionStatus stargateConnectionStatus;
    public boolean initiatingConnection;
    public StargateAddressDynamic dialedAddress;
    public int energyBuffer;
    public int energyBufferMax;
    public int energyConsumption;
    public double secondsToClose;
    public long secondsOpen;
    public IBELogManager logManager = new StargateLogManager();
    public final Map<ChevronEnum, Boolean> chevronActiveStateMap = new HashMap<>();
    public final Map<ChevronEnum, Boolean> chevronOpenStateMap = new HashMap<>();


    // server
    public ACStargateData(Stargate<?> stargate) {
        this.stargatePos = stargate.getStargatePos();
        this.logManager = stargate.getLogManager();
        var dm = stargate.getDialingManager();
        var sm = stargate.getStateManager();
        var em = stargate.getEnergyManager();
        this.stargateState = dm.getStargateState();
        this.stargateConnectionStatus = dm.getConnection().getStatus();
        this.initiatingConnection = dm.getConnection().isInitiating();
        this.dialedAddress = new StargateAddressDynamic(dm.getDialedAddress());
        this.energyBuffer = em.getStorage().getEnergyStored();
        this.energyBufferMax = em.getStorage().getMaxEnergyStored();
        this.energyConsumption = em.getTransferredLastTick();
        this.secondsToClose = em.getSecondsToClose();
        this.secondsOpen = dm.getConnection().getSecondsOpen();
        for (var chevron : ChevronEnum.values()) {
            var state = sm.getChevronsState().get(chevron);
            chevronActiveStateMap.put(chevron, state.isLocked());
            chevronOpenStateMap.put(chevron, state.isOpen());
        }
    }

    // packet to client
    public ACStargateData(FriendlyByteBuf byteBuf) {
        fromBytes(byteBuf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        stargatePos.toBytes(buf);
        logManager.toBytes(buf);

        buf.writeInt(stargateState.ordinal());
        buf.writeInt(stargateConnectionStatus.ordinal());
        buf.writeBoolean(initiatingConnection);
        dialedAddress.toBytes(buf);
        buf.writeInt(energyBuffer);
        buf.writeInt(energyBufferMax);
        buf.writeInt(energyConsumption);
        buf.writeDouble(secondsToClose);
        buf.writeLong(secondsOpen);
        for (var chevron : ChevronEnum.values()) {
            buf.writeBoolean(chevronActiveStateMap.get(chevron));
            buf.writeBoolean(chevronOpenStateMap.get(chevron));
        }
    }

    public void fromBytes(FriendlyByteBuf buf) {
        stargatePos = new StargatePos(buf);
        logManager.fromBytes(buf);

        stargateState = EnumStargateState.values()[buf.readInt()];
        stargateConnectionStatus = StargateConnectionStatus.values()[buf.readInt()];
        initiatingConnection = buf.readBoolean();
        dialedAddress = new StargateAddressDynamic(buf);
        energyBuffer = buf.readInt();
        energyBufferMax = buf.readInt();
        energyConsumption = buf.readInt();
        secondsToClose = buf.readDouble();
        secondsOpen = buf.readLong();
        chevronActiveStateMap.clear();
        for (var chevron : ChevronEnum.values()) {
            chevronActiveStateMap.put(chevron, buf.readBoolean());
            chevronOpenStateMap.put(chevron, buf.readBoolean());
        }
    }
}
