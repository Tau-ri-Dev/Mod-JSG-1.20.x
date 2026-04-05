package dev.tauri.jsg.screen.gui;

import dev.tauri.jsg.packet.JSGPacketHandler;
import dev.tauri.jsg.packet.packets.linkable.UniverseDialerKeyPressedToServer;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DialerVirtualGui extends Screen {
    public DialerVirtualGui() {
        super(Component.literal("Remote Dialer"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (pKeyCode == 259) { // backspace
           JSGPacketHandler.sendToServer(new UniverseDialerKeyPressedToServer(' ', true, Screen.hasShiftDown(), Screen.hasAltDown(), Screen.hasControlDown()));
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (SharedConstants.isAllowedChatCharacter(pCodePoint)) {
            JSGPacketHandler.sendToServer(new UniverseDialerKeyPressedToServer(pCodePoint, false, Screen.hasShiftDown(), Screen.hasAltDown(), Screen.hasControlDown()));
            return true;
        }
        return false;
    }
}
