package dev.tauri.jsg.effect;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.packet.JSGPacketHandler;
import dev.tauri.jsg.packet.packets.effect.StargateWormholeEffectToClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;

/**
 * SERVER SIDE
 */
@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
public class StargateWormholeHandler {
    public static final Map<UUID, Long> WORMHOLE_ANIMATION_END_MAP = new HashMap<>();
    public static final Map<UUID, PlaceEntityInterface> PLAYER_PLACER_INTERFACES = new HashMap<>();

    public interface PlaceEntityInterface {
        void place(Consumer<ServerPlayer> afterPlace);
    }

    public static void handle(ServerPlayer sp, PlaceEntityInterface placer, StargateWormholeType type, int length) {
        if (!JSGConfig.Stargate.enableTravelAnimation.get()) {
            placer.place((newSp) -> {
            });
            return;
        }

        var time = System.currentTimeMillis();
        WORMHOLE_ANIMATION_END_MAP.put(sp.getUUID(), (time + (length * 1000L)));
        PLAYER_PLACER_INTERFACES.put(sp.getUUID(), placer);
        JSGPacketHandler.sendTo(new StargateWormholeEffectToClient(false, type, length), sp);
        sp.setInvisible(true);
        sp.setInvulnerable(true);
        sp.noPhysics = true;
        sp.getAbilities().invulnerable = true;
        sp.getAbilities().mayBuild = false;
        sp.onUpdateAbilities();
        sp.setInvisible(true);

        //((ServerLevel) sp.level()).removePlayerImmediately(sp, Entity.RemovalReason.DISCARDED);
        //sp.server.getPlayerList().remove(sp);
    }

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.side.isClient()) return;
        var server = event.level.getServer();
        if (server == null) return;
        List<UUID> toDelete = new ArrayList<>();
        for (var t : WORMHOLE_ANIMATION_END_MAP.entrySet()) {
            if (System.currentTimeMillis() >= t.getValue()) {
                toDelete.add(t.getKey());
            }
        }
        for (var k : toDelete)
            release(server, k);
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        if (sp.getServer() == null) return;
        release(sp.getServer(), sp);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        if (sp.getServer() == null) return;
        release(sp.getServer(), sp);
    }

    @ParametersAreNonnullByDefault
    public static void release(MinecraftServer server, ServerPlayer sp) {
        release(server, sp.getUUID());
    }

    public static void release(MinecraftServer server, UUID uuid) {
        if (!WORMHOLE_ANIMATION_END_MAP.containsKey(uuid)) return;
        WORMHOLE_ANIMATION_END_MAP.remove(uuid);
        ServerPlayer sp = server.getPlayerList().getPlayer(uuid);
        if (sp == null) return;
        PLAYER_PLACER_INTERFACES.get(uuid).place((newSp) -> {
            JSGPacketHandler.sendTo(new StargateWormholeEffectToClient(true, StargateWormholeType.MILKYWAY, 0), newSp);
            newSp.setInvisible(false);
            newSp.setInvulnerable(false);
            newSp.noPhysics = false;
            newSp.getAbilities().invulnerable = false;
            newSp.getAbilities().mayBuild = true;
            newSp.gameMode.getGameModeForPlayer().updatePlayerAbilities(newSp.getAbilities());
            newSp.onUpdateAbilities();

            //server.getPlayerList().placeNewPlayer(newSp.connection.connection, newSp);
            //((ServerLevel) newSp.level()).addNewPlayer(newSp);
        });
    }
}
