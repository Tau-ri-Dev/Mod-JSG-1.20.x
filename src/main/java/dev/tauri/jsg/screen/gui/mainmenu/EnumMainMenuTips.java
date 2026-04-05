package dev.tauri.jsg.screen.gui.mainmenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public enum EnumMainMenuTips {
    KAWOOSH(0, "menu.tip.kawoosh"),
    UNSTABLE_EH(1, "menu.tip.unstable_eh"),
    ABYDOS_CARTOUCHE(2, "menu.tip.abydos_cartouche");

    public final String[] text;
    public final int id;

    EnumMainMenuTips(int id, @Nullable String... text) {
        this.id = id;
        this.text = text;
    }

    @Nonnull
    public static EnumMainMenuTips byId(int id) {
        for (EnumMainMenuTips t : EnumMainMenuTips.values()) {
            if (t.id == id) return t;
        }
        return EnumMainMenuTips.values()[0];
    }

    @Nonnull
    public static EnumMainMenuTips random(@Nullable EnumMainMenuTips previousTip) {
        int length = EnumMainMenuTips.values().length;
        EnumMainMenuTips newTip;
        do {
            newTip = EnumMainMenuTips.values()[new Random().nextInt(length)];
        } while (previousTip != null && newTip.id == previousTip.id && length > 1);
        return newTip;
    }
}
