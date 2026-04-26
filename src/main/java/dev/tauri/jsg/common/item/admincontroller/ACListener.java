package dev.tauri.jsg.common.item.admincontroller;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.manager.IStargateStateManager;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.common.packet.JSGPacketHandler;
import dev.tauri.jsg.common.packet.packets.admincontroller.event.ACStargateEngageSymbolPacketToClient;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;

public class ACListener {
    public static void sendChevronEngaged(IStargateStateManager manager, StargatePos stargate, SymbolInterface symbolInterface, ChevronEnum chevron) {
        JSGPacketHandler.sendToClient(new ACStargateEngageSymbolPacketToClient(stargate, symbolInterface, chevron), manager.getTargetPoint());
    }
}
