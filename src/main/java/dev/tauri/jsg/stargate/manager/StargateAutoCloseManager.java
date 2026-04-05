package dev.tauri.jsg.stargate.manager;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.manager.IStargateAutoCloseManager;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class StargateAutoCloseManager extends AbstractStargateManager<Stargate<?>> implements IStargateAutoCloseManager {
    private int secondsPassed;
    private int playersPassed;

    public StargateAutoCloseManager(Stargate<?> gateTile) {
        super(gateTile);
    }

    public void wormholeDisconnected() {
        secondsPassed = 0;
        playersPassed = 0;
    }

    public void playerPassing() {
        playersPassed++;
    }

    @Override
    public boolean shouldClose(StargatePos sourceStargatePos) {
        if (stargate.getTime() % 20 == 0) {
            Level sourceWorld = sourceStargatePos.getWorld();
            BlockPos sourcePos = sourceStargatePos.gatePos;

            boolean sourceLoaded = sourceWorld.isLoaded(sourcePos);

            if (playersPassed > 0) {
                if (sourceLoaded) {
                    // create scan box
                    AABB scanBox = new AABB(sourcePos.offset(new Vec3i(-10, -5, -10)), sourcePos.offset(new Vec3i(10, 5, 10)));
                    // scan players in box
                    int playerCount = sourceWorld.getEntities((Entity) null, scanBox, (e) -> {
                        if (e instanceof Player p)
                            return !p.isDeadOrDying();
                        return false;
                    }).size();
                    // check if player is in the box
                    if (playerCount == 0)
                        // if no, add 1 to secondsPassed
                        secondsPassed++;
                    else
                        // if yes, reset secondsPassed
                        secondsPassed = 0;
                } else {
                    secondsPassed++;
                }
            }

            return secondsPassed >= (JSGConfig.Stargate.secondsToAutoclose.get() + 3);
        }

        return false;
    }


    // ------------------------------------------------------------------------
    // NBT

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();

        compound.putInt("secondsPassed", secondsPassed);
        compound.putInt("playersPassed", playersPassed);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        if (compound == null)
            return;

        secondsPassed = compound.getInt("secondsPassed");
        playersPassed = compound.getInt("playersPassed");
    }
}
