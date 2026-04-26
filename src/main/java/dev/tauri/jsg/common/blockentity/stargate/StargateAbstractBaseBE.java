package dev.tauri.jsg.common.blockentity.stargate;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.entity.StargateAddressData;
import dev.tauri.jsg.api.integration.StargateComputerEvents;
import dev.tauri.jsg.api.registry.JSGNotebookPageTypes;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.listener.IStargateListenerHandler;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.util.IStargateGenerator;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateAbstractRendererState;
import dev.tauri.jsg.common.config.JSGConfigUtil;
import dev.tauri.jsg.common.item.linkable.dialer.modes.UniverseDialerModes;
import dev.tauri.jsg.common.multistructure.mergehelper.StargateAbstractMergeHelper;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.common.stargate.StargateListenerHandler;
import dev.tauri.jsg.common.stargate.manager.*;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateAbstractDialingManager;
import dev.tauri.jsg.common.stargate.manager.state.StargateAbstractStateManager;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.common.stargate.rig.StargateRIGManager;
import dev.tauri.jsg.common.state.stargate.StargateRendererActionState;
import dev.tauri.jsg.common.util.JSGAdvancementsUtil;
import dev.tauri.jsg.core.common.blockentity.CamouflageBE;
import dev.tauri.jsg.core.common.blockentity.IBELogManager;
import dev.tauri.jsg.core.common.blockentity.ILinkable;
import dev.tauri.jsg.core.common.config.json.dimension.JSGDimensionConfig;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.integration.ComputerDeviceHolder;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemFilled;
import dev.tauri.jsg.core.common.multistructure.IMultiStructureBE;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.sound.IPositionedSound;
import dev.tauri.jsg.core.common.sound.ISoundEvent;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class StargateAbstractBaseBE<S extends StargateAbstractRendererState, E extends JSGEnergyStorage> extends CamouflageBE implements Stargate<E>, IMultiStructureBE<StargateAbstractMergeHelper> {
    protected final StargateAbstractStateManager<?, S> stateManager = createStateManager();
    protected final StargateSoundManager<?> soundManager = createSoundManager();
    protected final StargateAbstractDialingManager<?> stargateDialingManager = createDialingManager();
    protected final StargateEnergyManager<?, E> stargateEnergyManager = createEnergyManager();
    protected final StargateRIGManager rigManager = new StargateRIGManager(this);
    protected final IStargateListenerHandler listenerHandler = new StargateListenerHandler(this);
    protected final IBELogManager logManager = new StargateLogManager();
    protected final StargateEventHorizonManager eventHorizon = createEventHorizonManager();
    protected StargateAutoCloseManager autoCloseManager;
    protected ComputerDeviceHolder computerDeviceHolder;

    protected final Map<SymbolType<?>, StargateAddress> gateAddressMap = new HashMap<>();
    protected StargatePos stargatePos;
    protected StargateAbstractMergeHelper mergeHelper = null;
    protected boolean isMerged;
    protected boolean addedToComputerNetwork;
    protected boolean needRegenerate = false;

    public Map<SymbolType<?>, StargateAddress> gateAddressMapClient = new HashMap<>();

    public StargateAbstractBaseBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
        generateMergeHelper();
        if (level != null && !level.isClientSide)
            createDeviceHolder();
        setChanged();
    }

    @Override
    public long getTime() {
        if (level == null) return 0;
        return level.getGameTime();
    }

    @Override
    public StargateNetwork getNetwork() {
        return StargateNetwork.INSTANCE;
    }

    @Override
    public abstract StargateAbstractStateManager<?, S> createStateManager();

    @Override
    public abstract StargateAbstractDialingManager<?> createDialingManager();

    @Override
    public abstract StargateEnergyManager<?, E> createEnergyManager();

    @Override
    public abstract StargateEventHorizonManager createEventHorizonManager();

    @Override
    public StargateSoundManager<?> createSoundManager() {
        return new StargateSoundManager<>(this);
    }

    // ----------------------------------------------
    // GETTERS

    @Override
    public StargateRIGManager getRIGManager() {
        return rigManager;
    }

    @Override
    public StargateAbstractDialingManager<?> getDialingManager() {
        return stargateDialingManager;
    }

    @Override
    public StargateEnergyManager<?, E> getEnergyManager() {
        return stargateEnergyManager;
    }

    @Override
    public StargateAbstractStateManager<?, S> getStateManager() {
        return stateManager;
    }

    @Override
    public StargateSoundManager<?> getSoundManager() {
        return soundManager;
    }

    @Override
    public StargateAutoCloseManager getAutoCloseManager() {
        if (autoCloseManager == null) autoCloseManager = new StargateAutoCloseManager(this);
        return autoCloseManager;
    }

    @Override
    public IStargateListenerHandler getListenerHandler() {
        return listenerHandler;
    }

    @Override
    public StargateEventHorizonManager getEventHorizonManager() {
        return eventHorizon;
    }

    @Override
    public IBELogManager getLogManager() {
        return logManager;
    }

    // ----------------------------------------------
    // ADDRESSES

    @Override
    public Map<SymbolType<?>, StargateAddress> getAddressMap() {
        return Map.copyOf(gateAddressMap);
    }

    @Nullable
    public StargateAddress getStargateAddress(SymbolType<?> symbolType) {
        if (gateAddressMap == null) return null;
        return gateAddressMap.get(symbolType);
    }

    @Override
    public void setGateAddress(SymbolType<?> symbolType, StargateAddress stargateAddress) {
        initStargatePos();
        gateAddressMap.put(symbolType, stargateAddress);
        getNetwork().putStargate(stargateAddress, stargatePos);
        JSG.logger.debug("Setting gate's address at {} to {} ({})", stargatePos.toString(), stargateAddress.toString(), symbolType.toString());
        setChanged();
    }

    // STARGATE POS
    @Override
    public StargatePos getStargatePos() {
        if (stargatePos == null) initStargatePos();
        return stargatePos;
    }

    @Override
    public void renameStargatePos(String newName) {
        initStargatePos();
        getNetwork().renameStargate(stargatePos, newName);
        stargatePos.setName(newName);
        setChanged();
    }

    @Override
    public void initStargatePos() {
        StargatePos oldPos = stargatePos;

        if (getLevel() == null) return;
        StargatePos gatePos = new StargatePos(getLevel().dimension(), getBlockPos(), getSymbolType(), getStargateType());
        if (oldPos != null)
            gatePos.setName(oldPos.getName());
        stargatePos = gatePos;
        setChanged();
    }

    @Override
    public void refresh() {
        initStargatePos();
        if (stargatePos != null)
            getNetwork().removeStargate(stargatePos);
        for (SymbolType<?> s : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
            StargateAddress address = getStargateAddress(s);
            if (address == null) {
                generateAddresses(true);
                //address = getStargateAddress(s);
                break;
            }
            setGateAddress(s, address);
        }
        updateFacing();
        setChanged();
    }

    @Override
    public boolean shouldAutoclose() {
        if (!JSGConfig.Stargate.autocloseEnabled.get()) return false;
        if (!rigManager.isActive()) {
            return getDialingManager().getConnection().callIfInitiating((conn, sg) -> getAutoCloseManager().shouldClose(sg.getStargatePos()), () -> false);
        } else return !rigManager.isActive();
    }

    @Override
    public final boolean isMerged() {
        return isMerged;
    }

    @Override
    public void setMerged(boolean merged) {
        isMerged = merged;
    }

    @Override
    public StargateAbstractMergeHelper getMergeHelper() {
        if (mergeHelper == null) generateMergeHelper();
        return mergeHelper;
    }


    @Override
    public void updateFacing() {
        generateMergeHelper();
        getEventHorizonManager().onFacingUpdated();
    }

    @Override
    public boolean prepareBE() {
        needRegenerate = true;
        if (this instanceof ILinkable<?> linkable) {
            if (linkable.isLinked()) {
                if (linkable.getLinkedDevice() instanceof ILinkable<?> linkableTarget)
                    linkableTarget.setLinkedDevice(null);
                linkable.setLinkedDevice(null);
            }
        }
        setChanged();
        return true;
    }

    @Override
    public void onGateBroken() {
        Stargate.super.onGateBroken();
        dropCamo();
    }

    @Override
    public void onGateUnmerged(boolean external) {
        if (getLevel() == null || getLevel().isClientSide()) return;
        if (external) {
            getMergeHelper().updateMemberStateAndCheck(false);
            generateMergeHelper();
        }
        var level = getLevel();
        if (level == null) return;
        setMerged(false);

        level.setBlockAndUpdate(this.getBlockPos(), getBlockState().setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY, true));
        if (level.getBlockState(getGateCenterPos()).getBlock() == Blocks.LIGHT)
            level.setBlock(getGateCenterPos(), Blocks.AIR.defaultBlockState(), 3);

        getStateManager().sendRenderingUpdate(StargateRendererActionState.EnumGateAction.GATE_RENDER_CHANGED, false);
        getDialingManager().onGateUnmerged();
        getSoundManager().updateWormholeSound(false);

        setChanged();
    }

    @Override
    public void onGateMerged() {
        setMerged(true);
        setChanged();
        var level = getLevel();
        if (level == null) return;
        level.setBlockAndUpdate(this.getBlockPos(), getBlockState().setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.RENDER_BLOCK_PROPERTY, false));
        var s = (StargateAbstractStateManager<?, ?>) getStateManager();
        s.sendRenderingUpdate(StargateRendererActionState.EnumGateAction.GATE_RENDER_CHANGED, true);
        s.getAndSendState(CoreStateTypes.RENDERER_STATE.get());
        JSGAdvancementsUtil.tryTriggerRangedAdvancement((Stargate<?>) this, JSGAdvancementsUtil.EnumAdvancementType.GATE_MERGE);
        refresh();
    }

    @Override
    protected boolean canBeUsedAsCamoBlock(BlockState blockState) {
        return JSGConfigUtil.canBeUsedAsCamoBlock(blockState);
    }

    @Override
    public void tryRegenerateStargateIfNeeded() {
        if (needRegenerate) {
            var pConfig = new IStargateGenerator.PlacementConfig();
            pConfig.baseInPlace = true;
            pConfig.stargateEnergyInternal = 10000;
            regenerateStargate(pConfig);
            needRegenerate = false;
            setChanged();
        }
    }

    @Override
    public boolean isBlackHoleEffected() {
        var l = getLevel();
        if (l == null) return false;
        var e = JSGDimensionConfig.INSTANCE.getConfigEntry(l.dimension());
        if (e == null) return false;
        return e.getBool("isBlackHoleDim", false);
    }

    @Override
    public ItemStack getAddressPage(SymbolType<?> symbolType, ItemStack defaultStack, int[] symbolsToDisplay) {
        ItemStack stack = defaultStack;
        var gateAddressMap = getAddressMap();

        if (stack.getItem() == JSGItems.UNIVERSE_DIALER.get()) {
            UniverseDialerModes.MEMORY.addEntry(gateAddressMap.get(symbolType), symbolsToDisplay, 0, stack);
        } else {
            stack = JSGNotebookPageTypes.STARGATE_ADDRESS.get().createPage(new StargateAddressData(new StargateAddressDynamic(gateAddressMap.get(symbolType)), symbolsToDisplay, getPointOfOrigin(symbolType)), PageNotebookItemFilled.getBiomeKeyFromWorld(getLevel(), getBlockPos()));
        }
        return stack;
    }

    @Override
    public void playPositionedSound(IPositionedSound positionedSound, boolean play) {
        var level = getLevel();
        if (level == null) return;
        JSGSoundHelper.playPositionedSound(level, getGateCenterPos(), positionedSound, play);
    }

    @Override
    public void playSoundEvent(ISoundEvent soundEnum, float pitch) {
        var level = getLevel();
        if (level == null) return;
        if (level.isClientSide())
            JSGSoundHelper.playSoundEventClientSide(level, getGateCenterPos(), soundEnum, pitch);
        else JSGSoundHelper.playSoundEvent(level, getGateCenterPos(), soundEnum, pitch);
    }

    public void createDeviceHolder() {
        computerDeviceHolder = new ComputerDeviceHolder(this);
    }

    @Override
    public ComputerDeviceHolder getDeviceHolder() {
        if (computerDeviceHolder == null) createDeviceHolder();
        return computerDeviceHolder;
    }

    @Override
    public String getDeviceType() {
        return "STARGATE_ABSTRACT";
    }

    // ------------------------------------------------------------------------
    // STATES

    @Override
    public final State getState(StateType stateType) {
        var pState = getStateManager().getState(stateType);
        if (pState != null) return pState;
        return super.getState(stateType);
    }

    @Override
    public final State createState(StateType stateType) {
        var pState = getStateManager().createState(stateType);
        if (pState != null) return pState;
        return super.createState(stateType);
    }

    @Override
    public final void setState(StateType stateType, State state) {
        getStateManager().setState(stateType, state);
        super.setState(stateType, state);
    }

    @Override
    public void sendState(StateType type, State state) {
        getStateManager().sendState(type, state);
    }

    @Override
    public PacketDistributor.TargetPoint getTargetPoint() {
        return getStateManager().getTargetPoint();
    }

    // ----------------------------------------------
    // BLOCK ENTITY METHODS

    @Override
    public AABB getRenderBoundingBox() {
        return relative(new JSGAxisAlignedBB(-4.5, -0.5, -0.5, 4.5, 8.5, 8).offset(new Vec3(0.5, 0.5, 0.5)), new Vec3(0.5, 0.5, 0.5));
    }

    @Override
    public void onLoad() {
        onStargateLoaded();
        super.onLoad();
    }

    @Override
    public void tick(Level level) {
        Stargate.super.tick(level);
        ScheduledTask.iterate(scheduledTasks, getTime());
        if (!level.isClientSide) {
            // This cannot be done in onLoad because it makes
            // Stargates invisible to the CC/OC network sometimes
            if (!addedToComputerNetwork) {
                addedToComputerNetwork = true;
                getDeviceHolder().connectToWirelessNetwork();
            }
            if (getTime() % 100 == 0)
                StargateComputerEvents.PING.get().sendVia(this);
        }
    }

    @Override
    public void onChunkUnloaded() {
        getRIGManager().onUnload();
        getDeviceHolder().disconnectFromWirelessNetwork();
        super.onChunkUnloaded();
    }

    @Override
    public void invalidateCaps() {
        getDeviceHolder().disconnectFromWirelessNetwork();
        super.invalidateCaps();
    }

    @Override
    public final <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        return getStargateCapability(capability, facing);
    }

    @Override
    public <T> LazyOptional<T> getStargateCapability(Capability<T> capability, @Nullable Direction facing) {
        var computerCaps = getDeviceHolder().getOrCreateDeviceBasedOnCap(capability);
        if (computerCaps.isPresent())
            return computerCaps;
        if (capability == ForgeCapabilities.ENERGY) {
            return LazyOptional.of(() -> getEnergyManager().getStorage()).cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    @Nullable
    public PointOfOrigin getPointOfOrigin(IPointOfOriginType pointOfOriginType) {
        return pointOfOriginType.getDefaultPoO();
    }

    @Override
    @Nullable
    public PointOfOrigin getPointOfOrigin(SymbolType<?> symbolType) {
        return getPointOfOrigin(symbolType.getPointOfOriginType());
    }

    // ----------------------------------------------
    // NBT

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.put("soundManager", getSoundManager().serializeNBT());
        compound.put("stargateEnergyManager", getEnergyManager().serializeNBT());
        compound.put("stargateDialingManager", getDialingManager().serializeNBT());
        compound.put("stateManager", getStateManager().serializeNBT());
        compound.put("eventHorizon", getEventHorizonManager().serializeNBT());
        compound.put("logManager", getLogManager().serializeNBT());

        compound.put("autoCloseManager", getAutoCloseManager().serializeNBT());
        compound.putBoolean("isMerged", isMerged());
        compound.put("listenerHandler", getListenerHandler().serializeNBT());

        compound.putBoolean("needRegenerate", needRegenerate);
        for (StargateAddress stargateAddress : gateAddressMap.values()) {
            compound.put("address_" + stargateAddress.getSymbolType(), stargateAddress.serializeNBT());
        }
        compound.put("scheduledTasks", ScheduledTask.serializeList(scheduledTasks));
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        if (compound.contains("soundManager")) {
            getSoundManager().deserializeNBT(compound.getCompound("soundManager"));
            getEnergyManager().deserializeNBT(compound.getCompound("stargateEnergyManager"));
            getDialingManager().deserializeNBT(compound.getCompound("stargateDialingManager"));
            getStateManager().deserializeNBT(compound.getCompound("stateManager"));
            getEventHorizonManager().deserializeNBT(compound.getCompound("eventHorizon"));
            getLogManager().deserializeNBT(compound.getCompound("logManager"));
        }
        getAutoCloseManager().deserializeNBT(compound.getCompound("autoCloseManager"));
        setMerged(compound.getBoolean("isMerged"));
        getListenerHandler().deserializeNBT(compound.getCompound("listenerHandler"));

        needRegenerate = compound.getBoolean("needRegenerate");
        for (SymbolType<?> symbolType : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
            if (compound.contains("address_" + symbolType))
                gateAddressMap.put(symbolType, new StargateAddress(compound.getCompound("address_" + symbolType)));
        }

        try {
            ScheduledTask.deserializeList(compound.getCompound("scheduledTasks"), scheduledTasks, this);
        } catch (NullPointerException | IndexOutOfBoundsException | ClassCastException e) {
            JSG.logger.warn("Exception at reading NBT");
            JSG.logger.warn("If loading world used with previous version and nothing game-breaking doesn't happen, please ignore it", e);
        }

        super.load(compound);
    }


    // ------------------------------------------------------------------------
    // Scheduled tasks

    protected List<ScheduledTask> scheduledTasks = new ArrayList<>();

    @Override
    public void addTask(ScheduledTask scheduledTask) {
        scheduledTask.setExecutor(this);
        scheduledTask.setTaskCreated(getTime());

        if (scheduledTask.getWaitTime() <= 0) {
            scheduledTask.execute();
            return;
        }

        scheduledTasks.add(scheduledTask);
        setChanged();
    }

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        if (level == null) return;
        if (scheduledTask == JSGScheduledTaskTypes.STARGATE_HORIZON_LIGHT_BLOCK.get()) {
            if (level.getBlockState(getGateCenterPos()).isAir())
                level.setBlockAndUpdate(getGateCenterPos(), Blocks.LIGHT.defaultBlockState().setValue(BlockStateProperties.LEVEL, 15));
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_LIGHTING_UPDATE_CLIENT.get()) {
            level.blockEntityChanged(getGateCenterPos());
        }
    }
}
