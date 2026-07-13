package dev.tauri.jsg.common.item.stargate.dialhomedevice.part;

import dev.tauri.jsg.api.item.IDHDPartItem;
import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.item.JSGItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import dev.tauri.jsg.core.common.registry.RegistryObject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DHDAbstractPartItem extends JSGItem implements IDHDPartItem {
    public final boolean mandatory;
    public final int raycasterId;
    protected List<Supplier<? extends IDHDPartItem>> partsNeededBeforeAssembly = new ArrayList<>();
    protected List<Supplier<? extends IDHDPartItem>> partsNeededBeforeRemoval = new ArrayList<>();
    protected List<Supplier<? extends IDHDPartItem>> partsNeededRemovedBeforeAssembly = new ArrayList<>();

    public DHDAbstractPartItem(Properties properties, boolean mandatory, int raycasterId) {
        super(properties);
        this.mandatory = mandatory;
        this.raycasterId = raycasterId;
    }

    public DHDAbstractPartItem(Properties properties, RegistryObject<CreativeModeTab> tab, boolean mandatory, int raycasterId) {
        super(properties, tab);
        this.mandatory = mandatory;
        this.raycasterId = raycasterId;
    }

    public DHDAbstractPartItem(Properties properties, List<RegistryObject<CreativeModeTab>> tabs, boolean mandatory, int raycasterId) {
        super(properties, tabs);
        this.mandatory = mandatory;
        this.raycasterId = raycasterId;
    }

    public DHDAbstractPartItem withPartsNeededBeforeAssembly(List<Supplier<? extends IDHDPartItem>> parts) {
        this.partsNeededBeforeAssembly = parts;
        return this;
    }

    public DHDAbstractPartItem withPartsNeededBeforeRemoval(List<Supplier<? extends IDHDPartItem>> parts) {
        this.partsNeededBeforeRemoval = parts;
        return this;
    }

    public DHDAbstractPartItem withPartsNeededRemovedBeforeAssembly(List<Supplier<? extends IDHDPartItem>> parts) {
        this.partsNeededRemovedBeforeAssembly = parts;
        return this;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        ItemHelper.applyGenericToolTip(this.getDescriptionId(), components, tooltipFlag);
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public int getRaycasterButtonID() {
        return raycasterId;
    }

    @Override
    public List<? extends IDHDPartItem> getPartsNeededAssembledBeforeAssembly() {
        return partsNeededBeforeAssembly.stream().map(Supplier::get).toList();
    }

    @Override
    public List<? extends IDHDPartItem> getPartsNeededToRemoveBeforeRemoval() {
        return partsNeededBeforeRemoval.stream().map(Supplier::get).toList();
    }

    @Override
    public List<? extends IDHDPartItem> getPartsNeededRemovedBeforeAssembly() {
        return partsNeededRemovedBeforeAssembly.stream().map(Supplier::get).toList();
    }
}
