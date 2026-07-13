package dev.tauri.jsg.common.container;

import dev.tauri.jsg.common.registry.JSGMenuTypes;
import dev.tauri.jsg.core.common.forgeutil.SlotHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public class ZPMSlotContainer extends ZPMHubContainer {
    // Server
    public ZPMSlotContainer(int containerID, Inventory playerInventory, BlockEntity blockEntity) {
        super(JSGMenuTypes.ZPM_SLOT_MENU_TYPE.get(), containerID, playerInventory, blockEntity);
    }

    // Client
    public ZPMSlotContainer(int containerID, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerID, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    protected List<Slot> createZPMSlots(IItemHandler itemHandler) {
        return List.of(new SlotHandler(itemHandler, 0, 80, 35));
    }

    @Override
    protected int getInventoryY() {
        return 81;
    }
}
