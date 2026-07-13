package dev.tauri.jsg.client.screen.inventory;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.container.ZPMSlotContainer;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ZPMSlotContainerGui extends ZPMHubContainerGui<ZPMSlotContainer> {
    public ZPMSlotContainerGui(ZPMSlotContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.imageHeight = 163;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected ResourceLocation getBackground() {
        return JSGMapping.rl(JSG.MOD_ID, "textures/gui/container_zpmslot.png");
    }

    @Override
    protected int getButtonY() {
        return 35;
    }

    @Override
    protected int getBarY() {
        return 59;
    }

    @Override
    protected int getPercentTextY() {
        return 69;
    }
}
