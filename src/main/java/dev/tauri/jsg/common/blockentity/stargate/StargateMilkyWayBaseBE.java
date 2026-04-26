package dev.tauri.jsg.common.blockentity.stargate;

import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.sound.StargateSoundPositionedEnum;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.core.common.blockentity.ILinkable;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.sound.PositionedSound;
import dev.tauri.jsg.core.common.sound.SoundEvent;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.common.multistructure.mergehelper.StargateMilkyWayMergeHelper;
import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.common.registry.JSGPositionedSounds;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.registry.tags.JSGBlockTags;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateMilkyWayRendererState;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateAbstractDialingManager;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateMilkyWayDialingManager;
import dev.tauri.jsg.common.stargate.manager.state.StargateAbstractStateManager;
import dev.tauri.jsg.common.stargate.manager.state.StargateMilkyWayStateManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StargateMilkyWayBaseBE extends StargateClassicBaseBE<StargateMilkyWayRendererState> implements ILinkable<DHDAbstractBE> {
    public StargateMilkyWayBaseBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    public StargateMilkyWayBaseBE(BlockPos pos, BlockState state) {
        this(JSGBlockEntities.STARGATE_MILKYWAY_BASE_BE.get(), pos, state);
    }

    @Override
    public StargateAbstractStateManager<StargateMilkyWayBaseBE, StargateMilkyWayRendererState> createStateManager() {
        return new StargateMilkyWayStateManager(this);
    }

    @Override
    public StargateAbstractDialingManager<?> createDialingManager() {
        return new StargateMilkyWayDialingManager(this);
    }

    @Override
    public void generateMergeHelper() {
        // setup merge helper - client and server
        mergeHelper = new StargateMilkyWayMergeHelper(this);
        mergeHelper.horizontalFacing = getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY);
        mergeHelper.verticalFacing = dev.tauri.jsg.core.common.blockstate.JSGProperties.getDirectionByVerticalFacing(getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY));
        mergeHelper.basePos = getBlockPos();
    }

    @Override
    protected TagKey<Block> getLinkableTag() {
        return JSGBlockTags.STARGATE_MILKYWAY_LINKABLE_BLOCKS;
    }

    // ------------------------------------------------------------------------
    // Stargate Network

    @Override
    public SymbolType<?> getSymbolType() {
        return JSGSymbolTypes.MILKYWAY.get();
    }

    @Override
    public StargateType<?> getStargateType() {
        return StargateTypes.MILKYWAY.get();
    }

    // ------------------------------------------------------------------------
    // Sounds

    @Override
    public PositionedSound getPositionedSound(StargateSoundPositionedEnum soundEnum) {
        return switch (soundEnum) {
            case GATE_RING_ROLL -> JSGPositionedSounds.MILKYWAY_RING_ROLL;
            case GATE_RING_ROLL_START -> JSGPositionedSounds.MILKYWAY_RING_ROLL_START;
        };
    }

    @Override
    @Nullable
    public SoundEvent getSoundEvent(StargateSoundEventEnum soundEnum) {
        return switch (soundEnum) {
            case OPEN -> JSGSoundEvents.GATE_MILKYWAY_OPEN;
            case OPEN_NOX -> JSGSoundEvents.GATE_NOX_OPEN;
            case CLOSE -> JSGSoundEvents.GATE_MILKYWAY_CLOSE;
            case DIAL_FAILED ->
                    getDialingManager().getStargateState().dialingComputer() ? JSGSoundEvents.GATE_MILKYWAY_DIAL_FAILED_COMPUTER : JSGSoundEvents.GATE_MILKYWAY_DIAL_FAILED;
            case INCOMING -> JSGSoundEvents.GATE_MILKYWAY_INCOMING;
            case CHEVRON_OPEN -> JSGSoundEvents.GATE_MILKYWAY_CHEVRON_OPEN;
            case CHEVRON_SHUT, RING_STOP -> JSGSoundEvents.GATE_MILKYWAY_CHEVRON_SHUT;
            default -> null;
        };
    }

    @Override
    public float getSoundEventPitch(StargateSoundEventEnum soundEvent) {
        if (soundEvent == StargateSoundEventEnum.CHEVRON_OPEN)
            return 0.95f + (0.05f * (getLevel() != null ? getLevel().random.nextFloat() : 0f));
        return super.getSoundEventPitch(soundEvent);
    }

    // ------------------------------------------------------------------------
    // Ticking and loading

    public static final List<Supplier<BiomeOverlayInstance>> SUPPORTED_OVERLAYS = List.of(CoreBiomeOverlays.NORMAL, CoreBiomeOverlays.FROST, CoreBiomeOverlays.MOSSY, CoreBiomeOverlays.AGED, CoreBiomeOverlays.SOOTY);

    @Override
    public List<Supplier<BiomeOverlayInstance>> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }
}
