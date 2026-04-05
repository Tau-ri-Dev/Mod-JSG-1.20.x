package dev.tauri.jsg.item.tooltips;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record ServerStargateInventoryTooltip(ItemStack stack) implements TooltipComponent {
}
