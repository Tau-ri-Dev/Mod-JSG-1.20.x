package dev.tauri.jsg.api.stargate.iris.codesender;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;


/**
 * @author matousss
 */
public abstract class CodeSender implements INBTSerializable<CompoundTag> {
    public abstract void sendMessage(Component message);

    public abstract Level getWorld();

    public boolean canReceiveMessage() {
        return true;
    }

    public abstract CodeSenderType getType();

    /**
     * @param args any arguments needed before deserialization
     *
     */
    public void prepareToLoad(Object[] args) {
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("type", getType().id);
        return nbt;
    }

    public static CodeSender fromNBT(CompoundTag compound, Level world) {
        var codeSender = CodeSenderType.fromId(compound.getInt("type")).constructor.get();
        switch (codeSender.getType()) {
            case PLAYER:
                codeSender.prepareToLoad(new Object[]{world});
                break;
            case COMPUTER:
                codeSender.prepareToLoad(null);
                break;

        }
        codeSender.deserializeNBT(compound);
        return codeSender;
    }
}
