package dev.tauri.jsg.item.linkable.dialer.modes.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.api.stargate.EnumStargateState;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import dev.tauri.jsg.item.linkable.dialer.IUniverseDialerScreen;
import dev.tauri.jsg.item.linkable.dialer.modes.UDNearbyMode;
import dev.tauri.jsg.item.linkable.dialer.utils.UDModesRenderUtils;
import dev.tauri.jsg.screen.gui.DialerVirtualGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

public class UDEntriesScreen implements IUniverseDialerScreen {
    @Override
    @ParametersAreNonnullByDefault
    public void render(ItemStack itemStack, CompoundTag compound, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        int selectedEntry = compound.getByte(UDNearbyMode.C_SELECTED);
        var entries = compound.getList(UDNearbyMode.C_ENTRIES, Tag.TAG_COMPOUND);

        for (int offset = -1; offset <= 2; offset++) {
            int index = selectedEntry + offset;
            if (index < 0 || index >= entries.size()) continue;
            boolean active = offset == 0;
            var entryCompound = entries.getCompound(index);

            IUniverseDialerScreen.drawStringWithShadow(stack, bufferSource, -0.1f, -0.5f - 0.2f * offset, (index + 1) + ".", active, false);

            var address = new StargateAddress(entryCompound);
            var symbolsToDisplay = entryCompound.getIntArray(UDNearbyMode.C_E_SYMBOLS);
            EnumStargateState gateStatus = EnumStargateState.valueOf(compound.getInt(UDNearbyMode.C_STATUS));
            var dialedAddress = IUniverseDialerScreen.addrFromBytes(compound, UDNearbyMode.C_DIALED);
            var toDialAddress = IUniverseDialerScreen.addrFromBytes(compound, UDNearbyMode.C_TO_DIAL);
            var name = (entryCompound.contains(UDNearbyMode.C_E_NAME) && !entryCompound.getString(UDNearbyMode.C_E_NAME).isEmpty()) ? entryCompound.getString(UDNearbyMode.C_E_NAME) : null;

            if (Minecraft.getInstance().screen instanceof DialerVirtualGui && active) {
                if (name == null) name = "";
                if (((int) (JSGMinecraftHelper.getPlayerTickClientSide() / 10)) % 2 == 0) {
                    name += "_";
                }
            }

            UDModesRenderUtils.drawAddress(offset, active, stack, bufferSource, light, name, gateStatus, symbolsToDisplay, address, toDialAddress, dialedAddress);
        }
    }
}
