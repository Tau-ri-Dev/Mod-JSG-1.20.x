package dev.tauri.jsg.worldgen.generator;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.util.GeneratedStargate;
import dev.tauri.jsg.api.util.IStargateGenerator;
import dev.tauri.jsg.block.dialhomedevice.DHDAbstractBlock;
import dev.tauri.jsg.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.core.common.blockentity.CamouflageBE;
import dev.tauri.jsg.core.common.blockentity.IUpgradable;
import dev.tauri.jsg.core.common.config.ingame.IConfigurable;
import dev.tauri.jsg.core.common.helper.BlockHelper;
import dev.tauri.jsg.core.common.helper.BlockPosHelper;
import dev.tauri.jsg.core.common.helper.ItemHandlerHelper;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.helpers.StargateLinkingHelper;
import dev.tauri.jsg.registry.JSGItems;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class StargateGenerator implements IStargateGenerator {
    public void setStargateEnergyInternalSmart(IStargateGenerator.PlacementConfig config, int energy) {
        var stargateCapacity = (int) (JSGConfig.Stargate.stargateEnergyStorage.get() / 4f);
        config.stargateEnergyInternal = Math.min(stargateCapacity, energy);
        var toCaps = (energy - config.stargateEnergyInternal);
        while (toCaps > 0) {
            var capacity = Math.min(stargateCapacity, toCaps);
            config.capacitors.add(Pair.of(capacity, false));
            toCaps -= stargateCapacity;
        }
    }


    @Nullable
    public GeneratedStargate generateStargate(@Nonnull IStargateGenerator.PlacementConfig conf) {
        return generateStargate(conf, true);
    }

    /**
     * Generates merged gate with base block at specified position in config
     *
     * @param conf - your placement configuration
     * @return generated gate
     */
    @Nullable
    public GeneratedStargate generateStargate(@Nonnull IStargateGenerator.PlacementConfig conf, boolean replaceMemberBlocks) {
        if (conf.world == null) return null;
        if (conf.gateBasePos == null) return null;

        Block dhdBlock = conf.gateType.getDHDBlock();
        BlockState gateBaseBlockState = conf.gateType.getBaseBlock().defaultBlockState();
        Item crystalGlyphUpgrade = conf.gateType.symbolType.get().getGlyphUpgrade();

        // Place base block
        if (!conf.baseInPlace)
            conf.world.setBlock(conf.gateBasePos, gateBaseBlockState.setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY, conf.gateFacing).setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY, dev.tauri.jsg.core.common.blockstate.JSGProperties.getVerticalFacingByDirection(conf.gateVerticalFacing)), 16 | 2);

        StargateAbstractBaseBE<?, ?> gateTile;
        try {
            gateTile = (StargateAbstractBaseBE<?, ?>) conf.world.getBlockEntity(conf.gateBasePos);
        } catch (Exception e) {
            JSG.logger.error("Error while generating gate at " + BlockHelper.blockPosToBetterString(conf.gateBasePos) + " in " + conf.world.dimension(), e);
            return null;
        }
        if (gateTile == null) return null;

        var mergeHelper = gateTile.getMergeHelper();

        // Place member blocks
        for (Map.Entry<BlockPos, BlockState> e : mergeHelper.getBlocks(true).entrySet()) {
            var currentState = conf.world.getBlockState(e.getKey());
            var isMemberBlock = (currentState.getBlock() == e.getValue().getBlock());
            if (replaceMemberBlocks || !isMemberBlock) {
                conf.world.setBlock(e.getKey(), e.getValue(), 16 | 2);
                if (!isMemberBlock && conf.world.getBlockEntity(e.getKey()) instanceof CamouflageBE camoBE) {
                    camoBE.setCamoBlock(currentState);
                }
            }
        }
        mergeHelper.updateMemberStateAndCheck(true);
        gateTile.refresh();

        if (conf.stargateConfig != null && gateTile instanceof IConfigurable configTile) {
            configTile.setConfig(conf.stargateConfig.apply(configTile.getConfig()));
        }

        gateTile.getEnergyManager().getStorage().setEnergyStored(conf.stargateEnergyInternal);

        if (gateTile instanceof IUpgradable upgradable) {
            int nextSlot = 0;
            boolean isTypeCrystalIn = false;
            ItemHandlerHelper.clearInventory(upgradable.getItemHandler());
            if (conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_STARGATE))
                upgradable.getItemHandler().insertItem(nextSlot++, new ItemStack(JSGItems.CRYSTAL_GLYPH_STARGATE.get(), 1), false);
            if (conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_MW)) {
                upgradable.getItemHandler().insertItem(nextSlot++, new ItemStack(JSGItems.CRYSTAL_GLYPH_MILKYWAY.get(), 1), false);
                if (conf.gateType.symbolType.get().getGlyphUpgrade() == JSGItems.CRYSTAL_GLYPH_MILKYWAY.get())
                    isTypeCrystalIn = true;
            }
            if (conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_PEG)) {
                upgradable.getItemHandler().insertItem(nextSlot++, new ItemStack(JSGItems.CRYSTAL_GLYPH_PEGASUS.get(), 1), false);
                if (conf.gateType.symbolType.get().getGlyphUpgrade() == JSGItems.CRYSTAL_GLYPH_PEGASUS.get())
                    isTypeCrystalIn = true;
            }
            if (conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_UNI)) {
                upgradable.getItemHandler().insertItem(nextSlot++, new ItemStack(JSGItems.CRYSTAL_GLYPH_UNIVERSE.get(), 1), false);
                if (conf.gateType.symbolType.get().getGlyphUpgrade() == JSGItems.CRYSTAL_GLYPH_UNIVERSE.get())
                    isTypeCrystalIn = true;
            }

            if (!isTypeCrystalIn && nextSlot < 4 && conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_TYPE)) {
                upgradable.getItemHandler().insertItem(nextSlot, new ItemStack(crystalGlyphUpgrade, 1), false);
            }

            if (conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.UPGRADE_IRIS_TITANIUM))
                upgradable.getItemHandler().insertItem(8, new ItemStack(JSGItems.UPGRADE_IRIS.get(), 1), false);
            if (conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.UPGRADE_IRIS_TRINIUM))
                upgradable.getItemHandler().insertItem(8, new ItemStack(JSGItems.UPGRADE_IRIS_TRINIUM.get(), 1), false);
            if (conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.UPGRADE_SHIELD))
                upgradable.getItemHandler().insertItem(8, new ItemStack(JSGItems.UPGRADE_SHIELD.get(), 1), false);
            if (conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.UPGRADE_IRIS_CREATIVE))
                upgradable.getItemHandler().insertItem(8, new ItemStack(JSGItems.UPGRADE_IRIS_CREATIVE.get(), 1), false);

            nextSlot = 4;
            for (Pair<Integer, Boolean> e : conf.capacitors) {
                if (nextSlot >= 7) break;
                ItemStack capacitor;
                if (e.second()) {
                    capacitor = new ItemStack(CoreBlocks.CAPACITOR_BLOCK_CREATIVE.get());
                } else {
                    if (e.first() < 0) continue;
                    capacitor = new ItemStack(CoreBlocks.CAPACITOR_BLOCK.get());
                    IEnergyStorage storage = capacitor.getCapability(ForgeCapabilities.ENERGY, null).resolve().orElseThrow();
                    storage.receiveEnergy(e.first(), false);
                }
                upgradable.getItemHandler().insertItem(nextSlot++, capacitor, false);
            }
        }

        if (gateTile instanceof StargateClassicBaseBE<?> classicTile) {
            classicTile.currentPowerTier = 0;
            classicTile.updatePowerTier();
            classicTile.getIrisManager().setIrisMode(conf.irisMode);
            classicTile.getIrisManager().setIrisCode(conf.irisCode);
        }


        // DHD
        if (conf.dhdPos != null && dhdBlock != null) {
            var dhdState = conf.world.getBlockState(conf.dhdPos);
            if (!(dhdState.getBlock() instanceof DHDAbstractBlock)) {
                int dhdRotation = conf.dhdRotation;
                if (dhdRotation < 0) {
                    dhdRotation = BlockPosHelper.getIntDHDRotationFromFacing(conf.gateFacing, (conf.gateFacing == Direction.WEST || conf.gateFacing == Direction.EAST));
                }
                BlockState dhdBlockState = dhdBlock.defaultBlockState().setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.ROTATION_PROPERTY, dhdRotation);
                conf.world.setBlock(conf.dhdPos, dhdBlockState, 16 | 2);
            }
            DHDAbstractBE dhdTile = ((DHDAbstractBE) Objects.requireNonNull(conf.world.getBlockEntity(conf.dhdPos)));

            ItemStack crystal = new ItemStack(dhdTile.getControlCrystal());
            dhdTile.getItemStackHandler().setStackInSlot(0, crystal);

            if (conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_DHD))
                dhdTile.getItemStackHandler().setStackInSlot(1, new ItemStack(JSGItems.CRYSTAL_GLYPH_DHD.get(), 1));
            if (conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.UPGRADE_CAPACITY_DHD))
                dhdTile.getItemStackHandler().setStackInSlot(2, new ItemStack(CoreItems.CRYSTAL_UPGRADE_CAPACITY.get(), 1));
            if (conf.upgrades.contains(IStargateGenerator.StargateUpgradesEnum.UPGRADE_EFFICIENCY_DHD))
                dhdTile.getItemStackHandler().setStackInSlot(3, new ItemStack(CoreItems.CRYSTAL_UPGRADE_EFFICIENCY.get(), 1));

            if (conf.dhdFluid >= 0) {
                dhdTile.getFluidHandler().setFluid(new FluidStack(CoreFluids.MOLTEN_NAQUADAH_REFINED.get(), conf.dhdFluid));
            }

            StargateLinkingHelper.updateLinkedGate(conf.world, conf.gateBasePos, conf.dhdPos);
        }


        ResourceLocation biomePath = conf.world.getBiome(conf.gateBasePos).unwrapKey().orElse(Biomes.FOREST).location();
        StargateAddress gateAddress = gateTile.getStargateAddress(conf.addressSymbolTypeToReturn);
        return new GeneratedStargate(gateAddress, biomePath.getPath(), true, gateTile.getPointOfOrigin());
    }
}
