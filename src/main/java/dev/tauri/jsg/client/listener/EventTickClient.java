package dev.tauri.jsg.client.listener;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;


@OnlyIn(Dist.CLIENT)
public class EventTickClient {
    @SubscribeEvent
    public void tick(PlayerTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.getEntity() != null) {
            Player player = mc.player;
            if (player != null) {
                Vec3 v = player.position();
                JSG.lastPlayerPosInWorld = new BlockPos((int) v.x, (int) v.y, (int) v.z);
                JSGApi.lastPlayerPosInWorld = JSG.lastPlayerPosInWorld;
            }
        }
    }
}
