package dev.tauri.jsg.item.admincontroller;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.manager.IStargateStateManager;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.packet.JSGPacketHandler;
import dev.tauri.jsg.packet.packets.admincontroller.event.ACStargateEngageSymbolPacketToClient;

public class ACListener {
    public static void sendChevronEngaged(IStargateStateManager manager, StargatePos stargate, SymbolInterface symbolInterface, ChevronEnum chevron) {
        JSGPacketHandler.sendToClient(new ACStargateEngageSymbolPacketToClient(stargate, symbolInterface, chevron), manager.getTargetPoint());
    }
}
