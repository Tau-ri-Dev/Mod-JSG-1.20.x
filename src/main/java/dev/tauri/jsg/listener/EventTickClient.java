package dev.tauri.jsg.listener;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.JSGApi;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


@OnlyIn(Dist.CLIENT)
public class EventTickClient {
    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (event.player != null) {
                Player player = mc.player;
                if (player != null) {
                    Vec3 v = player.position();
                    JSG.lastPlayerPosInWorld = new BlockPos((int) v.x, (int) v.y, (int) v.z);
                    JSGApi.lastPlayerPosInWorld = JSG.lastPlayerPosInWorld;
                }
            }
        }
    }
}
