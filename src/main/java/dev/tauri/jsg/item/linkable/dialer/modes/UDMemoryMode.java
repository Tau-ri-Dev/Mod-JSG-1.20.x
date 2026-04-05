package dev.tauri.jsg.item.linkable.dialer.modes;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.blockentity.stargate.StargateUniverseBaseBE;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import dev.tauri.jsg.registry.JSGSoundEvents;
import dev.tauri.jsg.registry.tags.JSGBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class UDMemoryMode extends UDNearbyMode {
    public UDMemoryMode() {
        super(JSG.rl("memory"), "item.jsg.universe_dialer.mode_saved", JSGBlockTags.DIALER_MEMORY_LINKABLE, (level, pos) -> {
            var be = level.getBlockEntity(pos);
            return (be instanceof StargateUniverseBaseBE uniBE && uniBE.isMerged());
        });
    }

    @Override
    public void onLinkUpdated(boolean isLinked, BlockPos linkedPos, CompoundTag compound, ItemStack stack, ServerLevel world, Entity entity) {
        if (isLinked && entity instanceof ServerPlayer sp)
            JSGSoundHelper.playSoundToPlayer(sp, JSGSoundEvents.UNIVERSE_DIALER_CONNECTED, entity.blockPosition());
    }

    @Override
    public boolean onUse(CompoundTag compound, ItemStack stack, Level world, Player player, InteractionHand hand, boolean shift) {
        if (shift) {
            if (world.isClientSide) return false;
            //if (!(player instanceof ServerPlayer serverPlayer)) return false;
            byte selectedEntry = compound.getByte(C_SELECTED);
            var entries = compound.getList(C_ENTRIES, Tag.TAG_COMPOUND);
            if (selectedEntry >= entries.size())
                return false;

            //var selectedCompound = entries.getCompound(selectedEntry);
            entries.remove(selectedEntry);
            compound.put(C_ENTRIES, entries);
            if (selectedEntry >= entries.size())
                selectedEntry--;
            compound.putByte(C_SELECTED, selectedEntry);
            stack.setTag(compound);

            /*

            Give page to the player with removed address
            -- commented out, because the address in the dialer is only virtual

            var symbolsToDisplay = selectedCompound.getIntArray(C_E_SYMBOLS);
            var address = new StargateAddress(selectedCompound);
            var originId = selectedCompound.getInt("originId");

            var pageCompound = PageNotebookItemFilled.getCompoundFromAddress(address, symbolsToDisplay, PageNotebookItemFilled.getRegistryPathFromWorld(null, null), originId, NotebookPageSerialization.STARGATES);
            var pageStack = new ItemStack(CoreItems.NOTEBOOK_PAGE_FILLED.get(), 1);
            pageStack.setTag(pageCompound);
            serverPlayer.addItem(pageStack);*/
            return true;
        }
        return super.onUse(compound, stack, world, player, hand, false);
    }

    public void addEntry(StargateAddress address, int[] symbolsToDisplay, int originId, ItemStack stack) {
        var tag = stack.getOrCreateTag();
        var modeTag = getTag(tag);
        var list = modeTag.getList(C_ENTRIES, CompoundTag.TAG_COMPOUND);

        var addressTag = address.serializeNBT();
        addressTag.putIntArray(C_E_SYMBOLS, symbolsToDisplay);
        addressTag.putInt("originId", originId);

        list.add(addressTag);
        modeTag.put(C_ENTRIES, list);
        stack.setTag(tag);
    }
}
