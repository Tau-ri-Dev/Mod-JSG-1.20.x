package dev.tauri.jsg.common.blockentity.energy;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.common.state.energy.ZPMModelType;
import dev.tauri.jsg.core.common.blockentity.BEStateProvider;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.packet.TargetPoint;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.power.general.SmallEnergyStorage;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.state.CapacitorPowerLevelUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class ZPMBE extends BlockEntity implements BEStateProvider, ITickable {
    protected long energyStoredLastTick = 0;
    protected long energyTransferredLastTick = 0;
    private TargetPoint targetPoint;
    private int powerLevel;
    private int lastPowerLevel;
    public boolean isCorrupted = false;

    public ZPMBE(BlockPos pos, BlockState state) {
        this(JSGBlockEntities.ZPM.get(), pos, state);
    }

    protected ZPMBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private final SmallEnergyStorage energyStorage = new SmallEnergyStorage(JSGConfig.ZPM.zpmCapacity.get(), 0, JSGConfig.ZPM.zpmHubMaxEnergyTransfer.get() / 3) {
        @Override
        public void onEnergyChanged() {
            setChanged();
        }
    };

    public JSGEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public static int getZPMPowerLevel(long energyStored, long maxEnergy) {
        return (int) Math.round((energyStored / (double) maxEnergy) * 5);
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public long getEnergyTransferredLastTick() {
        return energyTransferredLastTick;
    }

    public ZPMModelType getZPMModelType() {
        return isCorrupted ? ZPMModelType.EXPLOSIVE : ZPMModelType.NORMAL;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null) return;
        if (!level.isClientSide) {
            targetPoint = new TargetPoint(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 512, level.dimension());
        } else {
            requestState(CoreStateTypes.RENDERER_UPDATE.get());
        }
    }

    @Override
    public void tick(@NotNull Level level) {
        if (level.isClientSide) return;

        powerLevel = getZPMPowerLevel(getEnergyStorage().getTrueEnergyStored(), getEnergyStorage().getTrueMaxEnergyStored());
        if (powerLevel != lastPowerLevel) {
            sendState(CoreStateTypes.RENDERER_UPDATE.get(), getState(CoreStateTypes.RENDERER_UPDATE.get()));
            lastPowerLevel = powerLevel;
        }

        energyTransferredLastTick = getEnergyStorage().getTrueEnergyStored() - energyStoredLastTick;
        energyStoredLastTick = getEnergyStorage().getTrueEnergyStored();
    }

    @Override
    public TargetPoint getTargetPoint() {
        return targetPoint;
    }

    @Override
    public State getState(StateType stateType) {
        if (stateType.equals(CoreStateTypes.RENDERER_UPDATE.get()))
            return new CapacitorPowerLevelUpdate(powerLevel);
        return null;
    }

    @Override
    public State createState(StateType stateType) {
        if (stateType.equals(CoreStateTypes.RENDERER_UPDATE.get()))
            return new CapacitorPowerLevelUpdate();
        return null;
    }

    @Override
    public void setState(StateType stateType, State state) {
        if (stateType.equals(CoreStateTypes.RENDERER_UPDATE.get())) {
            this.powerLevel = ((CapacitorPowerLevelUpdate) state).powerLevel;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.put("energyStorage", getEnergyStorage().serializeNBT(registries));
        compound.putBoolean("corrupted", isCorrupted);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        getEnergyStorage().deserializeNBT(registries, compound.getCompound("energyStorage"));
        isCorrupted = compound.getBoolean("corrupted");
    }
}
