package dev.tauri.jsg.common.container;

import dev.tauri.jsg.api.stargate.dialhomedevice.DHDReactorStateEnum;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.common.registry.JSGMenuTypes;
import dev.tauri.jsg.common.state.dialhomedevice.DHDContainerGuiUpdate;
import dev.tauri.jsg.core.client.screen.tab.OpenTabHolderInterface;
import dev.tauri.jsg.core.client.screen.util.ContainerHelper;
import dev.tauri.jsg.core.common.forgeutil.SlotHandler;
import dev.tauri.jsg.core.common.menu.JSGContainer;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.StateUpdatePacketToClient;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.util.FluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DHDContainer extends JSGContainer implements OpenTabHolderInterface {

    public Slot slotCrystal;
    public FluidTank tankNaquadah;
    public DHDAbstractBE dhdTile;

    protected BlockPos pos;
    protected int tankLastAmount;
    protected DHDReactorStateEnum lastReactorState;
    protected boolean lastLinked;
    protected final List<Integer> openedTabsSlotsIds = new ArrayList<>();

    public final Inventory playerInventory;

    @Override
    public List<Integer> getOpenTabsSlotsIds() {
        return openedTabsSlotsIds;
    }

    @Override
    public void modifyOpenTabSlotId(int slotId, boolean add) {
        if (add) openedTabsSlotsIds.add(slotId);
        else openedTabsSlotsIds.removeIf(v -> v == slotId);
    }

    public DHDContainer(int containerID, Inventory playerInventory, BlockEntity tile) {
        super(JSGMenuTypes.DHD_MENU_TYPE.get(), containerID);
        this.playerInventory = playerInventory;
        if (tile == null) {
            throw new NullPointerException("Gate tile is null inside the container! Can not continue!");
        }
        dhdTile = (DHDAbstractBE) tile;
        pos = dhdTile.getBlockPos();
        IItemHandler itemHandler = dhdTile.getCapability(ForgeCapabilities.ITEM_HANDLER, null).resolve().orElseThrow();

        // Crystal slot (index 0)
        slotCrystal = new SlotHandler(itemHandler, 0, 81, 40);
        addSlot(slotCrystal);

        tankNaquadah = (FluidTank) dhdTile.getCapability(ForgeCapabilities.FLUID_HANDLER, null).resolve().orElseThrow();

        // Upgrades (index 1-3)
        for (int col = 0; col < 3; col++) {
            addSlot(new SlotHandler(itemHandler, col + 1, 9 + 18 * col, 40));
        }

        // Bucket (index 4)
        addSlot(new SlotHandler(itemHandler, 4, 116, 23));

        // Biome overlay slot (index 5)
        addSlot(new SlotHandler(itemHandler, 5, 0, 0));

        for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, 91))
            addSlot(slot);
    }

    // Client
    public DHDContainer(int containerID, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerID, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = getSlot(index).getItem();

        // Transfering from DHD to player's inventory
        if (index < 6) {
            if (!moveItemStackTo(stack, 6, slots.size(), false)) {
                return ItemStack.EMPTY;
            }

            getSlot(index).set(ItemStack.EMPTY);
            setRemoteSlot(index, ItemStack.EMPTY);
        }

        // Transfering from player's inventory to DHD
        else {
            if (stack.getItem() == getControlCrystal()) {
                if (!slotCrystal.hasItem()) {
                    ItemStack stack1 = stack.copy();
                    stack1.setCount(1);
                    slotCrystal.set(stack1);

                    stack.shrink(1);

                    return ItemStack.EMPTY;
                }
            } else if (DHDAbstractBE.SUPPORTED_UPGRADES.contains(stack.getItem()) && !dhdTile.hasUpgrade(stack.getItem())) {
                for (int i = 1; i < 4; i++) {
                    if (!getSlot(i).hasItem()) {
                        ItemStack stack1 = stack.copy();
                        stack1.setCount(1);

                        getSlot(i).set(stack1);
                        setRemoteSlot(i, stack1);
                        stack.shrink(1);

                        return stack;
                    }
                }
            } else if (stack.getItem() instanceof BucketItem) {
                Fluid fluid = ((BucketItem) stack.getItem()).getFluid();
                if (fluid == CoreFluids.MOLTEN_NAQUADAH_REFINED.get()) {
                    if (!getSlot(4).hasItem()) {
                        ItemStack stack1 = stack.copy();
                        stack1.setCount(1);

                        getSlot(4).set(stack1);
                        setRemoteSlot(4, stack1);
                        stack.shrink(1);

                        return stack;
                    }
                }
            }

            // Biome override blocks
            else if (openedTabsSlotsIds.contains(5) && getSlot(5).mayPlace(stack)) {
                if (!getSlot(5).hasItem()) {
                    ItemStack stack1 = stack.copy();
                    stack1.setCount(1);

                    getSlot(5).set(stack1);
                    setRemoteSlot(5, stack1);
                    stack.shrink(1);

                    return ItemStack.EMPTY;
                }
            }

            return ItemStack.EMPTY;
        }

        return stack;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (tankLastAmount != tankNaquadah.getFluidAmount() || lastReactorState != dhdTile.getReactorState() || lastLinked != dhdTile.isLinked()) {
            if (playerInventory.player instanceof ServerPlayer sp)
                JSGCorePacketHandler.sendTo(new StateUpdatePacketToClient(pos, CoreStateTypes.GUI_UPDATE, new DHDContainerGuiUpdate(tankNaquadah.getFluidAmount(), tankNaquadah.getCapacity(), dhdTile.getReactorState(), dhdTile.isLinked())), sp);

            tankLastAmount = tankNaquadah.getFluidAmount();
            lastReactorState = dhdTile.getReactorState();
            lastLinked = dhdTile.isLinked();
        }
    }

    public Item getControlCrystal() {
        return dhdTile.getControlCrystal();
    }
}
