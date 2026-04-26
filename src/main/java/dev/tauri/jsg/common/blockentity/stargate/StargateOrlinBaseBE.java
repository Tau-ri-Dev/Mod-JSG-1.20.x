package dev.tauri.jsg.common.blockentity.stargate;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.power.PowerUtils;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.api.sound.StargateSoundEventEnum;
import dev.tauri.jsg.api.sound.StargateSoundPositionedEnum;
import dev.tauri.jsg.api.stargate.StargateClosedReasonEnum;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.api.util.IStargateGenerator;
import dev.tauri.jsg.client.renderer.blockentity.stargate.StargateOrlinRendererState;
import dev.tauri.jsg.common.multistructure.mergehelper.StargateOrlinMergeHelper;
import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.common.registry.JSGSoundEvents;
import dev.tauri.jsg.common.stargate.manager.StargateEnergyManager;
import dev.tauri.jsg.common.stargate.manager.StargateEventHorizonManager;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateAbstractDialingManager;
import dev.tauri.jsg.common.stargate.manager.dialing.StargateOrlinDialingManager;
import dev.tauri.jsg.common.stargate.manager.state.StargateAbstractStateManager;
import dev.tauri.jsg.common.stargate.manager.state.StargateOrlinStateManager;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.common.worldgen.generator.StargateGenerator;
import dev.tauri.jsg.core.common.blockentity.ILinkable;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.power.general.EnergyRequiredToOperate;
import dev.tauri.jsg.core.common.power.general.SmallEnergyStorage;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.sound.PositionedSound;
import dev.tauri.jsg.core.common.sound.SoundEvent;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StargateOrlinBaseBE extends StargateAbstractBaseBE<StargateOrlinRendererState, SmallEnergyStorage> {
    public boolean canNotGenerate = false;
    private int openCount = 0;
    private boolean isPowered;
    private final SmallEnergyStorage energyStorage = PowerUtils.getSmall(this::setChanged);

    public StargateOrlinBaseBE(BlockPos pos, BlockState state) {
        super(JSGBlockEntities.STARGATE_ORLIN_BASE_BE.get(), pos, state);
    }

    @Override
    public StargateAbstractStateManager<StargateOrlinBaseBE, StargateOrlinRendererState> createStateManager() {
        return new StargateOrlinStateManager(this);
    }

    @Override
    public StargateAbstractDialingManager<StargateOrlinBaseBE> createDialingManager() {
        return new StargateOrlinDialingManager(this);
    }

    @Override
    public StargateOrlinDialingManager getDialingManager() {
        return (StargateOrlinDialingManager) super.getDialingManager();
    }

    @Override
    public StargateEnergyManager<StargateOrlinBaseBE, SmallEnergyStorage> createEnergyManager() {
        return new StargateEnergyManager<>(this) {
            @Override
            public SmallEnergyStorage getStorage() {
                return energyStorage;
            }

            @Override
            public EnergyRequiredToOperate getEnergyRequiredToDial(@Nullable StargatePos targetGatePos, StargateAddressDynamic address) {
                return super.getEnergyRequiredToDial(targetGatePos, address).mul(JSGConfig.Stargate.stargateOrlinEnergyMul.get()).cap(JSGConfig.Stargate.stargateEnergyStorage.get() / 4 - 1000000);
            }
        };
    }

    @Override
    public StargateEventHorizonManager createEventHorizonManager() {
        return new StargateEventHorizonManager(this) {
            @Override
            public JSGAxisAlignedBB getTeleportBox() {
                return new JSGAxisAlignedBB(-1.0, 0.6, -0.15, 1.0, 2.7, -0.05);
            }

            @Override
            public boolean getForceUnstable() {
                return false;
            }

            @Override
            public int getKawooshSegmentsCount() {
                return 2;
            }

            @Override
            public List<JSGAxisAlignedBB> getGateVaporizingBoxes() {
                return Collections.singletonList(new JSGAxisAlignedBB(-0.5, 1, -0.5, 0.5, 2, 0.5));
            }

            @Override
            public JSGAxisAlignedBB getHorizonKillingBox() {
                return new JSGAxisAlignedBB(-0.5, 1, -0.5, 0.5, 2, 1.5);
            }
        };
    }


    public CompoundTag notebookPageTag = null;

    @Nullable
    public StargateAddress getAddressFromPageNBT() {
        if (notebookPageTag == null) return setUpRandomGate();
        if (!notebookPageTag.contains("address"))
            return setUpRandomGate();
        var address = notebookPageTag.getCompound("address");
        return new StargateAddress(address);
    }

    @Nullable
    private StargateAddress setUpRandomGate() {
        if (level == null) return null;
        var gate = StargateNetwork.INSTANCE.getRandomAddress(level.getRandom(), JSGSymbolTypes.MILKYWAY.get(), StargateTypes.MILKYWAY.get(), null);
        if (gate == null) return null;
        var tag = gate.second().serializeNBT();
        if (notebookPageTag == null) notebookPageTag = new CompoundTag();
        notebookPageTag.put("address", tag);
        return gate.second();
    }

    @Nullable
    public EnergyRequiredToOperate getEnergyRequiredToDial() {
        var addressToDial = getAddressFromPageNBT();
        if (addressToDial == null) return new EnergyRequiredToOperate(0, 0);
        var sgPos = StargateNetwork.INSTANCE.getStargate(addressToDial);
        if (sgPos == null) return null;
        return getEnergyManager().getEnergyRequiredToDial(sgPos, new StargateAddressDynamic(addressToDial));
    }

    @Override
    public void generateMergeHelper() {
        // setup merge helper - client and server
        mergeHelper = new StargateOrlinMergeHelper(this);
        mergeHelper.horizontalFacing = getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY);
        mergeHelper.verticalFacing = dev.tauri.jsg.core.common.blockstate.JSGProperties.getDirectionByVerticalFacing(getBlockState().getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY));
        mergeHelper.basePos = getBlockPos();
    }

    @Override
    public SymbolType<?> getSymbolType() {
        return JSGSymbolTypes.MILKYWAY.get();
    }

    @Override
    public StargateType<?> getStargateType() {
        return StargateTypes.ORLIN.get();
    }

    @Override
    public @Nullable PositionedSound getPositionedSound(StargateSoundPositionedEnum soundEnum) {
        return null;
    }

    @Override
    public @Nullable SoundEvent getSoundEvent(StargateSoundEventEnum soundEnum) {
        return switch (soundEnum) {
            case OPEN -> JSGSoundEvents.GATE_MILKYWAY_OPEN;
            case OPEN_NOX -> JSGSoundEvents.GATE_NOX_OPEN;
            case CLOSE -> JSGSoundEvents.GATE_MILKYWAY_CLOSE;
            case DIAL_FAILED -> JSGSoundEvents.GATE_ORLIN_FAIL;
            case GATE_BROKE -> JSGSoundEvents.GATE_ORLIN_BROKE;
            default -> null;
        };
    }

    @Override
    public BlockPos getGateCenterPos() {
        return getBlockPos().above();
    }

    @Override
    public List<Supplier<BiomeOverlayInstance>> getSupportedOverlays() {
        return List.of(CoreBiomeOverlays.NORMAL);
    }

    public void initializeFromItemStack(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag compound = stack.getOrCreateTag();

            if (compound.contains("openCount")) {
                openCount = compound.getInt("openCount");
            }
            if (compound.contains("notebook_page")) {
                notebookPageTag = compound.getCompound("notebook_page");
            }
        }
    }

    public void incrementOpenCount() {
        if (level == null || level.isClientSide()) return;
        openCount++;
        getMergeHelper().incrementMembersOpenCount();
        if (isBroken(true)) {
            level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()).setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ORLIN_BROKEN, true), 3);
        }
        setChanged();
    }

    @Override
    public StargateOrlinMergeHelper getMergeHelper() {
        return (StargateOrlinMergeHelper) super.getMergeHelper();
    }

    public boolean isBroken(boolean onlyBase) {
        var max = JSGConfig.Stargate.stargateOrlinMaxOpenCount.get();
        if (max <= 0) return false;

        if (openCount >= max)
            return true;
        if (onlyBase)
            return false;
        return getMergeHelper().getMaxOpenCount() >= max;
    }


    public void redstonePowerUpdate(boolean power) {
        if (!isMerged())
            return;

        if ((isPowered && !power) || (!isPowered && power)) {
            isPowered = power;
            if (isPowered && getDialingManager().getStargateState().idle() && !isBroken(false))
                getDialingManager().beginOpening();
            else if (!isPowered && getDialingManager().getStargateState().initiating()) {
                getDialingManager().attemptClose(StargateClosedReasonEnum.REQUESTED);
            }
            setChanged();
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putBoolean("isPowered", isPowered);
        compound.putInt("openCount", openCount);
        compound.putBoolean("canNotGenerate", canNotGenerate);
        if (notebookPageTag != null)
            compound.put("notebook_page", notebookPageTag);

        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        isPowered = compound.getBoolean("isPowered");
        openCount = compound.getInt("openCount");
        canNotGenerate = compound.getBoolean("canNotGenerate");
        if (compound.contains("notebook_page"))
            notebookPageTag = compound.getCompound("notebook_page");
        super.load(compound);
    }

    @Override
    public void regenerateStargate(IStargateGenerator.PlacementConfig pConfig) {
        if (level == null) return;
        if (level.isClientSide) return;
        JSG.logger.info("Regenerating Orlin's stargate at {} in {}", getBlockPos(), level.dimension().location());

        pConfig.world = this.level;
        pConfig.gateBasePos = this.getBlockPos();
        pConfig.gateFacing = mergeHelper.horizontalFacing;
        pConfig.gateVerticalFacing = mergeHelper.verticalFacing;
        pConfig.gateType = getStargateType();
        pConfig.overlay = getBiomeOverlayWithOverride();

        if (level.random.nextFloat() < 0.3f)
            pConfig.upgrades.add(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_TYPE);

        if (this instanceof ILinkable<?> linkable && linkable.isLinked()) {
            pConfig.dhdPos = linkable.getLinkedPos();
            if (level.random.nextFloat() < 0.3f)
                pConfig.upgrades.add(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_DHD);
            pConfig.dhdFluid = (int) (((JSGConfig.DialHomeDevice.fluidCapacity.get() - (JSGConfig.DialHomeDevice.fluidCapacity.get() * 0.1f)) * level.random.nextFloat()) + (JSGConfig.DialHomeDevice.fluidCapacity.get() * 0.1f));
        }

        var generator = new StargateGenerator();
        generator.setStargateEnergyInternalSmart(pConfig, (int) (JSGConfig.Stargate.stargateEnergyStorage.get() * level.random.nextFloat()));
        generator.generateStargate(pConfig, false);
        generateAddresses(true);
        setChanged();
    }
}
