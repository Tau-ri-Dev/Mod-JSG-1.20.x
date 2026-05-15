package dev.tauri.jsg.common.blockentity.dialhomedevice;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.client.screen.util.DHDScreenHelper;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolMilkyWayEnum;
import dev.tauri.jsg.api.stargate.result.StargateCloseResult;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.client.renderer.blockentity.dialhomedevice.DHDMilkyWayRendererState;
import dev.tauri.jsg.common.advancements.JSGCriterions;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.common.helpers.StargateLinkingHelper;
import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.registry.tags.JSGBlockTags;
import dev.tauri.jsg.common.state.dialhomedevice.DHDActivateButtonState;
import dev.tauri.jsg.core.common.blockentity.ILinkable;
import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.helper.LinkingHelper;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import dev.tauri.jsg.core.common.state.BiomeOverrideState;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DHDMilkyWayBE extends DHDAbstractBE {
    public static final List<Supplier<BiomeOverlayInstance>> SUPPORTED_OVERLAYS = List.of(CoreBiomeOverlays.NORMAL,
            CoreBiomeOverlays.FROST, CoreBiomeOverlays.MOSSY, CoreBiomeOverlays.SOOTY, CoreBiomeOverlays.AGED);

    public DHDMilkyWayBE(BlockPos pos, BlockState state) {
        super(JSGBlockEntities.DHD_MILKYWAY.get(), pos, state);
    }

    @Override
    public List<Supplier<BiomeOverlayInstance>> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }

    @SuppressWarnings("null")
    @Override
    public void tick(@NotNull Level level) {
        if (level.isClientSide) {
            // Client

            // Each 2s check for the sky
            if (level.getGameTime() % 40 == 0 && rendererStateClient != null
                    && getRendererStateClient().biomeOverride == null) {
                rendererStateClient.setBiomeOverlay(BiomeOverlayInstance.getUpdatedBiomeOverlay(level, getBlockPos(), SUPPORTED_OVERLAYS.stream().map(Supplier::get).toList()));
            }
        }
        super.tick(level);
    }

    @Override
    public DHDScreenHelper getScreenHelper() {
        return DHDScreenHelper.getMilkyWay();
    }

    @Override
    public SymbolType<SymbolMilkyWayEnum> getSymbolType() {
        return JSGSymbolTypes.MILKYWAY.get();
    }

    public Item getControlCrystal() {
        return JSGItems.CRYSTAL_CONTROL_MILKYWAY_DHD.get();
    }

    public void activateSymbol(SymbolInterface symbolInt) {
        if (level == null) return;
        var gateTile = getLinkedDevice();
        if (gateTile == null) return;

        SymbolMilkyWayEnum symbol = getSymbolType().valueOf(symbolInt.getId());

        // When using OC to dial, don't play sound of the DHD button press
        if (!gateTile.getDialingManager().getStargateState().dialingComputer() || ((StargateClassicBaseBE<?>) gateTile).getConfig().getValueOrDefault(StargateConfigOptions.Classic.DHD_OC_PRESS_SOUND)) {
            if (symbol.brb())
                JSGSoundHelper.playSoundEvent(level, getBlockPos(), JSGSoundEvents.DHD_MILKYWAY_PRESS_BRB);
            else
                JSGSoundHelper.playSoundEvent(level, getBlockPos(), JSGSoundEvents.DHD_MILKYWAY_PRESS);
        }

        level.blockEntityChanged(getBlockPos());

        sendState(JSGStateTypes.DHD_ACTIVATE_BUTTON.get(), new DHDActivateButtonState(symbol));
    }

    @Override
    public void pushSymbolButton(SymbolInterface symbol, @Nullable ServerPlayer player, boolean force) {
        if (getCapability(ForgeCapabilities.ITEM_HANDLER, null).resolve().orElseThrow().getStackInSlot(0).isEmpty()) {
            if (player != null)
                player.sendSystemMessage(Component.translatable("block.jsg.dhd_milkyway.no_crystal_warn"), true);
            return;
        }

        if (!isLinked()) {
            if (player != null)
                player.sendSystemMessage(Component.translatable("block.jsg.dhd_milkyway.not_linked_warn"), true);
            return;
        }

        var gateTile = (StargateClassicBaseBE<?>) getLinkedDevice();
        var gateState = Objects.requireNonNull(gateTile).getDialingManager().getStargateState();

        if (gateState.engaged() && symbol.brb()) {
            // Gate is open, BRB was press, possible closure attempt

            if (gateState.initiating())
                gateTile.getDialingManager().attemptClose(StargateClosedReasonEnum.REQUESTED);
            else if (player != null) {
                StargateComputerEvents.ATTEMPT_CLOSE_FAILED.apply(StargateCloseResult.NOT_INITIATING, gateTile.getDialingManager().getDialedAddress(), false).sendVia(gateTile);
                player.sendSystemMessage(Component.translatable("block.jsg.dhd_milkyway.incoming_wormhole_warn"), true);
            }
        } else if (gateState.idle()) {
            // Gate is idle, some glyph was pressed

            if (symbol.brb()) {
                // BRB pressed on idling gate, attempt to open

                StargateOpenResult openResult = gateTile.getDialingManager().attemptOpenDialed();

                if (openResult.ok())
                    JSGCriterions.CHEVRON_SEVEN_LOCKED.trigger(player);

                if (openResult == StargateOpenResult.NOT_ENOUGH_POWER && player != null) {
                    player.sendSystemMessage(Component.translatable("block.jsg.dhd_milkyway.not_enough_power"), true);
                }
            } else {
                // Not BRB, some other glyph pressed on idling gate, we can add this symbol now
                var r = gateTile.getDialingManager().engageSymbolDHD(symbol, false, false);
                JSG.logger.debug(r.name());
            }
        }
    }

    // --------------------------------
    // LINKING

    @Override
    public void updateLinkStatus(Level world, BlockPos pos) {
        if (isLinked() && getLinkedDevice() instanceof ILinkable<?> linkable) {
            linkable.setLinkedDevice(null);
            setLinkedDevice(null);
        }
        BlockPos closestGate = LinkingHelper.findClosestUnlinked(world, pos, StargateLinkingHelper.getDhdRange(), JSGBlockTags.DHD_MILKYWAY_LINKABLE_BLOCKS);

        if (closestGate != null) {
            ILinkable<?> gateTile = (ILinkable<?>) world.getBlockEntity(closestGate);
            if (gateTile != null) {
                gateTile.setLinkedDevice(pos);
                setLinkedDevice(closestGate);
            }
        }
    }

    // --------------------------------
    // STATES

    @Override
    public State getState(StateType stateType) {
        if (stateType == CoreStateTypes.RENDERER_STATE.get()) {
            StargateAddressDynamic address = new StargateAddressDynamic(getSymbolType());
            var config = new BEConfig();

            if (isLinked()) {
                var gateTile = getLinkedDevice();

                if (gateTile != null) {

                    address.addAll(gateTile.getDialingManager().getDialedAddress());
                    boolean brbActive = false;

                    switch (gateTile.getDialingManager().getStargateState()) {
                        case ENGAGED_INITIATING:
                            brbActive = true;
                            break;

                        case ENGAGED:
                            address.clear();
                            brbActive = true;
                            break;

                        default:
                            break;
                    }

                    if (gateTile instanceof StargateClassicBaseBE)
                        config = ((StargateClassicBaseBE<?>) gateTile).getConfig();

                    return new DHDMilkyWayRendererState(address, brbActive, determineBiomeOverride(), !gateTile.getDialingManager().getConnection().getStatus().none(), config);
                }
            }

            return new DHDMilkyWayRendererState(address, false, determineBiomeOverride(), false, config);
        }
        throw new UnsupportedOperationException(
                "EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
    }

    @Override
    public State createState(StateType stateType) {
        if (stateType == CoreStateTypes.RENDERER_STATE.get()) {
            return new DHDMilkyWayRendererState();
        }
        return super.createState(stateType);
    }

    @SuppressWarnings("null")
    @Override
    public void setState(StateType stateType, State state) {
        if (level == null)
            return;

        boolean connected;
        if (isLinked()) {
            var gateTile = getLinkedDevice();
            if (gateTile != null)
                connected = !gateTile.getDialingManager().getConnection().getStatus().none();
            else {
                connected = false;
            }
        } else {
            connected = false;
        }

        stateType.stateExecutor()
                .tryType(CoreStateTypes.RENDERER_STATE.get(), () -> rendererStateClient = ((DHDMilkyWayRendererState) state).initClient(getBlockPos(), BiomeOverlayInstance.getUpdatedBiomeOverlay(level, getBlockPos(), SUPPORTED_OVERLAYS.stream().map(Supplier::get).toList()), connected))
                .tryType(JSGStateTypes.DHD_ACTIVATE_BUTTON.get(), () -> {
                    if (state != null) {
                        DHDActivateButtonState activateState = (DHDActivateButtonState) state;

                        getRendererStateClient().setIsConnected(connected);

                        if (activateState.clearAll)
                            ((DHDMilkyWayRendererState) getRendererStateClient()).clearSymbols(level.getGameTime());
                        else
                            ((DHDMilkyWayRendererState) getRendererStateClient()).activateSymbol(level.getGameTime(), getSymbolType().valueOf(activateState.symbol));
                    }
                })
                .tryType(CoreStateTypes.BIOME_OVERRIDE_STATE.get(), () -> {
                    BiomeOverrideState overrideState = (BiomeOverrideState) state;

                    if (rendererStateClient != null) {
                        getRendererStateClient().biomeOverride = overrideState.biomeOverride;
                    }
                })
                .run();
        super.setState(stateType, state);
    }
}
