package dev.tauri.jsg.api.stargate;

import dev.tauri.jsg.api.stargate.manager.IStargateIrisManager;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;

public interface StargateWithIris<E extends JSGEnergyStorage> extends Stargate<E> {
    BlockPos[] IRIS_PATTER = Util.make(new ArrayList<BlockPos>(), (list) -> {
        for (int i = 0; i < 6; i++) {
            for (int j = -2; j < 3; j++) {
                if ((i == 0 || i == 5) && (j == -2 || j == 2)) continue;
                list.add(new BlockPos(j, i + 1, 0));
            }
        }
    }).toArray(new BlockPos[0]);

    IStargateIrisManager getIrisManager();
}
