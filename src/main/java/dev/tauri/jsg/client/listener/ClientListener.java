package dev.tauri.jsg.client.listener;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.client.screen.gui.mainmenu.GuiCustomMainMenu;
import dev.tauri.jsg.client.screen.gui.sggenerator.LevelGenerationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

@EventBusSubscriber(modid = JSG.MOD_ID, value = Dist.CLIENT)
public class ClientListener {

    @SubscribeEvent
    public static void onSounds(PlaySoundEvent event) {
        if (event.getSound() != null && event.getSound().getLocation().toString().equalsIgnoreCase("minecraft:music.menu")) {
            event.setSound(null);
        }
    }

    public static boolean initMainMenu = true;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGuiOpen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof LevelLoadingScreen) {
            event.setNewScreen(new LevelGenerationScreen());
            return;
        }
        if (!JSGConfig.General.disableJSGMainMenu.get()) {
            if (!event.isCanceled() && event.getCurrentScreen() instanceof TitleScreen) {
                event.setNewScreen(new GuiCustomMainMenu());
                if (initMainMenu) {
                    GuiCustomMainMenu.menuDisplayed = -1;
                    initMainMenu = false;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGuiOpen(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof LevelLoadingScreen) {
            Minecraft.getInstance().forceSetScreen(new LevelGenerationScreen());
            return;
        }
        if (!JSGConfig.General.disableJSGMainMenu.get()) {
            if (event.getScreen() instanceof TitleScreen) {
                Minecraft.getInstance().setScreen(new GuiCustomMainMenu());
                if (initMainMenu) {
                    GuiCustomMainMenu.menuDisplayed = -1;
                    initMainMenu = false;
                }
            }
        }
    }
}
