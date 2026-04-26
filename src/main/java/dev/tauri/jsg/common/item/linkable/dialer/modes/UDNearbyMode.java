package dev.tauri.jsg.common.item.linkable.dialer.modes;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.stargate.NearbyGate;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.animation.EnumDialingType;
import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.client.renderer.item.dialer.IUniverseDialerScreen;
import dev.tauri.jsg.client.renderer.item.dialer.screen.UDEntriesScreen;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateUniverseBaseBE;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerClientActionEnum;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerMode;
import dev.tauri.jsg.common.packet.packets.linkable.UniverseDialerActionPacketToServer;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.registry.tags.JSGBlockTags;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
public class UDNearbyMode extends UniverseDialerMode {
    public static final String C_SELECTED = "selected";
    public static final String C_ENTRIES = "entries";
    public static final String C_STATUS = "gateStatus";
    public static final String C_DIALED = "dialedAddress";
    public static final String C_TO_DIAL = "toDialAddress";

    public static final String C_E_NAME = "name";
    public static final String C_E_SYMBOLS = "symbolsToDisplay";


    public UDNearbyMode() {
        super(JSG.rl("nearby"), "item.jsg.universe_dialer.mode_scan", JSGBlockTags.DIALER_NEARBY_LINKABLE, (level, pos) -> (level.getBlockEntity(pos) instanceof StargateUniverseBaseBE uniBE && uniBE.isMerged()));
    }

    public UDNearbyMode(ResourceLocation id, String title, @Nullable TagKey<Block> matchBlocks, BiPredicate<Level, BlockPos> linkMatchTest) {
        super(id, title, matchBlocks, linkMatchTest);
    }

    private final UDEntriesScreen screen = new UDEntriesScreen();

    @Override
    public @NotNull IUniverseDialerScreen getScreen() {
        return screen;
    }

    public void inventoryTick(ItemStack stack, CompoundTag compound, Level world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, compound, world, entity, itemSlot, isSelected);
        if (!compound.contains(C_SELECTED) || compound.getByte(C_SELECTED) < 0) {
            compound.putByte(C_SELECTED, (byte) 0);
        }
        if (compound.getByte(C_SELECTED) > 0 && compound.getByte(C_SELECTED) >= compound.getList(C_ENTRIES, Tag.TAG_COMPOUND).size()) {
            compound.putByte(C_SELECTED, (byte) (compound.getList(C_ENTRIES, Tag.TAG_COMPOUND).size() - 1));
        }

        if (!compound.contains(C_LINKED_POS)) return;
        var linkedPos = BlockPos.of(compound.getLong(C_LINKED_POS));
        var gateTile = (StargateAbstractBaseBE<?, ?>) world.getBlockEntity(linkedPos);
        if (gateTile == null) return;

        addrToBytes(gateTile.getDialingManager().getDialedAddress(), compound, C_DIALED);
        gateTile.getDialingManager().getDialingSequence().ifPresentOrElse(
                s -> addrToBytes((StargateAddressDynamic) s.getOriginalAddress(), compound, C_TO_DIAL),
                () -> {
                    if (gateTile.getDialingManager().getDialedAddressSize() > 0)
                        addrToBytes(gateTile.getDialingManager().getDialedAddress(), compound, C_TO_DIAL);
                    else
                        addrToBytes(new StargateAddressDynamic(gateTile.getSymbolType()), compound, C_TO_DIAL);
                }
        );
        compound.putInt(C_STATUS, gateTile.getDialingManager().getStargateState().ordinal());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onLinkUpdated(boolean isLinked, BlockPos linkedPos, CompoundTag compound, ItemStack stack, ServerLevel world, Entity entity) {
        super.onLinkUpdated(isLinked, linkedPos, compound, stack, world, entity);
        var nearbyList = new ListTag();

        if (!isLinked) {
            compound.put(C_ENTRIES, nearbyList);
            return;
        }
        var gateTile = (StargateClassicBaseBE<?>) world.getBlockEntity(linkedPos);
        if (gateTile == null) return;

        var nearbyGates = gateTile.getNearbyGates(StargateTypes.UNIVERSE.get(), true, false);
        for (NearbyGate gate : nearbyGates) {
            var entryCompound = gate.address.serializeNBT();
            entryCompound.putIntArray(C_E_SYMBOLS, gate.getSymbolsToDisplay());

            nearbyList.add(entryCompound);
        }
        compound.put(C_ENTRIES, nearbyList);
    }

    @Override
    public void handlePacketToServer(UniverseDialerClientActionEnum action, CompoundTag compound, UniverseDialerActionPacketToServer packet, NetworkEvent.Context ctx) {
        if (action != UniverseDialerClientActionEnum.ADDRESS_CHANGE) return;
        var selected = compound.getByte(C_SELECTED);
        var addressCount = compound.getList(C_ENTRIES, Tag.TAG_COMPOUND).size();
        var player = ctx.getSender();
        if (player == null) return;

        if (packet.next && selected < addressCount - 1) { // message.offset < 0
            compound.putByte("selected", (byte) (selected + 1));
            JSGSoundHelper.playSoundToPlayer(player, JSGSoundEvents.UNIVERSE_DIALER_MODE_CHANGE, player.blockPosition());
            return;
        }

        if (!packet.next && selected > 0) {
            compound.putByte("selected", (byte) (selected - 1));
            JSGSoundHelper.playSoundToPlayer(player, JSGSoundEvents.UNIVERSE_DIALER_MODE_CHANGE, player.blockPosition());
        }
    }

    @Override
    public void keyPressed(CompoundTag compound, ItemStack stack, Level world, Player player, InteractionHand hand, char keyCode, boolean backspace, boolean shift, boolean alt, boolean ctrl) {
        int selectedEntry = compound.getByte(C_SELECTED);
        var entries = compound.getList(C_ENTRIES, Tag.TAG_COMPOUND);
        if (selectedEntry >= entries.size())
            return;
        var selectedCompound = entries.getCompound(selectedEntry);
        var name = selectedCompound.getString(UDNearbyMode.C_E_NAME);
        if (name.isEmpty() && backspace) return;
        if (backspace) name = name.substring(0, name.length() - 1);
        else name += Character.toString(keyCode);
        selectedCompound.putString(UDNearbyMode.C_E_NAME, name);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onUse(CompoundTag compound, ItemStack stack, Level world, Player player, InteractionHand hand, boolean shift) {
        if (world.isClientSide) return false;
        if (!(player instanceof ServerPlayer serverPlayer)) return false;

        int selectedEntry = compound.getByte(C_SELECTED);
        var linkedPos = BlockPos.of(compound.getLong(C_LINKED_POS));
        var entries = compound.getList(C_ENTRIES, Tag.TAG_COMPOUND);
        var linkedTe = world.getBlockEntity(linkedPos);
        if (!(linkedTe instanceof StargateUniverseBaseBE gateTile) || !gateTile.isMerged()) return false;

        if (shift) {
            if (gateTile.getIrisManager().hasIris() && gateTile.getIrisManager().getIrisMode() == EnumIrisMode.DIALER) {
                gateTile.getIrisManager().toggleIris();
                return true;
            }
        }

        if (selectedEntry >= entries.size())
            return false;

        var selectedCompound = entries.getCompound(selectedEntry);

        switch (gateTile.getDialingManager().getStargateState()) {
            case IDLE:
                if (gateTile.getDialingManager().canAbortDialing() && gateTile.getDialingManager().abortDialingSequence()) {
                    serverPlayer.displayClientMessage(Component.translatable("item.jsg.universe_dialer.aborting"), true);
                    JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_START_DIAL, player.blockPosition());
                    break;
                }
                var symbolsToDisplay = selectedCompound.getIntArray(C_E_SYMBOLS);
                var address = new StargateAddress(selectedCompound);
                var symbolsToDial = new ArrayList<SymbolInterface>();
                for (var s : symbolsToDisplay) {
                    if (s == 9) continue;
                    symbolsToDial.add(address.get(s - 1));
                }
                var addressToDial = new StargateAddressDynamic(address.getSymbolType());
                addressToDial.addAll(symbolsToDial);
                addressToDial = addressToDial.addOriginIfMissingAndImmutable();
                var fastDial = gateTile.getConfig().getValueOrDefault(StargateConfigOptions.Universe.FAST_DIALING);
                gateTile.getDialingManager().dialAddress(addressToDial, false, false, fastDial ? EnumDialingType.FAST : EnumDialingType.REMOTE);
                serverPlayer.displayClientMessage(Component.translatable("item.jsg.universe_dialer.dial_start"), true);
                JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_START_DIAL, player.blockPosition());
                break;

            case ENGAGED_INITIATING:
                gateTile.getDialingManager().attemptClose(StargateClosedReasonEnum.REQUESTED);
                JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_START_DIAL, player.blockPosition());
                break;

            case ENGAGED:
                serverPlayer.displayClientMessage(Component.translatable("tile.jsg.dhd_block.incoming_wormhole_warn"), true);
                JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_ERROR, player.blockPosition());
                break;

            default:
                if (gateTile.getDialingManager().abortDialingSequence()) {
                    serverPlayer.displayClientMessage(Component.translatable("item.jsg.universe_dialer.aborting"), true);
                    JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_START_DIAL, player.blockPosition());
                    break;
                }
                serverPlayer.displayClientMessage(Component.translatable("item.jsg.universe_dialer.gate_busy"), true);
                JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_ERROR, player.blockPosition());
                break;
        }
        return true;
    }
}
