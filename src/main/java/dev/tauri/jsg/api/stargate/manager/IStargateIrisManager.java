package dev.tauri.jsg.api.stargate.manager;

import dev.tauri.jsg.api.stargate.iris.EnumIrisMode;
import dev.tauri.jsg.api.stargate.iris.EnumIrisState;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.api.stargate.iris.codesender.CodeSender;
import dev.tauri.jsg.core.common.blockentity.ITickable;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface IStargateIrisManager extends INBTSerializable<CompoundTag>, ITickable {
    @Nullable
    CodeSender getCodeSender();

    EnumIrisType getIrisType();

    EnumIrisMode getIrisMode();

    String getIrisCode();

    EnumIrisState getIrisState();

    default boolean isIrisOpened() {
        return getIrisState() == EnumIrisState.OPENED || getIrisType() == EnumIrisType.NULL;
    }

    default boolean isIrisClosed() {
        return getIrisState() == EnumIrisState.CLOSED && getIrisType() != EnumIrisType.NULL;
    }

    default boolean hasIris() {
        return getIrisType() != EnumIrisType.NULL;
    }

    default boolean hasShield() {
        return getIrisType() == EnumIrisType.SHIELD;
    }

    default boolean hasPhysicalIris() {
        return getIrisType() == EnumIrisType.IRIS_TRINIUM || getIrisType() == EnumIrisType.IRIS_TITANIUM || getIrisType() == EnumIrisType.IRIS_CREATIVE;
    }

    default boolean hasCreativeIris() {
        return getIrisType() == EnumIrisType.IRIS_CREATIVE;
    }

    ItemStack getIrisItem();

    boolean canInsertItemAsIris(@Nullable Item item);

    boolean receiveIrisCode(CodeSender sender, String code);

    void setIrisCode(@Nullable String code);

    void setIrisMode(EnumIrisMode irisMode);

    boolean toggleIris();

    void hitIris();
}
