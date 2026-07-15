package dev.tauri.jsg.common.blockentity.dialhomedevice;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.dialhomedevice.StargateDHD;
import dev.tauri.jsg.api.dialhomedevice.upgrade.IDHDUpgrade;
import dev.tauri.jsg.api.dialhomedevice.upgrade.IDHDUpgradeBehavior;
import dev.tauri.jsg.api.dialhomedevice.upgrade.IDHDUpgradeItem;
import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.item.IDHDPartItem;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.result.StargateCloseResult;
import dev.tauri.jsg.api.stargate.result.StargateOpenResult;
import dev.tauri.jsg.common.advancements.JSGCriterions;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.common.dialhomedevice.animation.DHDButtonsState;
import dev.tauri.jsg.common.dialhomedevice.manager.DHDReactorManager;
import dev.tauri.jsg.common.dialhomedevice.manager.state.DHDAbstractStateManager;
import dev.tauri.jsg.common.helpers.StargateLinkingHelper;
import dev.tauri.jsg.core.common.blockentity.BEStateProvider;
import dev.tauri.jsg.core.common.blockentity.ILinkable;
import dev.tauri.jsg.core.common.blockentity.JSGBlockEntity;
import dev.tauri.jsg.core.common.config.ingame.IConfigurable;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.helper.LinkingHelper;
import dev.tauri.jsg.core.common.item.CommonUpgrade;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import dev.tauri.jsg.core.common.sound.ISoundEvent;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.util.FluidTank;
import dev.tauri.jsg.core.common.util.JSGItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public abstract class DHDAbstractBE extends JSGBlockEntity implements StargateDHD, ILinkable<Stargate<?>>, BEStateProvider {
    // ====================================================================================
    protected DHDAbstractStateManager<?, ?> stateManager;
    protected DHDReactorManager reactorManager;

    protected final HashMap<Integer, IDHDUpgradeBehavior> behaviors = new HashMap<>();

    public DHDAbstractBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
        this.stateManager = createStateManager();
        this.reactorManager = new DHDReactorManager(this, () -> new FluidTank(new FluidStack(CoreFluids.MOLTEN_NAQUADAH_REFINED.get(), 0), JSGConfig.DialHomeDevice.fluidCapacity.get()) {
            @Override
            public boolean isFluidValid(FluidStack fluid) {
                if (fluid == null) return false;
                return fluid.getFluid() == CoreFluids.MOLTEN_NAQUADAH_REFINED.get();
            }

            @Override
            protected void onContentsChanged() {
                setChanged();
            }
        }, (tank) -> hasControlCrystal(), (tank) -> {
            double energyPerOne = JSGConfig.DialHomeDevice.energyPerNaquadah.get();
            if (hasUpgrade(CommonUpgrade.EFFICIENCY_UPGRADE)) {
                energyPerOne *= JSGConfig.DialHomeDevice.efficiencyUpgradeMultiplier.get();
            }
            return (int) energyPerOne;
        });
    }

    @Override
    public DHDAbstractStateManager<?, ?> getStateManager() {
        return stateManager;
    }

    protected abstract DHDAbstractStateManager<?, ?> createStateManager();

    @Override
    public DHDReactorManager getReactorManager() {
        return reactorManager;
    }


    @Override
    public State getState(StateType stateType) {
        return getStateManager().getState(stateType);
    }

    @Override
    public State createState(StateType stateType) {
        return getStateManager().getState(stateType);
    }

    @Override
    public void setState(StateType stateType, State state) {
        getStateManager().setState(stateType, state);
    }


    public abstract ISoundEvent getButtonPressSound();

    public abstract ISoundEvent getBRBPressSound();

    @Override
    public void activateSymbol(SymbolInterface symbol) {
        if (level == null) return;
        if (getStateManager().getButtonsState().get(symbol).isActive()) return;
        var playSound = getLinkedDeviceOptional().map(gateTile -> {
            if (!gateTile.getDialingManager().getStargateState().dialingComputer()) return true;
            return (gateTile instanceof IConfigurable configurable && configurable.getConfig().getValueOrDefault(StargateConfigOptions.Classic.DHD_OC_PRESS_SOUND));
        }).orElse(false);

        // When using OC to dial, don't play sound of the DHD button press
        if (playSound) {
            if (symbol.brb())
                JSGSoundHelper.playSoundEvent(level, getBlockPos(), getBRBPressSound());
            else
                JSGSoundHelper.playSoundEvent(level, getBlockPos(), getButtonPressSound());
        }

        Optional.ofNullable(getStateManager().getButtonsState().get(symbol)).ifPresent(DHDButtonsState.ButtonState::activate);
    }

    @Override
    public void clearSymbols() {
        getStateManager()
                .getButtonsState()
                .getActivatedButtons()
                .forEach(symbol -> getStateManager()
                        .getButtonsState()
                        .get(symbol)
                        .deactivate());
    }

    // TODO(Mine): Refactor "dhd_milkyway" key to just "dhd"
    @Override
    public void pushSymbolButton(SymbolInterface symbol, @Nullable ServerPlayer player, boolean force) {
        if (!hasControlCrystal()) {
            if (player != null)
                player.sendSystemMessage(Component.translatable("block.jsg.dhd_milkyway.no_crystal_warn"), true);
            return;
        }
        getLinkedDeviceOptional().ifPresentOrElse((gateTile) -> {
            var dm = gateTile.getDialingManager();
            var gateState = dm.getStargateState();
            if (gateState.engaged() && symbol.brb()) {
                // Gate is open, BRB was press, possible closure attempt
                if (gateState.initiating()) {
                    dm.attemptClose(StargateClosedReasonEnum.REQUESTED);
                    return;
                }
                if (player != null) {
                    StargateComputerEvents.ATTEMPT_CLOSE_FAILED.apply(StargateCloseResult.NOT_INITIATING, dm.getDialedAddress(), false).sendVia(gateTile);
                    player.sendSystemMessage(Component.translatable("block.jsg.dhd_milkyway.incoming_wormhole_warn"), true);
                    return;
                }
            }
            if (dm.canAbortDialing() && force && symbol.brb()) {
                dm.abortDialingSequence();
                return;
            }
            if (symbol.brb() && gateState.idle()) {
                // BRB pressed on idling gate, attempt to open
                StargateOpenResult openResult = dm.attemptOpenDialed();
                if (openResult.ok()) {
                    JSGCriterions.CHEVRON_SEVEN_LOCKED.trigger(player);
                    return;
                }
                if (openResult == StargateOpenResult.NOT_ENOUGH_POWER && player != null) {
                    player.sendSystemMessage(Component.translatable("block.jsg.dhd_milkyway.not_enough_power"), true);
                    return;
                }
                return;
            }
            dm.engageSymbolDHD(symbol, false, false);
        }, () -> {
            if (player != null)
                player.sendSystemMessage(Component.translatable("block.jsg.dhd_milkyway.not_linked_warn"), true);
        });
    }

    public Collection<IDHDUpgradeBehavior> getBehaviors() {
        return behaviors.values();
    }

    // ====================================================================================


    @Override
    public Vec3 getBlockPosInFront() {
        Direction dhdFacing = Direction.from2DDataValue(Math.round(getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ROTATION_PROPERTY) / 4.0f)).getOpposite();
        return getBlockPos().getCenter().add(new Vec3(dhdFacing.getNormal().getX(), dhdFacing.getNormal().getY(), dhdFacing.getNormal().getZ()));
    }

    private BlockPos linkedGate = null;
    private BlockPos lastPos = BlockPos.ZERO;

    protected final ItemStackHandler itemStackHandler = new JSGItemStackHandler(6) {

        @Override
        public boolean isItemValid(int slot, @NonNull ItemStack stack) {
            if (slot == 0) return stack.getItem() == getControlCrystal();
            return stack.getItem() instanceof IDHDUpgradeItem upgrade && upgrade.getUpgrade() instanceof IDHDUpgrade;
        }

        @SuppressWarnings("null")
        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }

        @SuppressWarnings("null")
        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            super.setStackInSlot(slot, stack);
        }

        @SuppressWarnings("null")
        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == 0) {
                if (getStackInSlot(slot).isEmpty())
                    getStateManager().disassemblePart((IDHDPartItem) getControlCrystal());
                else if (!isAssembled((IDHDPartItem) getControlCrystal()))
                    getStateManager().assemblePart((IDHDPartItem) getControlCrystal());
            } else {
                if (getStackInSlot(slot).isEmpty() && behaviors.containsKey(slot))
                    behaviors.remove(slot);
                else if (getStackInSlot(slot).getItem() instanceof IDHDUpgradeItem dhdUpgradeItem && dhdUpgradeItem.getUpgrade() instanceof IDHDUpgrade dhdUpgrade) {
                    behaviors.put(slot, dhdUpgrade.createBehavior(DHDAbstractBE.this));
                }
            }
            setChanged();
        }
    };

    @Override
    public abstract Item getControlCrystal();

    @ParametersAreNonnullByDefault
    public void handleAssembleRequestFromClient(IDHDPartItem part, boolean disassemble, ServerPlayer player, ItemStack stack) {
        var level = getLevel();
        if (level == null || level.isClientSide()) return;
        if (!getAllParts().contains(part)) return;
        var hasPart = isAssembled(part);
        if (disassemble == !hasPart) return;
        if (disassemble) {
            var eventCancel = MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level, getBlockPos(), getBlockState(), player));
            if (eventCancel) return;
            var newStack = new ItemStack(part.self());
            var result = onPartAssembled(part, newStack, true);
            if (!result) return;
            getStateManager().disassemblePart(part);
            player.getInventory().add(newStack);
            level.playSound(null, getBlockPos(), part.getDisassembleSound(), SoundSource.BLOCKS, 1, 1);
        } else {
            var eventCanceled = ForgeEventFactory.onBlockPlace(player, BlockSnapshot.create(level.dimension(), level, getBlockPos()), Direction.UP);
            if (eventCanceled) return;
            var result = onPartAssembled(part, stack, false);
            if (!result) return;
            getStateManager().assemblePart(part);
            stack.shrink(1);
            level.playSound(null, getBlockPos(), part.getAssembleSound(), SoundSource.BLOCKS, 1, 1);
        }
        level.gameEvent(GameEvent.BLOCK_CHANGE, getBlockPos(), GameEvent.Context.of(player));
        setChanged();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isAssembled(IDHDPartItem part) {
        return getStateManager().isDHDPartAssembled(part);
    }

    @Override
    public boolean isAssembled() {
        return getAllParts().stream().filter(IDHDPartItem::isMandatory).allMatch(this::isAssembled);
    }

    @Override
    public boolean onPartAssembled(IDHDPartItem part, ItemStack stack, boolean removed) {
        if (part != getFluidTankItemPart()) return true;
        var stackTank = getFluidTankItemPart().getTank(stack);
        var dhdTank = getReactorManager().getTank();
        if (removed) {
            stackTank.setCapacity(dhdTank.getCapacity());
            stackTank.setFluid(dhdTank.getFluid());
            return true;
        }
        dhdTank.setCapacity(stackTank.getCapacity());
        dhdTank.setFluid(stackTank.getFluid());
        return true;
    }

    @Override
    public ItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }

    @Override
    public IItemHandler getItemHandler() {
        return getItemStackHandler();
    }

    public abstract TagKey<Block> getLinkableBlocks();

    public void updateLinkStatus(Level world, BlockPos pos) {
        if (isLinked() && getLinkedDevice() instanceof ILinkable<?> linkable) {
            linkable.setLinkedDevice(null);
            setLinkedDevice(null);
        }
        BlockPos closestGate = LinkingHelper.findClosestUnlinked(world, pos, StargateLinkingHelper.getDhdRange(), getLinkableBlocks());

        if (closestGate != null) {
            ILinkable<?> gateTile = (ILinkable<?>) world.getBlockEntity(closestGate);
            if (gateTile != null) {
                gateTile.setLinkedDevice(pos);
                setLinkedDevice(closestGate);
            }
        }
    }

    @Override
    public boolean canLinkTo() {
        return !isLinked();
    }

    @Override
    public void setLinkedDevice(BlockPos devicePos) {
        linkedGate = devicePos;
        setChanged();
        if (getLevel() != null && !getLevel().isClientSide)
            sendLinkedDeviceToClients(getBlockPos(), getTargetPoint());
    }

    @Nullable
    @Override
    public StargateAbstractBaseBE<?, ?> getLinkedDevice() {
        if (linkedGate == null) return null;
        if (getLevel() == null) return null;
        if (getLevel().getBlockEntity(linkedGate) instanceof StargateAbstractBaseBE<?, ?> gate)
            return gate;
        return null;
    }

    @Nullable
    @Override
    public BlockPos getLinkedPos() {
        return linkedGate;
    }

    // -----------------------------------------------------------------------------
    // Symbol activation

    @SuppressWarnings("null")
    @Override
    public void onLoad() {
        if (getLevel() == null) return;
        StargateDHD.super.onLoad(getLevel());
        if (getLevel().isClientSide())
            requestLinkedDeviceFromServer(getBlockPos());
    }

    @SuppressWarnings("null")
    @Override
    public void tick(@NotNull Level level) {
        StargateDHD.super.tick(level);

        if (!level.isClientSide) {
            if (!lastPos.equals(getBlockPos())) {
                lastPos = getBlockPos();
                this.updateLinkStatus(level, getBlockPos());
            }
        }
    }


    public BiomeOverlayInstance determineBiomeOverride() {
        // TODO: Get overlay from Stargate
        return null;
    }

    // -----------------------------------------------------------------------------
    // Capabilities

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
        if (capability == ForgeCapabilities.FLUID_HANDLER) {
            if (isAssembled(getFluidTankItemPart()))
                return LazyOptional.of(() -> getReactorManager().getTank()).cast();
        }
        return super.getCapability(capability, facing);
    }


    @Override
    public PacketDistributor.TargetPoint getTargetPoint() {
        return getStateManager().getTargetPoint();
    }


    // ---------------------------------------------------------------------------------------------------
    // NBT

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        compound.put("stateManager", stateManager.serializeNBT());
        compound.put("reactorManager", reactorManager.serializeNBT());
        super.saveAdditional(compound);


        if (linkedGate != null) {
            compound.putLong("linkedGate", linkedGate.asLong());
        }

        compound.put("itemStackHandler", itemStackHandler.serializeNBT());

        var behaviorsCompound = new CompoundTag();
        behaviors.forEach((slot, behavior) -> behaviorsCompound.put("behavior_" + slot, behavior.serializeNBT()));
        compound.put("behaviors", behaviorsCompound);
    }

    @Override
    public void load(@Nonnull CompoundTag compound) {
        super.load(compound);
        stateManager.deserializeNBT(compound.getCompound("stateManager"));
        reactorManager.deserializeNBT(compound.getCompound("reactorManager"));


        if (compound.contains("linkedGate")) {
            linkedGate = BlockPos.of(compound.getLong("linkedGate"));
        }

        itemStackHandler.deserializeNBT(compound.getCompound("itemStackHandler"));

        var behaviorsCompound = compound.getCompound("behaviors");
        behaviors.clear();
        for (var key : behaviorsCompound.getAllKeys()) {
            int slot = Integer.parseInt(key.replace("behavior_", ""));
            var stack = getItemStackHandler().getStackInSlot(slot);
            if (stack.getItem() instanceof IDHDUpgradeItem dhdUpgradeItem && dhdUpgradeItem.getUpgrade() instanceof IDHDUpgrade dhdUpgrade)
                behaviors.put(slot, dhdUpgrade.createBehavior(this));
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        return super.serializeNBT();
    }

    @Nonnull
    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().offset(-1, 0, -1), getBlockPos().offset(1, 2, 1));
    }

    @Override
    public boolean prepareBE() {
        if (getLinkedDevice() instanceof ILinkable<?> linkable) linkable.setLinkedDevice(null);
        setLinkedDevice(null);
        return true;
    }
}
