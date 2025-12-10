package dev.tauri.jsg.api.client.screen.util;


import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class ContainerHelper {

    public static List<Slot> generatePlayerSlots(Container playerInventory, int yInventory) {
        List<Slot> slots = new ArrayList<>(39);

        slots.addAll(generateSlotRow(playerInventory, 0, 8, 18, yInventory + 58)); // 144

        for (int row = 0; row < 3; row++) {
            slots.addAll(generateSlotRow(playerInventory, 9 * (row + 1), 8, 18, yInventory + 18 * row)); // 86
        }

        return slots;
    }

    public static List<Slot> generateSlotRow(Container playerInventory, int firstIndex, int xStart, int xOffset, int y) {
        List<Slot> slots = new ArrayList<>(9);

        for (int col = 0; col < 9; col++) {
            slots.add(new Slot(playerInventory, firstIndex + col, 18 * col + 8, y));
        }

        return slots;
    }
}
