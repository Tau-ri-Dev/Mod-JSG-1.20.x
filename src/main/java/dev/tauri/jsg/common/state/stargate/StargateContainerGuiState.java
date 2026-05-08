package dev.tauri.jsg.common.state.stargate;

import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;

public class StargateContainerGuiState extends State {
    public StargateContainerGuiState(BEConfig config) {
        this.config = config;
    }

    public Map<SymbolType<?>, StargateAddress> gateAdddressMap = new HashMap<>();
    public BEConfig config;

    public StargateContainerGuiState(Map<SymbolType<?>, StargateAddress> gateAdddressMap, BEConfig config) {
        this.gateAdddressMap = gateAdddressMap;
        this.config = config;
    }

    @Override
    public void toBytes(ByteBuf buff) {
        var buf = new FriendlyByteBuf(buff);
        for (SymbolType<?> symbolType : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
            gateAdddressMap.get(symbolType).toBytes(buf);
        }

        config.toBytes(buf);
    }

    @Override
    public void fromBytes(ByteBuf buff) {
        var buf = new FriendlyByteBuf(buff);
        gateAdddressMap = new HashMap<>(3);

        for (SymbolType<?> symbolType : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
            StargateAddress address = new StargateAddress(symbolType);
            address.fromBytes(buf);
            gateAdddressMap.put(symbolType, address);
        }

        config.fromBytes(buf);
    }
}
