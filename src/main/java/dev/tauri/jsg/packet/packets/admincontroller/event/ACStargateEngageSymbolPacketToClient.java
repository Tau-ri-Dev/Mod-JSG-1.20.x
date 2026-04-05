package dev.tauri.jsg.packet.packets.admincontroller.event;

import dev.tauri.jsg.api.stargate.ChevronEnum;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.packet.packets.admincontroller.ACPacketToClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.ParametersAreNonnullByDefault;

public class ACStargateEngageSymbolPacketToClient extends ACPacketToClient {
    public StargatePos stargate;
    public SymbolInterface symbolInterface;
    public ChevronEnum chevron;

    @ParametersAreNonnullByDefault
    public ACStargateEngageSymbolPacketToClient(StargatePos stargate, SymbolInterface symbolInterface, ChevronEnum chevron) {
        this.stargate = stargate;
        this.symbolInterface = symbolInterface;
        this.chevron = chevron;
    }

    public ACStargateEngageSymbolPacketToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        stargate.toBytes(buf);
        buf.writeResourceLocation(symbolInterface.getSymbolType().getId());
        buf.writeInt(symbolInterface.getId());
        buf.writeInt(chevron.ordinal());
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        stargate = new StargatePos(buf);
        var symbolType = SymbolType.byId(buf.readResourceLocation());
        symbolInterface = symbolType.valueOf(buf.readInt());
        chevron = ChevronEnum.values()[buf.readInt()];
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> getGUI().ifPresent(gui -> gui.handleChevronEngageEvent(this)));
    }
}
