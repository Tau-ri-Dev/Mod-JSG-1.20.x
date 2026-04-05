package dev.tauri.jsg.blockentity.dialhomedevice;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.client.screen.util.DHDScreenHelper;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.registry.JSGStateTypes;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.dialhomedevice.DHDReactorStateEnum;
import dev.tauri.jsg.api.stargate.dialhomedevice.StargateDHD;
import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.core.common.blockentity.ILinkable;
import dev.tauri.jsg.core.common.blockentity.StateProviderInterface;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.item.CommonUpgrade;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.StateUpdateRequestToServer;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.util.FluidTank;
import dev.tauri.jsg.core.common.util.JSGItemStackHandler;
import dev.tauri.jsg.registry.JSGItems;
import dev.tauri.jsg.renderer.dialhomedevice.DHDAbstractRendererState;
import dev.tauri.jsg.screen.inventory.dialhomedevice.DHDContainerGuiUpdate;
import dev.tauri.jsg.state.dialhomedevice.DHDActivateButtonState;
import dev.tauri.jsg.state.stargate.StargateBiomeOverrideState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class DHDAbstractBE extends BlockEntity implements StargateDHD, ILinkable<Stargate<?>>, StateProviderInterface {

    // ---------------------------------------------------------------------------------------------------
    // Gate linking

    public static final List<Supplier<BiomeOverlayInstance>> SUPPORTED_OVERLAYS = List.of(CoreBiomeOverlays.NORMAL, CoreBiomeOverlays.FROST, CoreBiomeOverlays.MOSSY, CoreBiomeOverlays.SOOTY, CoreBiomeOverlays.AGED);
    public static final List<Item> SUPPORTED_UPGRADES = Arrays.asList(JSGItems.CRYSTAL_GLYPH_DHD.get(), CoreItems.CRYSTAL_UPGRADE_CAPACITY.get(), CoreItems.CRYSTAL_UPGRADE_EFFICIENCY.get());
    public static final int BIOME_OVERRIDE_SLOT = 5;

    public abstract DHDScreenHelper getScreenHelper();

    public DHDAbstractBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Override
    public Vec3 getBlockPosInFront() {
        Direction dhdFacing = Direction.from2DDataValue(Math.round(getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ROTATION_PROPERTY) / 4.0f)).getOpposite();
        return getBlockPos().getCenter().add(new Vec3(dhdFacing.getNormal().getX(), dhdFacing.getNormal().getY(), dhdFacing.getNormal().getZ()));
    }

    @Override
    public FluidTank getFluidHandler() {
        return fluidHandler;
    }

    protected final FluidTank fluidHandler = new FluidTank(new FluidStack(CoreFluids.MOLTEN_NAQUADAH_REFINED.get(), 0), JSGConfig.DialHomeDevice.fluidCapacity.get()) {

        @Override
        public boolean isFluidValid(FluidStack fluid) {
            if (fluid == null) return false;

            return fluid.getFluid() == CoreFluids.MOLTEN_NAQUADAH_REFINED.get();
        }

        protected void onContentsChanged() {
            setChanged();
        }
    };
    protected DHDAbstractRendererState rendererStateClient;
    protected PacketDistributor.TargetPoint targetPoint;
    protected DHDReactorStateEnum reactorState = DHDReactorStateEnum.STANDBY;
    private BlockPos linkedGate = null;
    private BlockPos lastPos = BlockPos.ZERO;

    // ---------------------------------------------------------------------------------------------------
    // Renderer state

    private boolean hadControlCrystal;

    protected final ItemStackHandler itemStackHandler = new JSGItemStackHandler(6) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            Item item = stack.getItem();

            return switch (slot) {
                case 0 -> item == getControlCrystal();
                case 1, 2, 3 -> SUPPORTED_UPGRADES.contains(item) && !hasUpgrade(item);
                case 4 -> {
                    if (stack.getItem() instanceof BucketItem bucket) {
                        FluidStack fluid = new FluidStack(bucket.getFluid(), 1000);
                        yield (fluid.getFluid() == CoreFluids.MOLTEN_NAQUADAH_REFINED.get());
                    }
                    yield false;
                }
                case BIOME_OVERRIDE_SLOT -> {
                    var override = BiomeOverlayInstance.getBiomeOverlayByItem(stack, true);
                    yield override != null && getSupportedOverlays().stream().map(Supplier::get).toList().contains(override);
                }
                default -> true;
            };
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

            if (level != null && !level.isClientSide && slot == 0) {
                // Crystal changed
                updateCrystal();
            }
        }

        @SuppressWarnings("null")
        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack out = super.extractItem(slot, amount, simulate);

            if (level != null && !level.isClientSide && slot == 0 && amount > 0 && !simulate) {
                // Removing crystal
                updateCrystal();
            }

            return out;
        }

        @Override
        protected void onContentsChanged(int slot) {
            switch (slot) {
                case BIOME_OVERRIDE_SLOT:
                    sendState(CoreStateTypes.BIOME_OVERRIDE_STATE.get(), new StargateBiomeOverrideState(determineBiomeOverride()));
                    break;

                case 4:
                    ItemStack stack = getStackInSlot(slot);
                    if (stack.getItem() instanceof BucketItem bucket) {
                        FluidStack fluid = new FluidStack(bucket.getFluid(), 1000);
                        if (fluid.getFluid() == CoreFluids.MOLTEN_NAQUADAH_REFINED.get()) {
                            int amount = fluid.getAmount();
                            int filled = fluidHandler.fill(fluid, IFluidHandler.FluidAction.SIMULATE);
                            if (filled == amount) {
                                setStackInSlot(slot, new ItemStack(Items.BUCKET));
                                fluidHandler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                            }
                        }
                    }
                    break;


                default:
                    break;
            }

            super.onContentsChanged(slot);
            setChanged();
        }
    };

    @Override
    public ItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }

    // ------------------------------------------------------------

    public DHDAbstractRendererState getRendererStateClient() {
        return rendererStateClient;
    }

    public abstract void updateLinkStatus(Level world, BlockPos pos);

    @Override
    public boolean canLinkTo() {
        return !isLinked();
    }

    @Override
    public void setLinkedDevice(BlockPos devicePos) {
        linkedGate = devicePos;
        setChanged();
        if (getLevel() != null && !getLevel().isClientSide)
            sendLinkedDeviceToClients(getBlockPos(), targetPoint);
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
        if (level != null && !level.isClientSide) {
            targetPoint = new PacketDistributor.TargetPoint(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 512, level.dimension());
            hadControlCrystal = hasControlCrystal();
            sendState(CoreStateTypes.BIOME_OVERRIDE_STATE.get(), new StargateBiomeOverrideState(determineBiomeOverride()));
        } else {
            JSGCorePacketHandler.sendToServer(new StateUpdateRequestToServer(getBlockPos(), CoreStateTypes.RENDERER_STATE.get()));
            requestLinkedDeviceFromServer(getBlockPos());
        }
    }

    @Override
    public DHDReactorStateEnum getReactorState() {
        return reactorState;
    }

    @SuppressWarnings("null")
    @Override
    public void tick(@NotNull Level level) {
        if (level.isClientSide) {
            if (getRendererStateClient() == null) {
                JSGCorePacketHandler.sendToServer(new StateUpdateRequestToServer(getBlockPos(), CoreStateTypes.RENDERER_STATE.get()));
            }
        }
        if (!level.isClientSide) {

            if (!lastPos.equals(getBlockPos())) {
                lastPos = getBlockPos();
                this.updateLinkStatus(level, getBlockPos());
            }

            // Fluid upgrades
            int newFluidCapacity = JSGConfig.DialHomeDevice.fluidCapacity.get();
            if (hasUpgrade(CommonUpgrade.CAPACITY_UPGRADE))
                newFluidCapacity *= (int) JSGConfig.DialHomeDevice.capacityUpgradeMultiplier.get();

            if (fluidHandler.getCapacity() != newFluidCapacity) {
                fluidHandler.setCapacity(newFluidCapacity);
                setChanged();
                JSG.logger.debug("DHD at {} set itself new capacity! ({}mb)", getBlockPos().toShortString(), newFluidCapacity);
            }

            // Has crystal
            if (hasControlCrystal()) {
                if (isLinked()) {
                    var gateTile = getLinkedDevice();
                    if (gateTile == null) {
                        setLinkedDevice(null);

                        JSG.logger.error("Gate didn't unlink properly, forcing...");
                        return;
                    }

                    IEnergyStorage energyStorage = gateTile.getStargateCapability(ForgeCapabilities.ENERGY, null).resolve().orElseThrow();

                    int amount = 1;

                    if (reactorState != DHDReactorStateEnum.STANDBY) {
                        FluidStack simulatedDrain = fluidHandler.drain(amount, IFluidHandler.FluidAction.SIMULATE);

                        if (simulatedDrain.getAmount() >= amount)
                            reactorState = DHDReactorStateEnum.ONLINE;
                        else reactorState = DHDReactorStateEnum.NO_FUEL;
                    }

                    if (reactorState == DHDReactorStateEnum.ONLINE || reactorState == DHDReactorStateEnum.STANDBY) {
                        float percent = Objects.requireNonNull(energyStorage).getEnergyStored() / (float) energyStorage.getMaxEnergyStored();

                        if (percent < JSGConfig.DialHomeDevice.activationLevel.get())
                            reactorState = DHDReactorStateEnum.ONLINE;

                        else if (percent >= JSGConfig.DialHomeDevice.deactivationLevel.get())
                            reactorState = DHDReactorStateEnum.STANDBY;
                    }

                    if (reactorState == DHDReactorStateEnum.ONLINE) {
                        fluidHandler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                        double energyPerOne = JSGConfig.DialHomeDevice.energyPerNaquadah.get();
                        if (hasUpgrade(CommonUpgrade.EFFICIENCY_UPGRADE))
                            energyPerOne *= JSGConfig.DialHomeDevice.efficiencyUpgradeMultiplier.get();
                        energyStorage.receiveEnergy((int) energyPerOne, false);
                    }
                }

                // Not linked
                else {
                    reactorState = DHDReactorStateEnum.NOT_LINKED;
                }
            }

            // No crystal
            else {
                reactorState = DHDReactorStateEnum.NO_CRYSTAL;
            }
        }
    }

    // Server
    protected BiomeOverlayInstance determineBiomeOverride() {
        ItemStack stack = itemStackHandler.getStackInSlot(BIOME_OVERRIDE_SLOT);

        if (stack.isEmpty()) {
            return null;
        }

        BiomeOverlayInstance biomeOverlay = BiomeOverlayInstance.getBiomeOverlayByItem(stack);

        if (getSupportedOverlays().stream().map(Supplier::get).toList().contains(biomeOverlay)) {
            return biomeOverlay;
        }

        return null;
    }

    @Override
    public List<Supplier<BiomeOverlayInstance>> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }

    @Override
    public boolean hasControlCrystal() {
        return !itemStackHandler.getStackInSlot(0).isEmpty();
    }


    // -----------------------------------------------------------------------------
    // Item handler

    private void updateCrystal() {
        boolean hasControlCrystal = hasControlCrystal();

        if (hadControlCrystal != hasControlCrystal) {
            if (hasControlCrystal) {
                getAndSendState(CoreStateTypes.RENDERER_STATE.get());
            } else {
                clearSymbols();
            }

            hadControlCrystal = hasControlCrystal;
        }
    }

    public abstract void activateSymbol(SymbolInterface symbol);

    public abstract void pushSymbolButton(SymbolInterface symbol, @Nullable ServerPlayer player, boolean force);

    public void clearSymbols() {
        if (level != null) {
            level.blockEntityChanged(getBlockPos());
        }

        sendState(JSGStateTypes.DHD_ACTIVATE_BUTTON.get(), new DHDActivateButtonState(true));
    }

    @Override
    public State createState(StateType stateType) {
        return stateType.stateSupplier()
                .tryType(JSGStateTypes.DHD_ACTIVATE_BUTTON.get(), DHDActivateButtonState::new)
                .tryType(CoreStateTypes.GUI_UPDATE.get(), DHDContainerGuiUpdate::new)
                .tryType(CoreStateTypes.BIOME_OVERRIDE_STATE.get(), StargateBiomeOverrideState::new)
                .orElseThrow(this);
    }

    public boolean isLinkedClient;

    // -----------------------------------------------------------------------------
    // Fluid handler

    @Override
    public void setState(StateType stateType, State state) {
        if (stateType == CoreStateTypes.GUI_UPDATE.get()) {
            DHDContainerGuiUpdate guiState = (DHDContainerGuiUpdate) state;

            fluidHandler.setFluid(new FluidStack(CoreFluids.MOLTEN_NAQUADAH_REFINED.get(), guiState.fluidAmount));
            fluidHandler.setCapacity(guiState.tankCapacity);
            reactorState = guiState.reactorState;
            isLinkedClient = guiState.isLinked;
        }
    }


    // -----------------------------------------------------------------------------
    // Capabilities

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(this::getItemStackHandler).cast();
        }
        if (capability == ForgeCapabilities.FLUID_HANDLER && (facing == null || facing == Direction.DOWN)) {
            return LazyOptional.of(this::getFluidHandler).cast();
        }
        return super.getCapability(capability, facing);
    }


    // ---------------------------------------------------------------------------------------------------
    // NBT

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        if (linkedGate != null) {
            compound.putLong("linkedGate", linkedGate.asLong());
        }

        compound.put("itemStackHandler", itemStackHandler.serializeNBT());

        CompoundTag fluidHandlerCompound = new CompoundTag();
        fluidHandler.writeToNBT(fluidHandlerCompound);
        compound.put("fluidHandler", fluidHandlerCompound);

        super.saveAdditional(compound);
    }

    @Override
    public void load(@Nonnull CompoundTag compound) {
        super.load(compound);

        if (compound.contains("linkedGate")) {
            linkedGate = BlockPos.of(compound.getLong("linkedGate"));
        }

        itemStackHandler.deserializeNBT(compound.getCompound("itemStackHandler"));

        if (compound.getBoolean("hasUpgrade") || compound.getBoolean("insertAnimation")) {
            itemStackHandler.setStackInSlot(1, new ItemStack(JSGItems.CRYSTAL_GLYPH_DHD.get()));
        }

        fluidHandler.readFromNBT(compound.getCompound("fluidHandler"));
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

    @Override
    public PacketDistributor.TargetPoint getTargetPoint() {
        if (targetPoint == null && level != null) {
            targetPoint = new PacketDistributor.TargetPoint(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 512, level.dimension());
        }
        return targetPoint;
    }
}
