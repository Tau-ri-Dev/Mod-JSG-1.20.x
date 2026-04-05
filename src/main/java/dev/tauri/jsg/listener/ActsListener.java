package dev.tauri.jsg.listener;


import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.event.StargateReceiveTravelerEvent;
import dev.tauri.jsg.blockentity.stargate.StargatePegasusBaseBE;
import dev.tauri.jsg.registry.JSGDimensions;
import dev.tauri.jsg.screen.gui.mainmenu.MainMenuTheme;
import dev.tauri.jsg.stargate.teleportation.traveler.PlayerTraveler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JSG.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ActsListener {
    @SubscribeEvent
    public static void onDimChanged(EntityTravelToDimensionEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            if (e.getDimension() == Level.END)
                MainMenuTheme.sendUpdateActToClient(p, MainMenuTheme.ACT_6);
            if (e.getDimension() == JSGDimensions.ABYDOS)
                MainMenuTheme.sendUpdateActToClient(p, MainMenuTheme.ACT_2);
            if (e.getDimension() == Level.OVERWORLD)
                MainMenuTheme.sendUpdateActToClient(p, MainMenuTheme.ACT_1);
        }
    }

    @SubscribeEvent
    public static void onGateTeleport(StargateReceiveTravelerEvent e) {
        var s = e.getTile();
        var t = e.getTargetTile();
        if ((s instanceof StargatePegasusBaseBE || t instanceof StargatePegasusBaseBE) && e.getTraveler() instanceof PlayerTraveler playerTraveler) {
            MainMenuTheme.sendUpdateActToClient(playerTraveler.get(), MainMenuTheme.ACT_3);
        }
    }
}
