package dev.tauri.jsg.common.container;

import dev.tauri.jsg.common.blockentity.energy.ZPMHubBE;
import dev.tauri.jsg.common.registry.JSGMenuTypes;
import dev.tauri.jsg.core.client.screen.util.ContainerHelper;
import dev.tauri.jsg.core.common.forgeutil.SlotHandler;
import dev.tauri.jsg.core.common.menu.JSGContainer;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.StateUpdatePacketToClient;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.util.CreativeItemsChecker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

public class ZPMHubContainer extends JSGContainer {
    public final ZPMHubBE hubTile;
    public final boolean isOperator;
    public final Inventory playerInventory;

    private long lastEnergyStored = -1;
    private long lastEnergyTransferred;

    // Server
    public ZPMHubContainer(int containerID, Inventory playerInventory, BlockEntity blockEntity) {
        this(JSGMenuTypes.ZPM_HUB_MENU_TYPE.get(), containerID, playerInventory, blockEntity);
    }

    protected ZPMHubContainer(MenuType<?> menuType, int containerID, Inventory playerInventory, BlockEntity blockEntity) {
        super(menuType, containerID);
        this.playerInventory = playerInventory;
        this.isOperator = playerInventory.player.isCreative();
        this.hubTile = (ZPMHubBE) Objects.requireNonNull(blockEntity, "ZPM Hub tile is null inside the container!");

        for (Slot slot : createZPMSlots(hubTile.getItemHandler()))
            addSlot(slot);

        for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, getInventoryY()))
            addSlot(slot);
    }

    // Client
    public ZPMHubContainer(int containerID, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerID, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    protected List<Slot> createZPMSlots(IItemHandler itemHandler) {
        return List.of(
                new SlotHandler(itemHandler, 0, 80, 27),
                new SlotHandler(itemHandler, 1, 56, 51),
                new SlotHandler(itemHandler, 2, 104, 51));
    }

    public int getZPMSlotCount() {
        return hubTile.getContainerSize();
    }

    protected int getInventoryY() {
        return 97;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = getSlot(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();

        if (!CreativeItemsChecker.canInteractWith(stack, isOperator)) return ItemStack.EMPTY;

        ItemStack returnStack = stack.copy();
        int zpmSlots = getZPMSlotCount();

        if (index < zpmSlots) {
            // to player
            if (!moveItemStackTo(stack, zpmSlots, slots.size(), true))
                return ItemStack.EMPTY;
        } else {
            // from player
            if (!moveItemStackTo(stack, 0, zpmSlots, false))
                return ItemStack.EMPTY;
        }

        if (stack.isEmpty())
            slot.set(ItemStack.EMPTY);
        else
            slot.setChanged();

        return returnStack;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (slotId >= 0 && slotId < slots.size() && !CreativeItemsChecker.canInteractWith(getSlot(slotId).getItem(), isOperator))
            return;
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        long energyInternal = hubTile.getEnergyStorage().getEnergyStoredInternal();
        if (lastEnergyStored != energyInternal || lastEnergyTransferred != hubTile.getEnergyTransferredLastTick()) {
            if (playerInventory.player instanceof ServerPlayer sp)
                JSGCorePacketHandler.sendTo(new StateUpdatePacketToClient(hubTile.getBlockPos(), CoreStateTypes.GUI_UPDATE, hubTile.getState(CoreStateTypes.GUI_UPDATE.get())), sp);

            lastEnergyStored = energyInternal;
            lastEnergyTransferred = hubTile.getEnergyTransferredLastTick();
        }
    }
}
