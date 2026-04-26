package dev.tauri.jsg.common.stargate.manager;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.config.util.StargateTimeLimitModeEnum;
import dev.tauri.jsg.api.power.PowerUtils;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.manager.IStargateEnergyManager;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.core.common.config.json.dimension.JSGDimensionConfig;
import dev.tauri.jsg.core.common.helper.BlockPosHelper;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.power.general.EnergyRequiredToOperate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class StargateEnergyManager<SG extends StargateAbstractBaseBE<?, ?>, E extends JSGEnergyStorage> extends AbstractStargateManager<SG> implements IStargateEnergyManager<E> {
    // saved
    protected final EnergyRequiredToOperate currentEnergyRequirements = EnergyRequiredToOperate.free();
    protected int energyStoredLastTick = -1;

    // not saved
    protected int energyTransferredLastTick;
    protected double energySecondsToClose = -1;

    public StargateEnergyManager(SG stargate) {
        super(stargate);
    }

    @Override
    public double getSecondsToClose() {
        return energySecondsToClose;
    }

    @Override
    public int getTransferredLastTick() {
        return energyTransferredLastTick;
    }

    public void setSecondsToClose(long seconds) {
        energySecondsToClose = seconds;
    }

    public void setTransferredLastTick(int energy) {
        energyTransferredLastTick = energy;
    }

    public void onGateOpen() {
        var targetGate = stargate.getDialingManager().getConnection().getTarget();
        if (targetGate.isPresent())
            currentEnergyRequirements.update(getEnergyRequiredToDial(targetGate.get(), stargate.getDialingManager().getDialedAddress()));
        else
            currentEnergyRequirements.update(EnergyRequiredToOperate.free());
        getStorage().extractEnergy(currentEnergyRequirements.energyToOpen, false);
    }

    @Override
    public void tick(Level level) {
        if (level.isClientSide) return;
        if (!stargate.getDialingManager().getConnection().getStatus().full()) {
            energySecondsToClose = -1;
        }

        checkMaximumTimeLimitOpen();

        energyTransferredLastTick = getStorage().getEnergyStored() - energyStoredLastTick;
        if (energyStoredLastTick == -1)
            energyTransferredLastTick = 0;
        energyStoredLastTick = getStorage().getEnergyStored();
        stargate.setChanged();

        stargate.getDialingManager().getConnection().runIfInitializing((connOutgoing, sgOutgoing) -> {
            if (!connOutgoing.getStatus().full()) return;
            var outgoingEnergyManager = (StargateEnergyManager<?, ?>) sgOutgoing.getEnergyManager();

            outgoingEnergyManager.consumeByWormhole();

            if (outgoingEnergyManager.energyTransferredLastTick < 0) {
                outgoingEnergyManager.energySecondsToClose = Math.max(0, ((double) outgoingEnergyManager.energyStoredLastTick / (double) -outgoingEnergyManager.energyTransferredLastTick) / 20.0);
            } else
                outgoingEnergyManager.energySecondsToClose = Integer.MAX_VALUE;
            sgOutgoing.setStargateChanged();
            connOutgoing.runOnConnected((connIncoming, sgIncoming) -> {
                var incomingEnergyManager = (StargateEnergyManager<?, ?>) sgOutgoing.getEnergyManager();
                incomingEnergyManager.energySecondsToClose = outgoingEnergyManager.energySecondsToClose;
                sgIncoming.setStargateChanged();
            });
        });
    }

    protected void checkMaximumTimeLimitOpen() {
        if (!stargate.getDialingManager().getConnection().getStatus().full())
            return;
        int configPower = JSGConfig.Stargate.maxOpenedPowerDrawAfterLimit.get();
        int maxSeconds = JSGConfig.Stargate.maxOpenedSeconds.get();
        StargateTimeLimitModeEnum limitMode = JSGConfig.Stargate.maxOpenedWhat.get();

        if (stargate instanceof StargateClassicBaseBE<?> casted) {
            limitMode = casted.getConfig().getValueOrDefault(StargateConfigOptions.Common.TIME_LIMIT_MODE);
            maxSeconds = casted.getConfig().getValueOrDefault(StargateConfigOptions.Common.TIME_LIMIT_TIME);
            configPower = casted.getConfig().getValueOrDefault(StargateConfigOptions.Common.TIME_LIMIT_POWER);
        }
        boolean enabled = (limitMode != StargateTimeLimitModeEnum.DISABLED);
        if (!enabled) return;
        var secondsOpen = stargate.getDialingManager().getConnection().getSecondsOpen();
        if (secondsOpen < maxSeconds) return;
        if (limitMode == StargateTimeLimitModeEnum.CLOSE_GATE) {
            stargate.getDialingManager().attemptClose(StargateClosedReasonEnum.TIME_LIMIT);
            return;
        }
        var power = (int) ((secondsOpen / (double) maxSeconds) * configPower);
        getStorage().extractEnergy(power, false);
    }

    protected void consumeByWormhole() {
        if (energySecondsToClose < 0) return;
        stargate.getEventHorizonManager().updateUnstability(energySecondsToClose, energyTransferredLastTick);
        if (stargate.getDialingManager().getConnection().withoutEnergy()) return;
        if (energySecondsToClose <= 1) {
            stargate.getDialingManager().attemptClose(StargateClosedReasonEnum.OUT_OF_POWER);
            return;
        }
        getStorage().extractEnergy(currentEnergyRequirements.keepAlive, false);
    }

    @Override
    public EnergyRequiredToOperate getEnergyRequiredToDial(@Nullable StargatePos targetGatePos, StargateAddressDynamic address) {
        var level = getLevel();
        if (level == null || targetGatePos == null) return PowerUtils.stargateConsumption();
        BlockPos sPos = getBlockPos();
        BlockPos tPos = targetGatePos.gatePos;

        ResourceKey<Level> sourceDim = level.dimension();
        ResourceKey<Level> targetDim = targetGatePos.getWorld().dimension();

        var coordsScale = DimensionType.getTeleportationScale(level.dimensionType(), targetGatePos.getWorld().dimensionType());

        double distance = (int) BlockPosHelper.dist(tPos, (int) (sPos.getX() * coordsScale), sPos.getY(), (int) (sPos.getZ() * coordsScale));

        if (distance < 5000) distance *= 0.8;
        else distance = 5000 * Math.log10(distance) / Math.log10(5000);

        EnergyRequiredToOperate energyRequired = PowerUtils.stargateConsumption();
        energyRequired = energyRequired.mul(distance).add(PowerUtils.stargateConsumption().mul(JSGDimensionConfig.INSTANCE.getDistanceBetween(sourceDim, targetDim)));

        if (address.size() == 9)
            energyRequired = energyRequired.mul(JSGConfig.Stargate.nineSymbolAddressMul.get());
        if (address.size() == 8)
            energyRequired = energyRequired.mul(JSGConfig.Stargate.eightSymbolAddressMul.get());
        return energyRequired;
    }

    protected Level getLevel() {
        return stargate.getLevel();
    }

    protected BlockPos getBlockPos() {
        return stargate.getBlockPos();
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.put("energyStorage", getStorage().serializeNBT());
        compound.putInt("energyStoredLastTick", energyStoredLastTick);
        compound.put("currentEnergyRequirements", currentEnergyRequirements.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        getStorage().deserializeNBT(compound.getCompound("energyStorage"));
        energyStoredLastTick = compound.getInt("energyStoredLastTick");
        currentEnergyRequirements.deserializeNBT(compound.getCompound("currentEnergyRequirements"));
    }
}
