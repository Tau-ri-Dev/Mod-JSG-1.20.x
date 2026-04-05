package dev.tauri.jsg.item.stargate;

import dev.tauri.jsg.core.common.item.JSGBlockItem;
import dev.tauri.jsg.item.tooltips.ServerStargateInventoryTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class StargateBaseItem extends JSGBlockItem {
    public StargateBaseItem(Block pBlock, Properties pProperties, @Nullable List<RegistryObject<CreativeModeTab>> tabs) {
        super(pBlock, pProperties, tabs);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return Optional.of(new ServerStargateInventoryTooltip(stack));
    }
}
