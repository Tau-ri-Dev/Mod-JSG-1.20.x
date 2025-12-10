package dev.tauri.jsg.api.packet;

import dev.tauri.jsg.api.JSGApi;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.function.Function;

public class SimplePacketHandler {
    public final String networkVersion;
    public final SimpleChannel channelInstance;
    public final ResourceLocation channelName;

    public SimplePacketHandler(ResourceLocation channel, String version) {
        networkVersion = version;
        channelName = channel;
        channelInstance = NetworkRegistry.ChannelBuilder.named(channel)
                .clientAcceptedVersions((v) -> Objects.equals(v, networkVersion))
                .serverAcceptedVersions((v) -> Objects.equals(v, networkVersion))
                .networkProtocolVersion(() -> networkVersion)
                .simpleChannel();
    }

    public void sendToServer(Object packet) {
        channelInstance.send(PacketDistributor.SERVER.noArg(), packet);
    }

    public void sendToClient(Object packet, PacketDistributor.TargetPoint point) {
        if (point == null) return;
        channelInstance.send(PacketDistributor.NEAR.with(() -> point), packet);
    }

    public void sendTo(Object packet, ServerPlayer player) {
        if (player == null) return;
        channelInstance.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public <MSG extends JSGPacket> void registerPacket(Class<MSG> clazz, int id, NetworkDirection direction, Function<FriendlyByteBuf, MSG> decoder) {
        try {
            channelInstance.messageBuilder(clazz, id, direction)
                    .encoder(JSGPacket::toBytes)
                    .decoder(decoder)
                    .consumerNetworkThread(JSGPacket::handleSupplier)
                    .add();
        } catch (Exception e) {
            JSGApi.logger.error("Could not register packet {} for channel {}: ", id, channelName, e);
        }
    }
}
