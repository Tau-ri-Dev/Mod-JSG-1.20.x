package dev.tauri.jsg.blockentity.stargate;

import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.sound.StargateSoundPositionedEnum;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.common.sound.PositionedSound;
import dev.tauri.jsg.core.common.sound.SoundEvent;
import dev.tauri.jsg.multistructure.mergehelper.StargateMovieMergeHelper;
import dev.tauri.jsg.registry.JSGBlockEntities;
import dev.tauri.jsg.registry.JSGPositionedSounds;
import dev.tauri.jsg.registry.JSGSoundEvents;
import dev.tauri.jsg.renderer.stargate.StargateMilkyWayRendererState;
import dev.tauri.jsg.stargate.manager.dialing.StargateAbstractDialingManager;
import dev.tauri.jsg.stargate.manager.dialing.StargateMovieDialingManager;
import dev.tauri.jsg.stargate.manager.state.StargateAbstractStateManager;
import dev.tauri.jsg.stargate.manager.state.StargateMovieStateManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StargateMovieBaseBE extends StargateMilkyWayBaseBE {
    public StargateMovieBaseBE(BlockPos pos, BlockState state) {
        super(JSGBlockEntities.STARGATE_MOVIE_BASE_BE.get(), pos, state);
    }

    @Override
    public StargateAbstractStateManager<StargateMilkyWayBaseBE, StargateMilkyWayRendererState> createStateManager() {
        return new StargateMovieStateManager(this);
    }

    @Override
    public StargateAbstractDialingManager<?> createDialingManager() {
        return new StargateMovieDialingManager(this);
    }


    @Override
    public StargateType<?> getStargateType() {
        return StargateTypes.MOVIE.get();
    }

    @Override
    public void generateMergeHelper() {
        // setup merge helper - client and server
        mergeHelper = new StargateMovieMergeHelper(this);
        mergeHelper.horizontalFacing = getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY);
        mergeHelper.verticalFacing = dev.tauri.jsg.core.common.blockstate.JSGProperties.getDirectionByVerticalFacing(getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY));
        mergeHelper.basePos = getBlockPos();
    }

    @Override
    public PositionedSound getPositionedSound(StargateSoundPositionedEnum soundEnum) {
        return switch (soundEnum) {
            case GATE_RING_ROLL -> JSGPositionedSounds.MOVIE_RING_ROLL;
            case GATE_RING_ROLL_START -> JSGPositionedSounds.MOVIE_RING_ROLL_START;
        };
    }

    @Override
    @Nullable
    public SoundEvent getSoundEvent(StargateSoundEventEnum soundEnum) {
        return switch (soundEnum) {
            case OPEN -> JSGSoundEvents.GATE_MOVIE_OPEN;
            case OPEN_NOX -> JSGSoundEvents.GATE_NOX_OPEN;
            case CLOSE -> JSGSoundEvents.GATE_MOVIE_CLOSE;
            case DIAL_FAILED ->
                    getDialingManager().getStargateState().dialingComputer() ? JSGSoundEvents.GATE_MILKYWAY_DIAL_FAILED_COMPUTER : JSGSoundEvents.GATE_MILKYWAY_DIAL_FAILED;
            case INCOMING, CHEVRON_OPEN -> JSGSoundEvents.GATE_MOVIE_CHEVRON_OPEN;
            case RING_STOP -> JSGSoundEvents.GATE_MOVIE_RING_ROLL_STOP;
            case CHEVRON_SHUT -> JSGSoundEvents.GATE_MOVIE_CHEVRON_CLOSE;
            default -> null;
        };
    }

    @Override
    public int getCloseSoundDelay() {
        return 30;
    }
}
