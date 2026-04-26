package dev.tauri.jsg.client.screen.inventory.stargate;

import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.StargateUpgrade;
import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.common.registry.JSGMenuTypes;
import dev.tauri.jsg.core.client.screen.tab.OpenTabHolderInterface;
import dev.tauri.jsg.core.client.screen.util.ContainerHelper;
import dev.tauri.jsg.core.common.forgeutil.SlotHandler;
import dev.tauri.jsg.core.common.item.IUpgradeItem;
import dev.tauri.jsg.core.common.item.capacitor.CapacitorItemBlock;
import dev.tauri.jsg.core.common.menu.JSGContainer;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.StateUpdatePacketToClient;
import dev.tauri.jsg.core.common.power.general.LargeEnergyStorage;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.util.CreativeItemsChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StargateContainer extends JSGContainer implements OpenTabHolderInterface {

    public StargateClassicBaseBE<?> gateTile;
    public boolean hasCreative;
    private final BlockPos pos;
    private int lastEnergyStored;
    private int energyTransferredLastTick;
    private double lastEnergySecondsToClose;
    private int lastProgress;
    protected final List<Integer> openedTabsSlotsIds = new ArrayList<>();
    private EnumIrisMode irisMode;
    private String irisCode = "";
    public long openedSince;
    private double gateTemp;
    private double irisTemp;

    public final Inventory playerInventory;

    // Server
    public StargateContainer(int containerID, Inventory playerInventory, BlockEntity baseTile) {
        super(JSGMenuTypes.STARGATE_MENU_TYPE.get(), containerID);
        this.playerInventory = playerInventory;
        this.hasCreative = playerInventory.player.isCreative();
        if (baseTile == null) {
            throw new NullPointerException("Gate tile is null inside the container! Can not continue!");
        }
        this.gateTile = (StargateClassicBaseBE<?>) baseTile;
        this.pos = gateTile.getBlockPos();
        IItemHandler itemHandler = gateTile.getItemHandler();

        for (int col = 0; col < 4; col++) {
            addSlot(new SlotHandler(itemHandler, col, 9 + 18 * col, 27));
        }

        // Capacitors 1x3 (index 4-6)
        for (int col = 0; col < 3; col++) {
            final int capacitorIndex = col;
            addSlot(new SlotHandler(itemHandler, col + 4, 115 + 18 * col, 27) {
                @Override
                public boolean isActive() {
                    // hasItem() is a compatibility thing for when players already had their capacitors in the gate.
                    return (capacitorIndex + 1 <= gateTile.getSupportedCapacitors()) || hasItem();
                }
            });
        }

        // Biome overlay slot (index 7)
        addSlot(new SlotHandler(itemHandler, 7, 0, 0));

        // Shield/Iris Upgrade (index 8)
        addSlot(new SlotHandler(itemHandler, 8, 81, 27));

        // Page slots (index 7-9)
        for (int i = 0; i < SymbolType.values(JSGSymbolUsages.STARGATES.get()).size(); i++) {
            addSlot(new SlotHandler(itemHandler, i + 9, -22, 89 + 22 * i));
        }

        for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, 91))
            addSlot(slot);
    }

    // Client
    public StargateContainer(int containerID, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerID, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public List<Integer> getOpenTabsSlotsIds() {
        return openedTabsSlotsIds;
    }

    @Override
    public void modifyOpenTabSlotId(int slotId, boolean add) {
        if (add) openedTabsSlotsIds.add(slotId);
        else openedTabsSlotsIds.removeIf(v -> v == slotId);
    }

    @Override
    public void setData(int id, int data) {
        gateTile.setPageProgress(data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = getSlot(index).getItem();

        if (!CreativeItemsChecker.canInteractWith(stack, hasCreative)) return ItemStack.EMPTY;

        // Transfering from Stargate to player's inventory
        if (index < 12) {
            if (!moveItemStackTo(stack, 12, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
            getSlot(index).set(ItemStack.EMPTY);
            setRemoteSlot(index, ItemStack.EMPTY);
        }

        // Transfering from player's inventory to Stargate
        else {
            var openedSlots = getOpenTabsSlotsIds();
            var biomeSlotId = 7;
            var addressSlots = openedSlots.stream().filter(slot -> (
                    slot >= 9 && slot <= (8 + SymbolType.values(JSGSymbolUsages.STARGATES.get()).size())
                            && gateTile.getItemHandler().isItemValid(slot, stack) && !getSlot(slot).hasItem()
            )).toList();

            // Capacitors
            if (stack.getItem() instanceof CapacitorItemBlock) {
                for (int i = 4; i < 7; i++) {
                    if (!getSlot(i).hasItem() && getSlot(i).mayPlace(stack)) {
                        ItemStack stack1 = stack.copy();
                        stack1.setCount(1);

                        setRemoteSlot(i, stack1);
                        getSlot(i).set(stack1);
                        stack.shrink(1);

                        return stack;
                    }
                }
            } else if (stack.getItem() instanceof IUpgradeItem upgradeItem && upgradeItem.getUpgrade() instanceof StargateUpgrade stargateUpgrade && !gateTile.hasUpgrade(stargateUpgrade)) {
                for (int i = 0; i < 4; i++) {
                    if (!getSlot(i).hasItem()) {
                        ItemStack stack1 = stack.copy();
                        stack1.setCount(1);

                        setRemoteSlot(i, stack1);
                        getSlot(i).set(stack1);
                        stack.shrink(1);

                        return ItemStack.EMPTY;
                    }
                }
            } else if (!addressSlots.isEmpty()) {
                var s = addressSlots.get(0);
                ItemStack stack1 = stack.copy();
                stack1.setCount(1);

                setRemoteSlot(s, stack1);
                getSlot(s).set(stack1);
                stack.shrink(1);

                return ItemStack.EMPTY;
            }
            // Biome override blocks
            else if (openedSlots.contains(biomeSlotId) && gateTile.getItemHandler().isItemValid(biomeSlotId, stack)) {
                if (!getSlot(biomeSlotId).hasItem()) {
                    ItemStack stack1 = stack.copy();
                    stack1.setCount(1);

                    setRemoteSlot(biomeSlotId, stack1);
                    getSlot(biomeSlotId).set(stack1);
                    stack.shrink(1);

                    return ItemStack.EMPTY;
                }
            }
            // Iris upgrade
            else if (gateTile.getIrisManager().canInsertItemAsIris(stack.getItem()) && !gateTile.hasUpgrade(stack.getItem())) {
                var irisSlot = 8;
                if (!getSlot(irisSlot).hasItem()) {
                    ItemStack stack1 = stack.copy();
                    stack1.setCount(1);

                    setRemoteSlot(irisSlot, stack1);
                    getSlot(irisSlot).set(stack1);
                    stack.shrink(1);

                    return ItemStack.EMPTY;
                }
            }

            return ItemStack.EMPTY;
        }

        return stack;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        try {
            if (slotId >= 0 && slotId < slots.size() && !CreativeItemsChecker.canInteractWith(getSlot(slotId).getItem(), hasCreative))
                return;
        } catch (Exception ignored) {
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        LargeEnergyStorage energyStorage = (LargeEnergyStorage) gateTile.getStargateCapability(ForgeCapabilities.ENERGY, null).resolve().orElseThrow();

        if (lastEnergyStored != Objects.requireNonNull(energyStorage).getEnergyStoredInternally()
                || lastEnergySecondsToClose != gateTile.getEnergyManager().getSecondsToClose()
                || energyTransferredLastTick != gateTile.getEnergyManager().getTransferredLastTick()
                || irisMode != gateTile.getIrisManager().getIrisMode()
                || !Objects.equals(irisCode, gateTile.getIrisManager().getIrisCode())
                || openedSince != gateTile.getDialingManager().getConnection().getSince()
                || (Math.abs(gateTemp - gateTile.gateHeat) > 5) || (gateTemp == -1 && gateTile.gateHeat != -1)
                || (Math.abs(irisTemp - gateTile.irisHeat) > 5) || (irisTemp == -1 && gateTile.irisHeat != -1)
                || lastProgress != gateTile.getPageProgress()

        ) {
            if (playerInventory.player instanceof ServerPlayer sp)
                JSGCorePacketHandler.sendTo(new StateUpdatePacketToClient(pos, CoreStateTypes.GUI_UPDATE, gateTile.getState(CoreStateTypes.GUI_UPDATE.get())), sp);

            lastEnergyStored = energyStorage.getEnergyStoredInternally();
            energyTransferredLastTick = gateTile.getEnergyManager().getTransferredLastTick();
            lastEnergySecondsToClose = gateTile.getEnergyManager().getSecondsToClose();
            openedSince = gateTile.getDialingManager().getConnection().getSince();
            irisMode = gateTile.getIrisManager().getIrisMode();
            irisCode = gateTile.getIrisManager().getIrisCode();
            gateTemp = gateTile.gateHeat;
            irisTemp = gateTile.irisHeat;
            lastProgress = gateTile.getPageProgress();
        }
    }

    @Override
    public void addSlotListener(@Nonnull ContainerListener listener) {
        super.addSlotListener(listener);

        if (listener instanceof ServerPlayer)
            JSGCorePacketHandler.sendTo(new StateUpdatePacketToClient(pos, CoreStateTypes.GUI_STATE, gateTile.getState(CoreStateTypes.GUI_STATE.get())), (ServerPlayer) listener);
    }
}
