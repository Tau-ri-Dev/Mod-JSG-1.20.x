package dev.tauri.jsg.common.blockentity.stargate;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.block.stargate.IStargateBlock;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.power.PowerUtils;
import dev.tauri.jsg.api.registry.JSGScheduledTaskTypes;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.*;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.api.stargate.iris.codesender.CodeSender;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.result.StargateAddressCheckResult;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.util.IStargateGenerator;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateClassicRendererState;
import dev.tauri.jsg.common.block.stargate.redstone.AbstractStargateRedstoneIO;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.common.capability.JSGCapabilities;
import dev.tauri.jsg.common.helpers.StargateLinkingHelper;
import dev.tauri.jsg.common.helpers.StargateTemperatureHelper;
import dev.tauri.jsg.common.item.stargate.IrisItem;
import dev.tauri.jsg.common.jub.JUBDevice;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.common.registry.JSGItems;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.stargate.manager.StargateEnergyManager;
import dev.tauri.jsg.common.stargate.manager.StargateEventHorizonManager;
import dev.tauri.jsg.common.stargate.manager.StargateIrisManager;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.common.state.stargate.StargateBiomeOverrideState;
import dev.tauri.jsg.common.state.stargate.StargateRendererActionState;
import dev.tauri.jsg.common.worldgen.generator.StargateGenerator;
import dev.tauri.jsg.core.common.blockentity.IAddressProvider;
import dev.tauri.jsg.core.common.blockentity.ILinkable;
import dev.tauri.jsg.core.common.blockentity.IUpgradable;
import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.config.ingame.IConfigurable;
import dev.tauri.jsg.core.common.config.json.dimension.JSGDimensionConfig;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.ScheduledTask;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.helper.BlockHelper;
import dev.tauri.jsg.core.common.helper.LinkingHelper;
import dev.tauri.jsg.core.common.item.capacitor.CapacitorItemBlock;
import dev.tauri.jsg.core.common.loader.PointOfOriginsLoader;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.StateUpdateRequestToServer;
import dev.tauri.jsg.core.common.power.general.LargeEnergyStorage;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.registry.helper.FluidHelper;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.address.IAddress;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import dev.tauri.jsg.core.common.util.JSGItemStackHandler;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class StargateClassicBaseBE<S extends StargateClassicRendererState> extends StargateAbstractBaseBE<S, LargeEnergyStorage> implements StargateWithIris<LargeEnergyStorage>, ILinkable<DHDAbstractBE>, IUpgradable, IConfigurable, IAddressProvider {
    protected final StargateIrisManager irisManager = new StargateIrisManager(this);
    protected BlockPos linkedDHD = null;

    public StargateClassicBaseBE(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    // ----------------------------------------------
    // GETTERS

    @Override
    public StargateIrisManager getIrisManager() {
        return irisManager;
    }

    // ----------------------------------------------
    // EVENT HORIZON

    @Override
    public StargateEventHorizonManager createEventHorizonManager() {
        return new StargateEventHorizonManager(this) {
            @Override
            public JSGAxisAlignedBB getTeleportBox() {
                return new JSGAxisAlignedBB(-2.5, 1.5, -0.2, 2.5, 7, 0.2);
            }

            @Override
            public boolean getForceUnstable() {
                return (getConfig().getValueOrDefault(StargateConfigOptions.Classic.FORCE_UNSTABLE) ||
                        getDialingManager().getConnection().callConnected((conn, sg) -> {
                            if (sg instanceof StargateClassicBaseBE<?> classicBaseBE)
                                return classicBaseBE.getConfig().getValueOrDefault(StargateConfigOptions.Classic.FORCE_UNSTABLE);
                            return false;
                        }, () -> false));
            }

            @Override
            public int getKawooshSegmentsCount() {
                return 6;
            }

            @Override
            public List<JSGAxisAlignedBB> getGateVaporizingBoxes() {
                return Arrays.asList(
                        new JSGAxisAlignedBB(-1.5, 2.0, -0.5, 1.5, 7, 0.5),
                        new JSGAxisAlignedBB(-2.5, 2.0, -0.5, -1.5, 6, 0.5),
                        new JSGAxisAlignedBB(2.5, 2.0, -0.5, 1.5, 6, 0.5)
                );
            }

            @Override
            protected JSGAxisAlignedBB getHorizonKillingBox() {
                return new JSGAxisAlignedBB(-1.5, 2.5, 0, 1.5, 6.5, 7);
            }

            @Override
            protected void kawooshDestruction() {
                if (!getIrisManager().isIrisClosed() || getIrisManager().getIrisType() == EnumIrisType.NULL)
                    super.kawooshDestruction();
            }
        };
    }

    // ----------------------------------------------
    // LINKING

    protected abstract TagKey<Block> getLinkableTag();

    public void updateLinkStatus() {
        if (level == null) return;
        if (isLinked()) {
            Objects.requireNonNull(getLinkedDevice()).setLinkedDevice(null);
            setLinkedDevice(null);
        }
        BlockPos closestDhd = LinkingHelper.findClosestUnlinked(level, getBlockPos(), StargateLinkingHelper.getDhdRange(), getLinkableTag());

        if (closestDhd != null && level.getBlockEntity(closestDhd) instanceof DHDAbstractBE dhd) {
            dhd.setLinkedDevice(getBlockPos());
            setLinkedDevice(closestDhd);
            setChanged();
        }
    }

    public boolean isLinkedAndDHDOperational() {
        if (!isLinked()) return false;

        DHDAbstractBE dhdTile = getLinkedDevice();
        if (dhdTile == null) return false;
        return dhdTile.hasControlCrystal();
    }

    @Override
    public void setLinkedDevice(@Nullable BlockPos dhdPos) {
        this.linkedDHD = dhdPos;
        setChanged();
        if (getLevel() != null && !getLevel().isClientSide)
            sendLinkedDeviceToClients(getBlockPos(), getStateManager().getTargetPoint());
    }

    @Override
    public boolean canLinkTo() {
        return isMerged() && !isLinked();
    }

    @Nullable
    public DHDAbstractBE getLinkedDevice() {
        if (linkedDHD == null) return null;
        if (getLevel() == null) return null;
        if (getLevel().getBlockEntity(linkedDHD) instanceof DHDAbstractBE dhd) return dhd;
        return null;
    }

    @Nullable
    @Override
    public BlockPos getLinkedPos() {
        return linkedDHD;
    }

    @Override
    public int getMaxChevrons() {
        if (getDialingManager().getStargateState().dialingComputer()) return 9;
        if (isLinkedAndDHDOperational()) {
            return getLinkedDevice() != null && !getLinkedDevice().hasUpgrade(DHDAbstractBE.DHDUpgradeEnum.CHEVRON_UPGRADE) ? 7 : 9;
        }
        return 9;
    }

    @Override
    @Nullable
    public PointOfOrigin getPointOfOrigin(IPointOfOriginType pointOfOriginType) {
        var originRL = getConfig().wasValueChanged(StargateConfigOptions.Classic.POINT_OF_ORIGIN) ? getConfig().getValueOrDefault(StargateConfigOptions.Classic.POINT_OF_ORIGIN) : JSGMapping.rl("empty");
        return PointOfOriginsLoader.INSTANCE.getOriginByIdOrElse(pointOfOriginType, originRL, () ->
                Optional.ofNullable(getLevel() == null ? null : Stargate.getOriginFor(pointOfOriginType, getLevel().dimension(), getBiomeOverlayWithOverride())).orElse(super.getPointOfOrigin(pointOfOriginType)));
    }

    @Override
    public void initStargatePos() {
        StargatePos oldPos = stargatePos;

        if (getLevel() == null) return;
        StargatePos gatePos = new StargatePos(getLevel().dimension(), Optional.ofNullable(getFakeWorld()).orElse(getLevel().dimension()), getBlockPos(), getFakePos(), getSymbolType(), getStargateType());
        if (oldPos != null)
            gatePos.setName(oldPos.getName());
        stargatePos = gatePos;
        setChanged();
    }

    // ----------------------------------------------

    protected double lastIrisHeat = -2;
    protected double lastGateHeat = -2;
    public double irisHeat;
    public double gateHeat;
    public static final double IRIS_MAX_HEAT_TITANIUM = JSGConfig.Stargate.irisTitaniumMaxHeat.get();
    public static final double IRIS_MAX_HEAT_TRINIUM = JSGConfig.Stargate.irisTriniumMaxHeat.get();
    public static final double GATE_MAX_HEAT = JSGConfig.Stargate.gateMaxHeat.get();

    public void tryHeatUp(boolean byIrisHit, double irisHeatUpCoefficient) {
        tryHeatUp(byIrisHit, false, 1, irisHeatUpCoefficient, 1, -1, -1);
    }

    public void tryHeatUp(double gateHeatUpCoefficient) {
        tryHeatUp(false, true, gateHeatUpCoefficient, 1, 1, -1, -1);
    }

    public double getMaxIrisHeat() {
        if (getIrisManager().hasShield() || getIrisManager().hasCreativeIris()) return Double.MAX_VALUE;
        return (getIrisManager().getIrisType() == EnumIrisType.IRIS_TRINIUM ? IRIS_MAX_HEAT_TRINIUM : IRIS_MAX_HEAT_TITANIUM);
    }

    @SuppressWarnings("unused")
    public double getMaxGateHeat() {
        return GATE_MAX_HEAT;
    }

    public void tryHeatUp(boolean heatUpIris, boolean heatUpGate, double gateHeatUpCoefficient, double irisHeatUpCoefficient, double coolDownCoefficient, double maxHeatByAround, double minHeatByAround) {
        final double heatUpCoefficientConst = 0.7;
        final double coolDownCoefficientConst = 0.3;

        if ((heatUpGate || Math.abs(gateHeat - irisHeat) >= 50) && (maxHeatByAround == -1 || (gateHeat + (heatUpCoefficientConst * gateHeatUpCoefficient)) <= maxHeatByAround))
            gateHeat += (heatUpCoefficientConst * gateHeatUpCoefficient);
        if ((minHeatByAround == -1 || (gateHeat - (coolDownCoefficientConst * coolDownCoefficient)) > minHeatByAround))
            gateHeat -= (coolDownCoefficientConst * coolDownCoefficient);

        if ((heatUpIris || Math.abs(gateHeat - irisHeat) >= 25) && (maxHeatByAround == -1 || (irisHeat + (heatUpCoefficientConst * irisHeatUpCoefficient)) <= maxHeatByAround))
            irisHeat += (heatUpCoefficientConst * irisHeatUpCoefficient);

        if ((minHeatByAround == -1 || (irisHeat - (coolDownCoefficientConst * coolDownCoefficient)) > minHeatByAround))
            irisHeat -= (coolDownCoefficientConst * coolDownCoefficient);

        // iris breaking
        ItemStack irisItem = getIrisManager().getIrisItem();
        double maxHeat = getMaxIrisHeat();
        if (irisHeat >= maxHeat) {
            int heatCoefficient = (int) Math.round(Math.abs(irisHeat - maxHeat));
            if (JSGConfig.Stargate.enableIrisOverHeatCollapse.get()) {
                if (getTime() % (((int) (Math.random() * 70)) + 1) == 0) {
                    if (getIrisManager().hasPhysicalIris() && irisItem.isDamageableItem()) {
                        irisItem.getItem().setDamage(irisItem, irisItem.getItem().getDamage(irisItem) + (new Random().nextInt(heatCoefficient) + 1));
                        if (irisItem.getCount() == 0)
                            getIrisManager().updateIrisType();
                        JSGSoundHelper.playSoundEvent(level, getGateCenterPos(), JSGSoundEvents.IRIS_HIT);
                    }
                }
            }
        }

        // gate explosion
        if (gateHeat >= GATE_MAX_HEAT) {
            if (level == null) return;
            if (JSGConfig.Stargate.enableGateOverHeatExplosion.get())
                level.explode(null, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 60, false, Level.ExplosionInteraction.BLOCK);
            //gateHeat = GATE_MAX_HEAT;
        }

        // send render update about temperature -> to color the gate red
        if (getTime() % 20 == 0) {// every second send a render_update packet to client if heat changed
            if (lastIrisHeat != irisHeat || lastGateHeat != gateHeat) {
                lastIrisHeat = irisHeat;
                lastGateHeat = gateHeat;
                setChanged();
                sendState(CoreStateTypes.RENDERER_UPDATE.get(), new StargateRendererActionState(irisHeat, gateHeat));
            }
        }

        setChanged();
    }

    public double getTemperatureAroundGate() {
        return StargateTemperatureHelper.getTemperatureAroundGate(this);
    }

    @Override
    public boolean receiveIrisCode(CodeSender sender, String code) {
        return getIrisManager().receiveIrisCode(sender, code);
    }

    @Override
    public boolean isGateBurried() {
        if (level == null) return false;
        if (!getConfig().getValueOrDefault(StargateConfigOptions.Classic.ENABLE_BURY_STATE)) return false;
        for (BlockPos targetPos : getIrisManager().getIrisBlocksPattern()) {
            BlockPos newPos = relative(targetPos); //BlockPosHelper.rotate(targetPos, getFacing(), getFacingVertical()).offset(getBlockPos());
            BlockState state = level.getBlockState(newPos);
            if (state.getBlock() instanceof IStargateBlock) continue;
            if (state.getBlock() instanceof LiquidBlock || FluidHelper.isLiquidBlock(state))
                return false;
            if (state.getBlock() instanceof AirBlock || state.getBlock() == Blocks.LIGHT || state.getBlock() == JSGBlocks.IRIS_BLOCK.get() || state.getBlock() == CoreBlocks.INVISIBLE_BLOCK.get())
                return false;
            if (state.canBeReplaced())
                return false;
        }
        return true;
    }

    @Override
    public void onGateBroken() {
        super.onGateBroken();
        getIrisManager().onUnmerged();
        getSoundManager().updateRingRollSound(false);

        if (isLinked() && getLinkedDevice() != null) {
            getLinkedDevice().clearSymbols();
            getLinkedDevice().setLinkedDevice(null);
            setLinkedDevice(null);
        }
    }

    @Override
    public void onGateMerged() {
        super.onGateMerged();

        getIrisManager().updateIrisType();
        double heat = getTemperatureAroundGate();
        gateHeat = heat;
        irisHeat = heat;
        setChanged();
        sendState(CoreStateTypes.GUI_STATE.get(), getState(CoreStateTypes.GUI_STATE.get()));
        updateLinkStatus();
    }

    @Override
    @Nullable
    public IAddress getAddress(SymbolType<?> symbolType) {
        if (level == null || level.isClientSide) return gateAddressMapClient.get(symbolType);
        return getStargateAddress(symbolType);
    }

    // ------------------------------------------------------------------------
    // Loading and ticking

    @Override
    public void regenerateStargate(IStargateGenerator.PlacementConfig pConfig) {
        if (getLevel() == null) return;
        var world = getLevel();
        if (world.isClientSide) return;
        JSG.logger.info("Regenerating stargate at {} in {}", getBlockPos(), world.dimension().location());
        updateLinkStatus();

        pConfig.world = world;
        pConfig.gateBasePos = this.getBlockPos();
        pConfig.stargateConfig = (c) -> this.getConfig();
        pConfig.gateFacing = mergeHelper.horizontalFacing;
        pConfig.gateVerticalFacing = mergeHelper.verticalFacing;
        pConfig.gateType = getStargateType();
        pConfig.overlay = getBiomeOverlayWithOverride();

        if (world.random.nextFloat() < 0.3f)
            pConfig.upgrades.add(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_TYPE);

        if (isLinked()) {
            pConfig.dhdPos = getLinkedPos();
            if (world.random.nextFloat() < 0.3f)
                pConfig.upgrades.add(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_DHD);
            pConfig.dhdFluid = (int) (((JSGConfig.DialHomeDevice.fluidCapacity.get() - (JSGConfig.DialHomeDevice.fluidCapacity.get() * 0.1f)) * world.random.nextFloat()) + (JSGConfig.DialHomeDevice.fluidCapacity.get() * 0.1f));
        }

        var generator = new StargateGenerator();
        generator.setStargateEnergyInternalSmart(pConfig, (int) (JSGConfig.Stargate.stargateEnergyStorage.get() * world.random.nextFloat()));
        generator.generateStargate(pConfig, false);
        generateAddresses(true);
        updateLinkStatus();
        setChanged();
    }

    @Override
    public void onStargateLoaded() {
        if (getLevel() == null) return;
        var world = getLevel();
        if (world.isClientSide) {
            JSGCorePacketHandler.sendToServer(new StateUpdateRequestToServer(getBlockPos(), CoreStateTypes.GUI_STATE.get()));
            requestLinkedDeviceFromServer(getBlockPos());
        }
        super.onStargateLoaded();
        updateItemHandlerSize();
        fixItemHandler();
        if (!world.isClientSide) {
            updatePowerTier();

            getIrisManager().onLoad();
            sendState(CoreStateTypes.BIOME_OVERRIDE_STATE.get(), new StargateBiomeOverrideState(determineBiomeOverride()));

            this.lastFakeWorld = getFakeWorld();
            this.lastFakePos = getFakePos();
            setChanged();
        }
    }

    protected BlockPos lastPos = BlockPos.ZERO;

    /*
     * Stargate Incoming Animations Helper
     */

    @Override
    public boolean isRIGAllowed() {
        return getConfig().getValueOrDefault(StargateConfigOptions.Classic.ALLOW_RIG);
    }

    @Override
    public void tick(Level level) {
        super.tick(level);
        updateDevices();
        if (lastFakeWorld == null) lastFakeWorld = getFakeWorld();
        if (lastFakePos == null) lastFakePos = getFakePos();

        if (level.isClientSide) {
            // Client -> request to update client config
            if (getConfig().getOptions().isEmpty()) {
                JSGCorePacketHandler.sendToServer(new StateUpdateRequestToServer(getBlockPos(), CoreStateTypes.GUI_STATE.get()));
            }
        }
        getIrisManager().tick(level);


        // Checking lastFakePos and lastFakeWorld (if changed, close the gate if its open (gate was probably warped))
        if (!level.isClientSide) {
            if ((lastFakePos != getFakePos() || lastFakeWorld != getFakeWorld())) {
                if (!getDialingManager().getConnection().getStatus().none() && !getDialingManager().getConnection().getStatus().full()) {
                    getDialingManager().abortDialingSequence();
                    lastFakePos = getFakePos();
                    lastFakeWorld = getFakeWorld();
                    setChanged();
                } else if (getDialingManager().getConnection().getStatus().full()) {
                    JSG.logger.warn("A stargateState indicates the Gate at {} should be open, but gate was warped! Closing gate...", blockPosition().toString());
                    getDialingManager().attemptClose(StargateClosedReasonEnum.CONNECTION_LOST);
                    lastFakePos = getFakePos();
                    lastFakeWorld = getFakeWorld();
                    setChanged();
                } else {
                    lastFakePos = getFakePos();
                    lastFakeWorld = getFakeWorld();
                    setChanged();
                }
            }
        }

        // Charging gate with lighting bold
        if (!level.isClientSide && isMerged()) {
            BlockPos topBlock = getMergeHelper().getTopBlock();
            if (topBlock != null) {
                if (level.isThundering() && level.isRaining() && level.isRainingAt(topBlock.above())) {
                    Random rand = new Random();
                    float chance = rand.nextFloat();
                    var entry = JSGDimensionConfig.INSTANCE.getConfigEntry(level.dimension());
                    if (entry != null && chance < (entry.getFloat("lightingBoltChance", 0) * 0.0005) && BlockHelper.isBlockDirectlyUnderSky(level, topBlock)) {
                        int max = JSGConfig.Stargate.stargateEnergyStorage.get() / 17;
                        int min = max / 6;
                        int energy = (int) ((rand.nextFloat() * (max - min)) + min);
                        getEnergyManager().getStorage().receiveEnergy(energy, false);
                        LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(level);
                        if (lightningbolt != null) {
                            lightningbolt.moveTo(Vec3.atBottomCenterOf(topBlock));
                            level.addFreshEntity(lightningbolt);
                        }
                    }
                }
            }
        }


        /*
         * =========================================================================
         * HEATING UP System
         */
        if (!level.isClientSide && isMerged() && level.getGameTime() % 20 == 0) {

            double middleTemperature = getTemperatureAroundGate();

            double c = Math.min(1, Math.abs(gateHeat - middleTemperature) / 20);

            tryHeatUp(false, true, c, c, c, middleTemperature, middleTemperature);
            if (!getIrisManager().hasIris()) {
                irisHeat = -1;
                setChanged();
            }
        }

        if (!level.isClientSide) {

            if (!lastPos.equals(getBlockPos())) {
                lastPos = getBlockPos();

                if (isMerged()) {
                    getMergeHelper().updateMemberStateAndCheck(null);
                }
            }

            if (givePageTask != null) {
                if (givePageTask.update(getTime())) {
                    givePageTask = null;
                }
            }

            if (doPageProgress) {
                if (getTime() % 2 == 0) {
                    pageProgress++;

                    if (pageProgress > 18) {
                        pageProgress = 0;
                        doPageProgress = false;
                    }
                }

                if (itemStackHandler.getStackInSlot(pageSlotId).isEmpty()) {
                    lockPage = false;
                    doPageProgress = false;
                    pageProgress = 0;
                    givePageTask = null;
                }
            } else {
                if (lockPage && itemStackHandler.getStackInSlot(pageSlotId).isEmpty()) {
                    lockPage = false;
                }

                if (!lockPage) {
                    var i = 9;
                    for (var symbolType : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
                        if (!itemStackHandler.getStackInSlot(i).isEmpty()) {
                            doPageProgress = true;
                            lockPage = true;
                            pageSlotId = i;
                            pageSymboltype = symbolType;
                            givePageTask = new ScheduledTask(JSGScheduledTaskTypes.STARGATE_GIVE_PAGE, 36);
                            givePageTask.setTaskCreated(getTime());
                            givePageTask.setExecutor(this);
                            break;
                        }
                        i++;
                    }
                }
            }
        }

        if (!lastPos.equals(getBlockPos())) {
            lastPos = getBlockPos();

            updateLinkStatus();
            setChanged();
        }
    }

    // Server
    @Nullable
    private BiomeOverlayInstance determineBiomeOverride() {
        ItemStack stack = itemStackHandler.getStackInSlot(7);

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
    public BiomeOverlayInstance getBiomeOverlayWithOverride() {
        var overrideOverlay = determineBiomeOverride();
        if (overrideOverlay != null) return overrideOverlay;
        return super.getBiomeOverlayWithOverride();
    }

    // ------------------------------------------------------------------------
    // NBT

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.put("irisManager", irisManager.serializeNBT());
        if (isLinked(true)) {
            compound.putLong("linkedDHD", linkedDHD.asLong());
        }
        compound.put("itemHandler", itemStackHandler.serializeNBT());

        compound.put("config", getConfig().serializeNBT());

        compound.putDouble("irisHeat", irisHeat);
        compound.putDouble("lastIrisHeat", lastIrisHeat);
        compound.putDouble("gateHeat", gateHeat);
        compound.putDouble("lastGateHeat", lastGateHeat);

        if (lastFakePos != null)
            compound.putLong("lastFakePos", lastFakePos.asLong());
        if (lastFakeWorld != null)
            compound.putString("lastFakeWorld", lastFakeWorld.location().toString());

        compound.putInt("redstoneIODevice_size", REDSTONE_IO_BLOCKS.size());
        for (int i = 0; i < REDSTONE_IO_BLOCKS.size(); i++) {
            compound.putLong("redstoneIODevice_pos" + i, REDSTONE_IO_BLOCKS.get(i).asLong());
        }

        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        irisManager.deserializeNBT(compound.getCompound("irisManager"));
        if (compound.contains("linkedDHD"))
            linkedDHD = BlockPos.of(compound.getLong("linkedDHD"));

        itemStackHandler.deserializeNBT(compound.getCompound("itemHandler"));

        getConfig().deserializeNBT(compound.getCompound("config"));

        this.irisHeat = compound.getDouble("irisHeat");
        this.lastIrisHeat = compound.getDouble("lastIrisHeat");
        this.gateHeat = compound.getDouble("gateHeat");
        this.lastGateHeat = compound.getDouble("lastGateHeat");

        if (compound.contains("lastFakePos"))
            this.lastFakePos = BlockPos.of(compound.getLong("lastFakePos"));
        if (compound.contains("lastFakeWorld"))
            this.lastFakeWorld = ResourceKey.create(Registries.DIMENSION, (JSGMapping.rl(compound.getString("lastFakeWorld"))));

        int size = compound.getInt("redstoneIODevice_size");
        REDSTONE_IO_BLOCKS.clear();
        for (int i = 0; i < size; i++) {
            REDSTONE_IO_BLOCKS.add(BlockPos.of(compound.getLong("redstoneIODevice_pos" + i)));
        }
        updatePowerTier();
    }

    // -----------------------------------------------------------------
    // Tile entity config

    protected final BEConfig config = new BEConfig(this::setChanged, getStargateType().configOptionsHolder.get());

    @Override
    public BEConfig getConfig() {
        return this.config;
    }

    @Override
    public void onConfigUpdated() {
        setChanged();
        if (getLevel() == null || getLevel().isClientSide()) return;
        sendState(CoreStateTypes.GUI_STATE.get(), getState(CoreStateTypes.GUI_STATE.get()));
    }

    // -----------------------------------------------------------------
    // Scheduled tasks

    @Override
    public void executeTask(ScheduledTaskType scheduledTask, CompoundTag customData) {
        if (scheduledTask == JSGScheduledTaskTypes.STARGATE_HORIZON_LIGHT_BLOCK.get()) {
            var iris = getIrisManager();
            if (iris.getIrisType() == EnumIrisType.NULL || iris.getIrisType() == EnumIrisType.SHIELD || !iris.isIrisClosed()) {
                super.executeTask(scheduledTask, customData);
            }
        } else if (scheduledTask == JSGScheduledTaskTypes.STARGATE_GIVE_PAGE.get()) {
            if (pageSlotId < 9 || pageSymboltype == null) return;
            ItemStack stack = itemStackHandler.getStackInSlot(pageSlotId);
            stack = getAddressPage(pageSymboltype, stack, (hasUpgrade(StargateUpgrade.CHEVRON_UPGRADE) ? new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9} : new int[]{1, 2, 3, 4, 5, 6, 9}));
            itemStackHandler.setStackInSlot(pageSlotId, stack);
        } else
            super.executeTask(scheduledTask, customData);
    }


    // ------------------------------------------------------------------------
    // Ring spinning

    public float getSpeedFactor() {
        return getConfig().getValueOrDefault(StargateConfigOptions.Classic.SPIN_SPEED) / 100f;
    }

    // -----------------------------------------------------------------------------
    // Page conversion

    private short pageProgress = 0;
    private int pageSlotId;
    private SymbolType<?> pageSymboltype;
    private boolean doPageProgress;
    private ScheduledTask givePageTask;
    private boolean lockPage;

    public int getPageProgress() {
        return pageProgress;
    }

    public void setPageProgress(int pageProgress) {
        this.pageProgress = (short) pageProgress;
    }

    // -----------------------------------------------------------------------------
    // Item handler

    public static final int BIOME_OVERRIDE_SLOT = 10;

    @Override
    public void updateContainerItemsByItemStack(ItemStack stack) {
        super.updateContainerItemsByItemStack(stack);
        var tag = stack.getOrCreateTag();
        if (tag.contains("config"))
            getConfig().deserializeNBT(tag.getCompound("config"));
        if (!tag.contains("itemHandler")) return;
        updateItemHandlerSize();
        itemStackHandler.deserializeNBT(tag.getCompound("itemHandler"));
        updatePowerTier();
        getIrisManager().updateIrisType();
        sendState(CoreStateTypes.BIOME_OVERRIDE_STATE.get(), new StargateBiomeOverrideState(determineBiomeOverride()));
        setChanged();
    }

    protected void updateItemHandlerSize() {
        if (itemStackHandler.getSize() != (9 + SymbolType.values(JSGSymbolUsages.STARGATES.get()).size())) {
            itemStackHandler.setSize(9 + SymbolType.values(JSGSymbolUsages.STARGATES.get()).size());
        }
    }

    protected void fixItemHandler() {
        if (itemStackHandler == null) return;
        if (itemStackHandler.getSize() < 12) return;
        var irisSlotOld = itemStackHandler.getStackInSlot(11);
        if (irisSlotOld.getItem() instanceof IrisItem) {
            var page8 = itemStackHandler.getStackInSlot(8);
            itemStackHandler.setStackInSlot(8, irisSlotOld);
            itemStackHandler.setStackInSlot(11, page8);
        }
        var biomeSlotOld = itemStackHandler.getStackInSlot(10);
        if ((!biomeSlotOld.isEmpty() && !biomeSlotOld.is(CoreItems.NOTEBOOK_PAGE_EMPTY.get()) && !biomeSlotOld.is(CoreItems.NOTEBOOK_PAGE_FILLED.get()))) {
            var page7 = itemStackHandler.getStackInSlot(7);
            itemStackHandler.setStackInSlot(7, biomeSlotOld);
            itemStackHandler.setStackInSlot(10, page7);
        }
    }

    @Override
    public ItemStack getDropBaseBlock(ServerPlayer player) {
        var stack = super.getDropBaseBlock(player);
        var tag = stack.getOrCreateTag();
        tag.put("itemHandler", itemStackHandler.serializeNBT());
        tag.put("config", getConfig().serializeNBT());
        stack.setTag(tag);
        return stack;
    }

    private final JSGItemStackHandler itemStackHandler = new JSGItemStackHandler(8 + SymbolType.values(JSGSymbolUsages.STARGATES.get()).size()) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            Item item = stack.getItem();
            boolean isItemCapacitor = (item instanceof CapacitorItemBlock);
            return switch (slot) {
                case 0, 1, 2, 3 -> StargateUpgrade.contains(item) && !hasUpgrade(item);
                case 4 -> isItemCapacitor && getSupportedCapacitors() >= 1;
                case 5 -> isItemCapacitor && getSupportedCapacitors() >= 2;
                case 6 -> isItemCapacitor && getSupportedCapacitors() >= 3;
                case 7 -> {
                    BiomeOverlayInstance override = BiomeOverlayInstance.getBiomeOverlayByItem(stack, true);
                    yield override != null && getSupportedOverlays().stream().map(Supplier::get).toList().contains(override);
                }
                case 8 -> getIrisManager().canInsertItemAsIris(item);
                default -> {
                    if (slot < 9) yield true;
                    var i = 9;
                    for (var symbolType : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
                        if (i > slot) yield false;
                        if (slot == i) {
                            if (item == CoreItems.NOTEBOOK_PAGE_EMPTY.get() || item == CoreItems.NOTEBOOK_PAGE_FILLED.get())
                                yield true;
                            if (symbolType == JSGSymbolTypes.UNIVERSE.get() && item == JSGItems.UNIVERSE_DIALER.get())
                                yield true;
                        }
                        i++;
                    }
                    yield false;
                }
            };
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (getLevel() == null || getLevel().isClientSide()) return;

            switch (slot) {
                case 4:
                case 5:
                case 6:
                    updatePowerTier();
                    break;

                case 7:
                    sendState(CoreStateTypes.BIOME_OVERRIDE_STATE.get(), new StargateBiomeOverrideState(determineBiomeOverride()));
                    break;
                // iris update state
                case 8:
                    getIrisManager().updateIrisType();
                    break;
                default:
                    break;
            }

            setChanged();
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            if (getStargateLevel() == null || getStargateLevel().isClientSide()) return;
            sendState(CoreStateTypes.BIOME_OVERRIDE_STATE.get(), new StargateBiomeOverrideState(determineBiomeOverride()));
            getIrisManager().updateIrisType();
            updatePowerTier();
        }
    };

    public int getSupportedCapacitors() {
        return getConfig().getValueOrDefault(StargateConfigOptions.Classic.MAX_CAPACITORS);
    }

// -----------------------------------------------------------

    private static final List<Integer> UPGRADE_SLOTS_IDS = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));

    @Override
    public Iterator<Integer> getUpgradeSlotsIterator() {
        return UPGRADE_SLOTS_IDS.iterator();
    }

    // -----------------------------------------------------------------------------
    // Power system


    protected final LargeEnergyStorage energyStorage = PowerUtils.getLarge(this::setChanged);

    @Override
    public StargateEnergyManager<?, LargeEnergyStorage> createEnergyManager() {
        return new StargateEnergyManager<>(this) {
            @Override
            public LargeEnergyStorage getStorage() {
                return energyStorage;
            }
        };
    }

    public int currentPowerTier = 1;

    public int getPowerTier() {
        return currentPowerTier;
    }

    public void updatePowerTier() {
        int powerTier = 1;

        for (int i = 4; i < 7; i++) {
            if (!itemStackHandler.getStackInSlot(i).isEmpty()) {
                powerTier++;
            }
        }

        if (powerTier != currentPowerTier) {
            currentPowerTier = powerTier;

            energyStorage.clearStorages();

            for (int i = 4; i < 7; i++) {
                ItemStack stack = itemStackHandler.getStackInSlot(i);

                if (!stack.isEmpty()) {
                    LazyOptional<IEnergyStorage> capCapability = stack.getCapability(ForgeCapabilities.ENERGY, null);
                    if (capCapability.isPresent() && capCapability.resolve().isPresent()) {
                        energyStorage.addStorage(capCapability.resolve().get());
                    }
                }
            }
            if (getLevel() != null && !getLevel().isClientSide())
                JSG.logger.debug("Updated to power tier: {}", powerTier);
        }
    }


// -----------------------------------------------------------------------------
// Capabilities

    @Override
    public <T> LazyOptional<T> getStargateCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> itemStackHandler).cast();
        }
        if (capability == JSGCapabilities.JUST_UNIVERSAL_BUS) {
            return LazyOptional.of(() -> jubDevice).cast();
        }
        return super.getStargateCapability(capability, facing);
    }

    @Override
    public void refresh() {
        super.refresh();
        this.lastFakeWorld = getFakeWorld();
        this.lastFakePos = getFakePos();
        setChanged();
    }

    protected ResourceKey<Level> lastFakeWorld = null;
    protected BlockPos lastFakePos = getFakePos();

    @Nullable
    public ResourceKey<Level> getFakeWorld() {
        if (getLevel() == null) return null;
        return getLevel().dimension();
    }

    @SuppressWarnings("unused")
    public void setFakeWorld(Level world) {
    }

    public BlockPos getFakePos() {
        return getBlockPos();
    }

    @SuppressWarnings("unused")
    public void setFakePos(@Nullable BlockPos pos) {
    }

    public ArrayList<NearbyGate> getNearbyGates() {
        return getNearbyGates(null, true, true);
    }

    public ArrayList<NearbyGate> getNearbyGates(@Nullable StargateType<?> gateType, boolean checkStargateType, boolean checkAddressAndEnergy) {
        if (gateType == null) gateType = getStargateType();
        double squaredGate = (double) JSGConfig.Stargate.universeGateNearbyReach.get() * JSGConfig.Stargate.universeGateNearbyReach.get();

        ArrayList<NearbyGate> addresses = new ArrayList<>();

        var level = getLevel();
        if (level == null) return addresses;
        var server = level.getServer();
        if (server == null) return addresses;

        for (var entry : StargateNetwork.INSTANCE.getAll().entrySet()) {
            StargatePos stargatePos = entry.getKey();
            var type = stargatePos.getStargateType();
            if (!type.isClassic) continue;
            if (checkStargateType && gateType != type) continue;

            ResourceKey<Level> targetDim = stargatePos.fakeGateDimension;
            BlockPos targetFoundPos = stargatePos.fakeGatePos;

            if (targetDim != getFakeWorld())
                continue;

            if (targetFoundPos.distSqr(getFakePos()) > squaredGate)
                continue;

            if (stargatePos.gatePos.equals(getBlockPos()) && stargatePos.dimension == level.dimension())
                continue;

            int symbolsNeeded = getDialingManager().getMinimalSymbolsToDial(stargatePos.getGateSymbolType(), stargatePos);

            if (checkAddressAndEnergy) {
                StargateAddressDynamic checkingAddress = new StargateAddressDynamic(getSymbolType());
                checkingAddress.addAll(entry.getValue().get(getSymbolType()).subList(0, (symbolsNeeded - 1)));
                checkingAddress.addSymbol(stargatePos.getGateSymbolType().getOrigin());
                if (getDialingManager().checkAddressAndEnergyRequirements(checkingAddress, false) == StargateAddressCheckResult.OK)
                    addresses.add(new NearbyGate(entry.getValue().get(getSymbolType()), symbolsNeeded, stargatePos.getStargateType()));
            } else
                addresses.add(new NearbyGate(entry.getValue().get(getSymbolType()), symbolsNeeded, stargatePos.getStargateType()));
        }
        return addresses;
    }

    /*
        REDSTONE I/O TICKING
     */
    protected final List<BlockPos> REDSTONE_IO_BLOCKS = new ArrayList<>();

    public void addRedstoneDevice(BlockPos pos) {
        REDSTONE_IO_BLOCKS.add(pos);
        setChanged();
    }

    public void removeRedstoneDevice(BlockPos pos) {
        REDSTONE_IO_BLOCKS.remove(pos);
        setChanged();
    }

    public void updateDevices() {
        if (level == null || level.isClientSide) return;
        var level = (ServerLevel) this.level;
        for (var pos : REDSTONE_IO_BLOCKS) {
            var state = level.getBlockState(pos);
            var block = state.getBlock();
            if (block instanceof AbstractStargateRedstoneIO ioBlock)
                ioBlock.tickFromStargate(state, level, pos, this);
        }
    }

    // ----------------------------------------------
// OC/CC
    @Override
    public String getDeviceType() {
        return "STARGATE_CLASSIC";
    }

    // ----------------------------------------------
// JUB
    public JUBDevice jubDevice = new JUBDevice(this) {
        @Nullable
        public IEnergyStorage getEnergyStorage() {
            return StargateClassicBaseBE.this.getEnergyManager().getStorage();
        }

        @Override
        public void onChanged() {
            setChanged();
        }

        @Override
        protected void packetReceived(String name, Object data, JUBDevice sender) {
            JSG.logger.info("Got packet {}!", name);
        }
    };
}
