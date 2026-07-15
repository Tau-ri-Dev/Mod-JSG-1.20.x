package dev.tauri.jsg.api.dialhomedevice;

import dev.tauri.jsg.common.helpers.CurrentRegistries;
import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.api.item.IDHDFluidTank;
import dev.tauri.jsg.api.item.IDHDPartItem;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.common.dialhomedevice.manager.DHDReactorManager;
import dev.tauri.jsg.common.dialhomedevice.manager.state.DHDAbstractStateManager;
import dev.tauri.jsg.core.common.blockentity.*;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public interface StargateDHD extends ILinkableBE<Stargate<?>>, ITickable, IPreparable, IUpgradable, PointOfOriginProvider {
    private BlockEntity self() {
        return (BlockEntity) this;
    }

    DHDAbstractStateManager<?, ?> getStateManager();

    DHDReactorManager getReactorManager();

    @Override
    default void onLoad(@NotNull Level level) {
        getStateManager().onLoad(level);
        getReactorManager().onLoad(level);
    }

    @Override
    default void tick(@NotNull Level level) {
        getStateManager().tick(level);
        getReactorManager().tick(level);
    }

    default long getTime() {
        var level = self().getLevel();
        if (level == null) return 0;
        return level.getGameTime();
    }

    Vec3 getBlockPosInFront();

    SymbolType<?> getSymbolType();

    Item getControlCrystal();

    ItemStackHandler getItemStackHandler();

    default boolean hasControlCrystal() {
        return isAssembled((IDHDPartItem) getControlCrystal());
    }

    IDHDFluidTank getFluidTankItemPart();

    void clearSymbols();

    void activateSymbol(SymbolInterface symbol);

    void pushSymbolButton(SymbolInterface symbol, @Nullable ServerPlayer player, boolean force);

    @ParametersAreNonnullByDefault
    boolean isAssembled(IDHDPartItem part);

    boolean isAssembled();

    /**
     * Called before assembling/disassembling part
     *
     * @param part    the part
     * @param stack   if {@param removed} is true then contains stack being added to the player, otherwise contains stack that player used to assemble this {@param part}
     * @param removed true if part is being removed, otherwise false
     * @return If succeed - true if part should be assembled/disassembled
     */
    default boolean onPartAssembled(IDHDPartItem part, ItemStack stack, boolean removed) {
        return true;
    }

    default Optional<IDHDPartItem> getNextPartToAssemble(Predicate<IDHDPartItem> isAssembledPredicate) {
        return getAllParts().stream().filter((part) -> !isAssembledPredicate.test(part)).findFirst();
    }

    /**
     * @return all parts of the DHD - needs correct order
     */
    LinkedList<IDHDPartItem> getAllParts();

    default Optional<Stargate<?>> getStargate() {
        if (!isLinked()) {
            return Optional.empty();
        }
        return Optional.ofNullable(getLinkedDevice());
    }

    @Nullable
    @Override
    default PointOfOrigin getPointOfOrigin() {
        return getLinkedDeviceOptional().map((sg) -> sg.getPointOfOrigin(getSymbolType())).orElse(null);
    }

    default void setDHDChanged() {
        self().setChanged();
    }

    default ItemStack getDropBlock(ServerPlayer player, BlockState blockState) {
        var stack = new ItemStack(blockState.getBlock());
        var tag = ItemNBT.getOrCreateTag(stack);
        tag.put("parts", getStateManager().serializeAssemblyToNBT());
        tag.put("itemHandler", getItemStackHandler().serializeNBT(CurrentRegistries.getOrThrow()));
        tag.put("tank", getReactorManager().getTank().writeToNBT(CurrentRegistries.getOrThrow(), new CompoundTag()));
        ItemNBT.setTag(stack, tag);
        return stack;
    }

    default void updateFromItemStack(ItemStack stack) {
        var tag = ItemNBT.getOrCreateTag(stack);
        //if (!tag.contains("parts")) return;
        getStateManager().deserializeAssemblyFromNBT(tag.getCompound("parts"));
        getItemStackHandler().deserializeNBT(CurrentRegistries.getOrThrow(), tag.getCompound("itemHandler"));
        getReactorManager().getTank().readFromNBT(CurrentRegistries.getOrThrow(), tag.getCompound("tank"));
        setDHDChanged();
    }

    @ParametersAreNonnullByDefault
    static boolean isPartAssembledOnStack(ItemStack stack, IDHDPartItem part) {
        var tag = ItemNBT.getOrCreateTag(stack);
        if (!tag.contains("parts")) return true;
        return tag.getCompound("parts").getBoolean(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(part.self())).toString());
    }
}
