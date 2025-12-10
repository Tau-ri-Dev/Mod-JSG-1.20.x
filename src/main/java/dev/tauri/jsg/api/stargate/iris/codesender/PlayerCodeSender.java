package dev.tauri.jsg.api.stargate.iris.codesender;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

/**
 * @author matousss
 */
public class PlayerCodeSender extends CodeSender {

    public UUID senderUUID;
    private Level world;

    /**
     * @param args Require :World: on first position
     */
    @Override
    public void prepareToLoad(Object[] args) {
        world = (Level) args[0];
    }

    public PlayerCodeSender(@NonNull Player player) {
        this.senderUUID = player.getUUID();
        world = player.level();
    }

    public PlayerCodeSender() {

    }

    @Override
    public void sendMessage(Component message) {
        Player player = getPlayer();
        if (player instanceof ServerPlayer sp) {
            sp.sendSystemMessage(message, true);
            return;
        }
        if (player != null) {
            player.sendSystemMessage(message);
        }
    }

    @Override
    public Level getWorld() {
        return world;
    }

    @Override
    public boolean canReceiveMessage() {
        Player player = getPlayer();
        if (player == null) return false;
        ItemStack gdo = player.getMainHandItem();
        /*if (gdo.isEmpty() || gdo.getItem() != ItemRegistry.GDO.get()) {
            gdo = player.getOffhandItem();
            if (gdo.isEmpty() || gdo.getItem() != ItemRegistry.GDO.get() || !gdo.hasTag()) return false;
        }*/
        return (gdo.getTag() != null && gdo.getTag().contains("linkedGate"));
    }

    @Override
    public CodeSenderType getType() {
        return CodeSenderType.PLAYER;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putUUID("sender_uuid", senderUUID);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        senderUUID = nbt.getUUID("sender_uuid");

    }

    private Player getPlayer() {
        if (senderUUID == null) return null;
        return world.getPlayerByUUID(senderUUID);
    }
}
