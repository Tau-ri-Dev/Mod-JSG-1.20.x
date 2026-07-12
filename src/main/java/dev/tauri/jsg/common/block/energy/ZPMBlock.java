package dev.tauri.jsg.common.block.energy;

import dev.tauri.jsg.common.blockentity.energy.ZPMBE;
import dev.tauri.jsg.common.item.energy.ZPMItemBlock;
import dev.tauri.jsg.common.registry.JSGTabs;
import dev.tauri.jsg.core.common.block.TickableBEBlock;
import dev.tauri.jsg.core.common.block.util.IHighlightBlock;
import dev.tauri.jsg.core.common.block.util.IItemBlock;
import dev.tauri.jsg.core.common.item.ITabbedItem;
import dev.tauri.jsg.core.common.item.JSGBlockItem;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import dev.tauri.jsg.core.common.util.ItemNBT;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import dev.tauri.jsg.core.common.registry.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public class ZPMBlock extends TickableBEBlock implements ITabbedItem, IItemBlock, IHighlightBlock {
    public ZPMBlock() {
        this(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_ORANGE)
                .sound(SoundType.GLASS)
                .strength(3.0f)
                .noOcclusion());
    }

    protected ZPMBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide) return;
        if (stack.getCapability(Capabilities.EnergyStorage.ITEM) instanceof JSGEnergyStorage itemStorage
                && level.getBlockEntity(pos) instanceof ZPMBE zpm) {
            zpm.getEnergyStorage().setEnergy(itemStorage.getTrueEnergyStored(), true);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, net.minecraft.world.entity.player.Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        if (level.getBlockEntity(pos) instanceof ZPMBE zpm) {
            long energy = zpm.getEnergyStorage().getTrueEnergyStored();
            ItemNBT.update(stack, tag -> tag.putLong("energy", energy));
        }
        return stack;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ZPMBE(pos, state);
    }

    @Override
    public @Nullable RegistryObject<CreativeModeTab> getTab() {
        return JSGTabs.TAB_MACHINES;
    }

    @Override
    public JSGBlockItem getItemBlock() {
        return new ZPMItemBlock(this, false);
    }

    @Override
    public boolean renderHighlight(BlockState blockState) {
        return false;
    }
}
