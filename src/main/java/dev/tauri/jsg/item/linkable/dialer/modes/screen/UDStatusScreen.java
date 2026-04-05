package dev.tauri.jsg.item.linkable.dialer.modes.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import dev.tauri.jsg.item.linkable.dialer.IUniverseDialerScreen;
import dev.tauri.jsg.item.linkable.dialer.modes.UDStatusMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

public class UDStatusScreen implements IUniverseDialerScreen {

    private final ArrayList<Integer> switchStates = new ArrayList<>();

    @Override
    @ParametersAreNonnullByDefault
    public void render(ItemStack itemStack, CompoundTag compound, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        var gateStatus = EnumStargateState.valueOf(compound.getInt(UDStatusMode.C_STATUS));
        String[] opened = compound.getString(UDStatusMode.C_OPEN_TIME).split(" ");
        String[] iris = compound.getString(UDStatusMode.C_IRIS).split(" ");
        String[] lastSymbol = compound.getString(UDStatusMode.C_SYMBOL).replaceAll("Glyph ", "G").split(" ");

        float top = -0.3f;
        float row = 0.20f;
        float x = -0.1f;
        float second = -0.9f;

        IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, x, top - (row * 0), "State:", false, false);
        IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, x, top - (row * 1), "Wormhole:", false, false);
        IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, x, top - (row * 2), "Iris:", false, false);
        IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, x, top - (row * 3), "Last glyph:", false, false);

        String[] state = gateStatus.toString()
                .replaceAll("ENGAGED", "ENGAGED INCOMING")
                .replaceAll("ENGAGED INCOMING_INITIATING", "ENGAGED OUTGOING")
                .replaceAll("_", " ").split(" ");

        if (switchStates.size() < 4) {
            switchStates.clear();
            for (int i = 0; i < 4; i++)
                switchStates.add(0);
        }

        for (int u = 0; u < 4; u++) {
            int max = 0;
            switch (u) {
                case 0:
                    max = state.length;
                    break;
                case 1:
                    max = opened.length;
                    break;
                case 2:
                    max = iris.length;
                    break;
                case 3:
                    max = lastSymbol.length;
                    break;
                default:
                    break;
            }
            int current = (int) Math.floor(JSGMinecraftHelper.getClientTick() % (40 * max) / 40D);

            switchStates.set(u, (current));
        }

        if (state.length > switchStates.get(0))
            IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, x + second, top - (row * 0), state[switchStates.get(0)].replaceAll("_", " "), true, false);
        if (opened.length > switchStates.get(1))
            IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, x + second, top - (row * 1), opened[switchStates.get(1)].replaceAll("_", " "), true, false);
        if (iris.length > switchStates.get(2))
            IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, x + second, top - (row * 2), iris[switchStates.get(2)].replaceAll("_", " "), true, false);
        if (lastSymbol.length > switchStates.get(3))
            IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, x + second, top - (row * 3), lastSymbol[switchStates.get(3)].replaceAll("_", " "), true, false);

    }
}
