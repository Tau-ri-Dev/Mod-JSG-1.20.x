package dev.tauri.jsg.common.item.linkable.dialer.modes;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.animation.EnumDialingType;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolUniverseEnum;
import dev.tauri.jsg.client.renderer.item.dialer.IUniverseDialerScreen;
import dev.tauri.jsg.client.renderer.item.dialer.screen.UDManualDialScreen;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateUniverseBaseBE;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerClientActionEnum;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerMode;
import dev.tauri.jsg.common.packet.packets.linkable.UniverseDialerActionPacketToServer;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.registry.tags.JSGBlockTags;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.ParametersAreNonnullByDefault;

import static dev.tauri.jsg.common.item.linkable.dialer.modes.UDNearbyMode.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class UDManualDialMode extends UniverseDialerMode {
    public static final String C_SELECTED_GLYPH = "selectedGlyph";
    public static final String C_EDIT_ADDRESS = "address";

    public UDManualDialMode() {
        super(JSGApi.rl("manual_dialing"), "item.jsg.universe_dialer.manual_dialing", JSGBlockTags.DIALER_NEARBY_LINKABLE, (level, pos) -> (level.getBlockEntity(pos) instanceof StargateUniverseBaseBE uniBE && uniBE.isMerged()));
    }

    private final UDManualDialScreen screen = new UDManualDialScreen();

    @Override
    public IUniverseDialerScreen getScreen() {
        return screen;
    }

    @Override
    public void inventoryTick(ItemStack stack, CompoundTag compound, Level world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, compound, world, entity, itemSlot, isSelected);

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
    public void handlePacketToServer(UniverseDialerClientActionEnum action, CompoundTag compound, UniverseDialerActionPacketToServer packet, NetworkEvent.Context ctx) {
        var player = ctx.getSender();
        if (player == null) return;
        if (action == UniverseDialerClientActionEnum.ADDRESS_CHANGE) {
            int symbolOffset = compound.getInt(C_SELECTED_GLYPH);
            if (packet.next)
                symbolOffset--;
            else
                symbolOffset++;
            compound.putInt(C_SELECTED_GLYPH, symbolOffset);
            JSGSoundHelper.playSoundToPlayer(player, JSGSoundEvents.UNIVERSE_DIALER_MODE_CHANGE, player.blockPosition());
        }
    }

    @Override
    public boolean onUse(CompoundTag compound, ItemStack stack, Level world, Player player, InteractionHand hand, boolean shift) {
        if (world.isClientSide) return false;
        var addressTag = compound.getCompound(C_EDIT_ADDRESS);
        var address = new StargateAddressDynamic(JSGSymbolTypes.UNIVERSE.get());
        address.deserializeNBT(addressTag);

        var serverPlayer = (ServerPlayer) player;
        if (shift) {
            var linkedPos = BlockPos.of(compound.getLong(C_LINKED_POS));
            var linkedTe = world.getBlockEntity(linkedPos);
            if (!(linkedTe instanceof StargateUniverseBaseBE gateTile) || !gateTile.isMerged()) {
                JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_ERROR, player.blockPosition());
                return false;
            }
            switch (gateTile.getDialingManager().getStargateState()) {
                case IDLE:
                    if (gateTile.getDialingManager().canAbortDialing() && gateTile.getDialingManager().abortDialingSequence()) {
                        serverPlayer.displayClientMessage(Component.translatable("item.jsg.universe_dialer.aborting"), true);
                        JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_START_DIAL, player.blockPosition());
                        break;
                    }
                    address = address.addOriginIfMissingAndImmutable();
                    var fastDial = gateTile.getConfig().getValueOrDefault(StargateConfigOptions.Universe.FAST_DIALING);
                    gateTile.getDialingManager().dialAddress(address, false, false, fastDial ? EnumDialingType.FAST : EnumDialingType.REMOTE);
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

        int symbolOffset = compound.getInt(C_SELECTED_GLYPH);
        var symbolId = symbolOffset % SymbolUniverseEnum.values().length;
        while (symbolId < 0) {
            symbolId += SymbolUniverseEnum.values().length;
        }
        if (symbolId == 0) {
            if (address.popLast() == null) {
                JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_ERROR, player.blockPosition());
                return false;
            }
            compound.put(C_EDIT_ADDRESS, address.serializeNBT());
            JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_MODE_CHANGE, player.blockPosition());
            return true;
        }

        var symbol = JSGSymbolTypes.UNIVERSE.get().valueOf(symbolId);

        if (address.size() >= 9 || (address.size() > 0 && address.getLast().origin()) || (symbol.origin() && address.size() < 6)) {
            JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_ERROR, player.blockPosition());
            return false;
        }
        address.addSymbol(symbol);
        compound.put(C_EDIT_ADDRESS, address.serializeNBT());
        JSGSoundHelper.playSoundToPlayer(serverPlayer, JSGSoundEvents.UNIVERSE_DIALER_MODE_CHANGE, player.blockPosition());
        return true;
    }
}
