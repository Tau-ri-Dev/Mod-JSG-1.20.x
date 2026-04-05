package dev.tauri.jsg.blockentity.stargate;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.sound.StargateSoundPositionedEnum;
import dev.tauri.jsg.api.stargate.NearbyGate;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.SymbolUniverseEnum;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.helper.DimensionsHelper;
import dev.tauri.jsg.core.common.power.general.EnergyRequiredToOperate;
import dev.tauri.jsg.core.common.power.general.LargeEnergyStorage;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.sound.PositionedSound;
import dev.tauri.jsg.core.common.sound.SoundEvent;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.multistructure.mergehelper.StargateUniverseMergeHelper;
import dev.tauri.jsg.registry.JSGBlockEntities;
import dev.tauri.jsg.registry.JSGPositionedSounds;
import dev.tauri.jsg.registry.JSGSoundEvents;
import dev.tauri.jsg.registry.tags.JSGBlockTags;
import dev.tauri.jsg.renderer.stargate.StargateUniverseRendererState;
import dev.tauri.jsg.stargate.manager.StargateEnergyManager;
import dev.tauri.jsg.stargate.manager.dialing.StargateAbstractDialingManager;
import dev.tauri.jsg.stargate.manager.dialing.StargateUniverseDialingManager;
import dev.tauri.jsg.stargate.manager.state.StargateAbstractStateManager;
import dev.tauri.jsg.stargate.manager.state.StargateUniverseStateManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StargateUniverseBaseBE extends StargateClassicBaseBE<StargateUniverseRendererState> {
    protected ResourceKey<Level> fakeWorld;
    protected BlockPos fakePos;

    private static final List<Supplier<BiomeOverlayInstance>> SUPPORTED_OVERLAYS = List.of(
            CoreBiomeOverlays.NORMAL,
            CoreBiomeOverlays.FROST,
            CoreBiomeOverlays.MOSSY,
            CoreBiomeOverlays.AGED
    );


    public StargateUniverseBaseBE(BlockPos pos, BlockState state) {
        super(JSGBlockEntities.STARGATE_UNIVERSE_BASE_BE.get(), pos, state);
    }

    @Override
    public StargateEnergyManager<?, LargeEnergyStorage> createEnergyManager() {
        return new StargateEnergyManager<>(this) {
            @Override
            public LargeEnergyStorage getStorage() {
                return energyStorage;
            }

            @Override
            public EnergyRequiredToOperate getEnergyRequiredToDial(@Nullable StargatePos targetGatePos, StargateAddressDynamic address) {
                return super.getEnergyRequiredToDial(targetGatePos, address).mul(JSGConfig.Stargate.stargateUniverseEnergyMul.get());
            }

            @Override
            protected Level getLevel() {
                return Optional.ofNullable((Level) DimensionsHelper.getLevel(stargate.getFakeWorld())).orElse(super.getLevel());
            }

            @Override
            protected BlockPos getBlockPos() {
                return stargate.getFakePos();
            }
        };
    }

    @Override
    public StargateAbstractStateManager<?, StargateUniverseRendererState> createStateManager() {
        return new StargateUniverseStateManager(this);
    }

    @Override
    public StargateAbstractDialingManager<?> createDialingManager() {
        return new StargateUniverseDialingManager(this);
    }

    @Override
    public void generateMergeHelper() {
        // setup merge helper - client and server
        mergeHelper = new StargateUniverseMergeHelper(this);
        mergeHelper.horizontalFacing = getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY);
        mergeHelper.verticalFacing = dev.tauri.jsg.core.common.blockstate.JSGProperties.getDirectionByVerticalFacing(getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY));
        mergeHelper.basePos = getBlockPos();
    }

    @Override
    protected TagKey<Block> getLinkableTag() {
        return JSGBlockTags.STARGATE_UNIVERSE_LINKABLE_BLOCKS;
    }

    @Override
    @Nullable
    public ResourceKey<Level> getFakeWorld() {
        if (fakeWorld == null) {
            if (level == null) return null;
            return level.dimension();
        }
        return fakeWorld;
    }

    @Override
    public void setFakeWorld(Level world) {
        fakeWorld = world.dimension();
        setChanged();
    }

    @Override
    public BlockPos getFakePos() {
        if (fakePos == null) return getBlockPos();
        return fakePos;
    }

    @Override
    public void setFakePos(@Nullable BlockPos pos) {
        fakePos = pos;
        setChanged();
    }

    @SuppressWarnings("unused")
    public void resetFakePos() {
        this.fakePos = this.getBlockPos();
        this.fakeWorld = (level == null ? null : this.level.dimension());
        setChanged();
    }

    @Override
    public SymbolType<?> getSymbolType() {
        return JSGSymbolTypes.UNIVERSE.get();
    }

    @Override
    public StargateType<?> getStargateType() {
        return StargateTypes.UNIVERSE.get();
    }

    @Override
    public int getOpenSoundDelay() {
        return super.getOpenSoundDelay() + 10;
    }

    @Override
    public int getSupportedCapacitors() {
        return getConfig().getValueOrDefault(StargateConfigOptions.Universe.MAX_CAPACITORS);
    }

    // --------------------------------------------------------------------------------
    // Overlays

    @Override
    public List<Supplier<BiomeOverlayInstance>> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }

    // --------------------------------------------------------------------------------
    // Sounds

    @Nullable
    @Override
    public PositionedSound getPositionedSound(StargateSoundPositionedEnum soundEnum) {
        return switch (soundEnum) {
            case GATE_RING_ROLL -> JSGPositionedSounds.UNIVERSE_RING_ROLL;
            case GATE_RING_ROLL_START -> JSGPositionedSounds.UNIVERSE_RING_ROLL_START;
        };

    }

    @Override
    public void playSoundEvent(StargateSoundEventEnum soundEnum) {
        if (soundEnum == StargateSoundEventEnum.CHEVRON_OPEN) return;
        super.playSoundEvent(soundEnum);
    }

    @Nullable
    @Override
    public SoundEvent getSoundEvent(StargateSoundEventEnum soundEnum) {
        return switch (soundEnum) {
            case OPEN -> JSGSoundEvents.GATE_UNIVERSE_OPEN;
            case OPEN_NOX -> JSGSoundEvents.GATE_NOX_OPEN;
            case CLOSE -> JSGSoundEvents.GATE_UNIVERSE_CLOSE;
            case DIAL_FAILED -> JSGSoundEvents.GATE_UNIVERSE_DIAL_FAILED;
            case INCOMING -> JSGSoundEvents.GATE_UNIVERSE_DIAL_START;
            case RING_STOP ->
                    getDialingManager().getSpinHelper().getCurrentTopSymbol() == SymbolUniverseEnum.TOP_CHEVRON ? JSGSoundEvents.GATE_UNIVERSE_CHEVRON_TOP_LOCK : JSGSoundEvents.GATE_UNIVERSE_CHEVRON_LOCK;
            case CHEVRON_SHUT -> JSGSoundEvents.GATE_UNIVERSE_CHEVRON_LOCK;
            default -> null;
        };

    }


    // --------------------------------------------------------------------------------
    // NBTs

    @Override
    public void saveAdditional(CompoundTag compound) {
        if (fakePos != null) {
            compound.putInt("fakeX", fakePos.getX());
            compound.putInt("fakeY", fakePos.getY());
            compound.putInt("fakeZ", fakePos.getY());
        }
        if (fakeWorld != null)
            compound.putString("fakeWorld", fakeWorld.location().toString());

        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("fakeX"))
            this.fakePos = new BlockPos(compound.getInt("fakeX"), compound.getInt("fakeY"), compound.getInt("fakeZ"));
        if (compound.contains("fakeWorld") && level != null && level.getServer() != null)
            this.fakeWorld = ResourceKey.create(Registries.DIMENSION, (JSGMapping.rl(compound.getString("fakeWorld"))));
    }

    @SuppressWarnings("all")
    public NearbyGate getRandomNearbyGate() {
        ArrayList<NearbyGate> addresses = getNearbyGates();
        if (addresses.isEmpty()) return null;
        int i = (int) Math.min(Math.floor(Math.random() * addresses.size()), (addresses.size() - 1));
        if (i < 0) i = 0;
        return addresses.get(i);
    }
}