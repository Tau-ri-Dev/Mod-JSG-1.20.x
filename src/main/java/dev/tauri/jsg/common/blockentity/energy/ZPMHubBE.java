package dev.tauri.jsg.common.blockentity.energy;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.common.item.energy.ZPMItemBlock;
import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.common.state.energy.ZPMHubContainerGuiUpdate;
import dev.tauri.jsg.common.state.energy.ZPMHubRendererState;
import dev.tauri.jsg.common.state.energy.ZPMModelType;
import dev.tauri.jsg.core.common.blockentity.BEStateProvider;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import dev.tauri.jsg.core.common.blockstate.JSGProperties;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.packet.TargetPoint;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.power.general.LargeEnergyStorage;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.util.JSGItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

public class ZPMHubBE extends BlockEntity implements BEStateProvider, ITickable {

    private static final int SLIDING_ANIMATION_LENGTH = 50; // in ticks

    public int getAnimationLength() {
        return SLIDING_ANIMATION_LENGTH;
    }

    public int getContainerSize() {
        return 3;
    }

    public ZPMHubBE(BlockPos pos, BlockState state) {
        this(JSGBlockEntities.ZPM_HUB.get(), pos, state);
    }

    protected ZPMHubBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected final JSGItemStackHandler itemStackHandler = new JSGItemStackHandler(getContainerSize()) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (!isSlidingUp || isAnimating) return false;
            return stack.getItem() instanceof ZPMItemBlock;
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!isSlidingUp || isAnimating) return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (level != null && !level.isClientSide) {
                reloadStorages();
                sendState(CoreStateTypes.RENDERER_UPDATE.get(), getState(CoreStateTypes.RENDERER_UPDATE.get()));
            }
            setChanged();
        }
    };

    private final LargeEnergyStorage energyStorage = new LargeEnergyStorage(0, 0, JSGConfig.ZPM.zpmHubMaxEnergyTransfer.get()) {
        @Override
        public boolean canExtract() {
            return !isSlidingUp && !isAnimating;
        }

        @Override
        public long extractLongEnergy(long maxExtract, boolean simulate) {
            if (!canExtract()) return 0;
            return super.extractLongEnergy(Math.min(maxExtract, maxExtract()), simulate);
        }

        @Override
        public void onEnergyChanged() {
            setChanged();
        }
    };

    protected long energyStoredLastTick = 0;
    protected long energyTransferredLastTick = 0;
    private TargetPoint targetPoint;

    public long animationStart;
    public boolean isAnimating;
    public boolean isSlidingUp = true;

    // Only on client
    public int zpm1Level = -1;
    public int zpm2Level = -1;
    public int zpm3Level = -1;

    public ZPMModelType zpm1Type = ZPMModelType.NORMAL;
    public ZPMModelType zpm2Type = ZPMModelType.NORMAL;
    public ZPMModelType zpm3Type = ZPMModelType.NORMAL;

    public float facingAngle;
    // ---------------

    private final int[] lastSentLevels = new int[]{-2, -2, -2};

    public void startAnimation() {
        if (isAnimating || level == null) return;
        animationStart = level.getGameTime();
        isAnimating = true;
        isSlidingUp = !isSlidingUp;
        sendState(CoreStateTypes.RENDERER_UPDATE.get(), getState(CoreStateTypes.RENDERER_UPDATE.get()));
        setChanged();
    }

    public LargeEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public ItemStackHandler getItemHandler() {
        return itemStackHandler;
    }

    public long getEnergyTransferredLastTick() {
        return energyTransferredLastTick;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null) return;
        if (!level.isClientSide) {
            reloadStorages();
            targetPoint = new TargetPoint(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 512, level.dimension());
        } else {
            requestState(CoreStateTypes.RENDERER_UPDATE.get());
        }
    }

    protected void reloadStorages() {
        energyStorage.clearStorages();
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack stack = itemStackHandler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            var storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (storage != null)
                energyStorage.addStorage(storage);
        }
    }

    @Override
    public void tick(@NotNull Level level) {
        if (level.isClientSide) return;

        if (!isSlidingUp && !isAnimating) {
            for (Direction facing : Direction.values()) {
                var target = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(facing), facing.getOpposite());
                if (target == null || !target.canReceive()) continue;

                long available = energyStorage.extractLongEnergy(JSGConfig.ZPM.zpmHubMaxEnergyTransfer.get(), true);
                if (available <= 0) continue;

                int received = target.receiveEnergy(JSGEnergyStorage.regularEnergy(available), false);
                if (received > 0) {
                    energyStorage.extractLongEnergy(received, false);
                    setChanged();
                }
            }
        }

        long energyStored = energyStorage.getTrueEnergyStored();
        energyTransferredLastTick = energyStored - energyStoredLastTick;
        if (energyTransferredLastTick > 0) energyTransferredLastTick = 0;
        energyStoredLastTick = energyStored;

        // Sync the renderer when the displayed power levels of the inserted ZPMs change
        int[] levels = getDisplayedPowerLevels();
        if (!Arrays.equals(levels, lastSentLevels)) {
            System.arraycopy(levels, 0, lastSentLevels, 0, levels.length);
            sendState(CoreStateTypes.RENDERER_UPDATE.get(), getState(CoreStateTypes.RENDERER_UPDATE.get()));
        }

        if (isAnimating && (animationStart + getAnimationLength()) < level.getGameTime()) {
            isAnimating = false;
            animationStart = -1;
            sendState(CoreStateTypes.RENDERER_UPDATE.get(), getState(CoreStateTypes.RENDERER_UPDATE.get()));
            setChanged();
        }
    }

    private int[] getDisplayedPowerLevels() {
        int[] levels = new int[3];
        for (int i = 0; i < 3; i++) {
            levels[i] = -1;
            if (getContainerSize() > i) {
                ItemStack stack = itemStackHandler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getCapability(Capabilities.EnergyStorage.ITEM) instanceof JSGEnergyStorage storage) {
                    levels[i] = ZPMBE.getZPMPowerLevel(storage.getTrueEnergyStored(), storage.getTrueMaxEnergyStored());
                }
            }
        }
        return levels;
    }

    @Override
    public TargetPoint getTargetPoint() {
        return targetPoint;
    }

    @Override
    public State getState(StateType stateType) {
        if (stateType.equals(CoreStateTypes.RENDERER_UPDATE.get())) {
            int[] levels = getDisplayedPowerLevels();
            ZPMModelType[] types = new ZPMModelType[]{ZPMModelType.NORMAL, ZPMModelType.NORMAL, ZPMModelType.NORMAL};
            for (int i = 0; i < 3; i++) {
                if (getContainerSize() > i) {
                    ItemStack stack = itemStackHandler.getStackInSlot(i);
                    if (!stack.isEmpty())
                        types[i] = ZPMModelType.byStack(stack);
                }
            }
            Direction facing = getBlockState().getValue(JSGProperties.FACING_HORIZONTAL_PROPERTY);
            return new ZPMHubRendererState(animationStart, isAnimating, isSlidingUp,
                    levels[0], levels[1], levels[2], types[0], types[1], types[2],
                    (facing.get2DDataValue() - 2) * 90);
        }
        if (stateType.equals(CoreStateTypes.GUI_UPDATE.get()))
            return new ZPMHubContainerGuiUpdate(energyStorage.getEnergyStoredInternal(), energyTransferredLastTick);
        return null;
    }

    @Override
    public State createState(StateType stateType) {
        if (stateType.equals(CoreStateTypes.RENDERER_UPDATE.get()))
            return new ZPMHubRendererState();
        if (stateType.equals(CoreStateTypes.GUI_UPDATE.get()))
            return new ZPMHubContainerGuiUpdate();
        return null;
    }

    @Override
    public void setState(StateType stateType, State state) {
        if (stateType.equals(CoreStateTypes.RENDERER_UPDATE.get())) {
            ZPMHubRendererState s = (ZPMHubRendererState) state;
            this.animationStart = s.animationStart;
            this.isAnimating = s.isAnimating;
            this.isSlidingUp = s.slidingUp;

            this.zpm1Level = s.zpm1Level;
            this.zpm2Level = s.zpm2Level;
            this.zpm3Level = s.zpm3Level;

            this.zpm1Type = s.zpm1Type;
            this.zpm2Type = s.zpm2Type;
            this.zpm3Type = s.zpm3Type;

            this.facingAngle = s.facing;
            return;
        }
        if (stateType.equals(CoreStateTypes.GUI_UPDATE.get())) {
            ZPMHubContainerGuiUpdate guiUpdate = (ZPMHubContainerGuiUpdate) state;
            energyStorage.setEnergy(guiUpdate.energyStored, false);
            energyTransferredLastTick = guiUpdate.energyTransferredLastTick;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.put("energyStorage", energyStorage.serializeNBT(registries));
        compound.put("itemStackHandler", itemStackHandler.serializeNBT(registries));

        compound.putBoolean("isAnimating", isAnimating);
        compound.putBoolean("isSlidingUp", isSlidingUp);
        compound.putLong("animationStart", animationStart);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        energyStorage.deserializeNBT(registries, compound.getCompound("energyStorage"));
        itemStackHandler.deserializeNBT(registries, compound.getCompound("itemStackHandler"));

        isAnimating = compound.getBoolean("isAnimating");
        isSlidingUp = compound.getBoolean("isSlidingUp");
        animationStart = compound.getLong("animationStart");
    }
}
