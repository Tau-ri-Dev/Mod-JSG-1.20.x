package dev.tauri.jsg.common.blockentity.stargate;

import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.sound.StargateSoundPositionedEnum;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargatePegasusRendererState;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.common.multistructure.mergehelper.StargatePegasusMergeHelper;
import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.common.registry.JSGPositionedSounds;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.registry.tags.JSGBlockTags;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateAbstractDialingManager;
import dev.tauri.jsg.common.stargate.manager.dialing.StargatePegasusDialingManager;
import dev.tauri.jsg.common.stargate.manager.state.StargateAbstractStateManager;
import dev.tauri.jsg.common.stargate.manager.state.StargatePegasusStateManager;
import dev.tauri.jsg.core.common.blockentity.ILinkable;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.sound.PositionedSound;
import dev.tauri.jsg.core.common.sound.SoundEvent;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StargatePegasusBaseBE extends StargateClassicBaseBE<StargatePegasusRendererState> implements ILinkable<DHDAbstractBE> {
    public StargatePegasusBaseBE(BlockPos pos, BlockState state) {
        super(JSGBlockEntities.STARGATE_PEGASUS_BASE_BE.get(), pos, state);
    }

    @Override
    public StargateAbstractStateManager<StargatePegasusBaseBE, StargatePegasusRendererState> createStateManager() {
        return new StargatePegasusStateManager(this);
    }

    @Override
    public StargateAbstractDialingManager<StargatePegasusBaseBE> createDialingManager() {
        return new StargatePegasusDialingManager(this);
    }

    @Override
    public void generateMergeHelper() {
        // setup merge helper - client and server
        mergeHelper = new StargatePegasusMergeHelper(this);
        mergeHelper.horizontalFacing = getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY);
        mergeHelper.verticalFacing = dev.tauri.jsg.core.common.blockstate.JSGProperties.getDirectionByVerticalFacing(getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY));
        mergeHelper.basePos = getBlockPos();
    }

    // ------------------------------------------------------------------------
    // Stargate Network

    @Override
    public SymbolType<?> getSymbolType() {
        return JSGSymbolTypes.PEGASUS.get();
    }

    @Override
    public StargateType<?> getStargateType() {
        return StargateTypes.PEGASUS.get();
    }


    // ------------------------------------------------------------------------
    // Sounds

    @Override
    public PositionedSound getPositionedSound(StargateSoundPositionedEnum soundEnum) {
        return switch (soundEnum) {
            case GATE_RING_ROLL -> JSGPositionedSounds.PEGASUS_RING_ROLL;
            case GATE_RING_ROLL_START -> JSGPositionedSounds.PEGASUS_RING_ROLL_START;
        };
    }

    @Override
    @Nullable
    public SoundEvent getSoundEvent(StargateSoundEventEnum soundEnum) {
        return switch (soundEnum) {
            case OPEN -> JSGSoundEvents.GATE_PEGASUS_OPEN;
            case OPEN_NOX -> JSGSoundEvents.GATE_NOX_OPEN;
            case CLOSE -> JSGSoundEvents.GATE_MILKYWAY_CLOSE;
            case DIAL_FAILED -> JSGSoundEvents.GATE_PEGASUS_DIAL_FAILED;
            case INCOMING -> JSGSoundEvents.GATE_PEGASUS_INCOMING;
            case RING_STOP, CHEVRON_OPEN, CHEVRON_SHUT -> JSGSoundEvents.GATE_PEGASUS_CHEVRON_OPEN;
            default -> null;
        };

    }

    public static final List<Supplier<BiomeOverlayInstance>> SUPPORTED_OVERLAYS = List.of(CoreBiomeOverlays.NORMAL, CoreBiomeOverlays.FROST, CoreBiomeOverlays.MOSSY, CoreBiomeOverlays.AGED, CoreBiomeOverlays.SOOTY);

    @Override
    public List<Supplier<BiomeOverlayInstance>> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }

    @Override
    protected TagKey<Block> getLinkableTag() {
        return JSGBlockTags.STARGATE_PEGASUS_LINKABLE_BLOCKS;
    }
}
