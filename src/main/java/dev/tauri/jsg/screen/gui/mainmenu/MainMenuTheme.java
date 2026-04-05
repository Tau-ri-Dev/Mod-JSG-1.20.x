package dev.tauri.jsg.screen.gui.mainmenu;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.config.data.ProgressJSON;
import dev.tauri.jsg.core.common.sound.PositionedSound;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.packet.JSGPacketHandler;
import dev.tauri.jsg.packet.packets.ProgressUpdateToClient;
import dev.tauri.jsg.registry.JSGPositionedSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class MainMenuTheme {
    public static final Map<ResourceLocation, MainMenuTheme> THEMES = new HashMap<>();

    public static final MainMenuTheme ACT_1 = registerAct(JSG.MOD_ID, 0, StargateTypes.MILKYWAY.get(), 1, 9, JSGPositionedSounds.MAINMENU_ACT1);
    public static final MainMenuTheme ACT_2 = registerAct(JSG.MOD_ID, 1, StargateTypes.MILKYWAY.get(), 2, 10, JSGPositionedSounds.MAINMENU_ACT2);
    public static final MainMenuTheme ACT_3 = registerAct(JSG.MOD_ID, 2, StargateTypes.PEGASUS.get(), 3, 7, JSGPositionedSounds.MAINMENU_ACT3);
    public static final MainMenuTheme ACT_6 = registerAct(JSG.MOD_ID, 5, StargateTypes.UNIVERSE.get(), 6, 4, JSGPositionedSounds.MAINMENU_ACT6);

    public static MainMenuTheme registerAct(String modId, int priority, @Nullable StargateType<?> gateType, int id, int backCount, PositionedSound sound) {
        return registerAct(modId, priority, gateType, id, backCount, sound, () -> ProgressJSON.get().currentActId.equalsIgnoreCase(modId + ":act" + id));
    }

    public static MainMenuTheme registerAct(String modId, int priority, @Nullable StargateType<?> gateType, int id, int backCount, PositionedSound sound, Supplier<Boolean> predicate) {
        var backs = new ArrayList<ResourceLocation>();
        for (var i = 0; i < backCount; i++) {
            backs.add(JSGMapping.rl(modId, "textures/gui/mainmenu/act" + id + "/" + i + ".jpg"));
        }
        return new MainMenuTheme(JSGMapping.rl(modId, "act" + id), priority, gateType) {
            @Override
            public boolean canBeChosen() {
                return predicate.get();
            }

            @Override
            public List<ResourceLocation> getBackgrounds() {
                return backs;
            }

            @Override
            public PositionedSound getSoundTheme() {
                return sound;
            }
        };
    }

    @ParametersAreNonnullByDefault
    public static void sendUpdateActToClient(ServerPlayer player, MainMenuTheme act) {
        JSGPacketHandler.sendTo(new ProgressUpdateToClient(act.id), player);
    }


    public final ResourceLocation id;
    public final int priority;
    @Nullable
    public final StargateType<?> gateType;

    public MainMenuTheme(ResourceLocation id, int priority, @Nullable StargateType<?> gateType) {
        this.id = id;
        this.priority = priority;
        this.gateType = gateType;
        THEMES.put(id, this);
    }

    public int getPriority() {
        return priority;
    }

    public abstract boolean canBeChosen();

    public abstract List<ResourceLocation> getBackgrounds();

    public abstract PositionedSound getSoundTheme();

    public ResourceLocation getBackground(int id) {
        return getBackgrounds().get(id);
    }

    public static void load() {
    }
}
