package dev.tauri.jsg.api.stargate.iris.codesender;

import dev.tauri.jsg.api.stargate.network.StargatePos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

/**
 * @author matousss
 */
public class ComputerCodeSender extends CodeSender {
    StargatePos originGate;

    public ComputerCodeSender(StargatePos originGate) {
        this.originGate = originGate;
    }

    public ComputerCodeSender() {
    }

    @Override
    public void sendMessage(Component message) {
    }

    @Override
    public Level getWorld() {
        return null;
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = super.serializeNBT();
        compound.put("originGate", originGate.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        originGate = new StargatePos(nbt.getCompound("originGate"));
    }

    @Override
    public CodeSenderType getType() {
        return CodeSenderType.COMPUTER;
    }
}
