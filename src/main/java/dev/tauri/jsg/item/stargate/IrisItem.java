package dev.tauri.jsg.item.stargate;

import dev.tauri.jsg.api.item.IIrisItem;
import dev.tauri.jsg.api.stargate.iris.EnumIrisType;
import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.item.ICreativeThing;
import dev.tauri.jsg.core.common.item.JSGItem;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class IrisItem extends JSGItem implements ICreativeThing, IIrisItem {

    public static IrisItem createCreative() {
        IrisItem i = new IrisItem(new Item.Properties(), EnumIrisType.IRIS_CREATIVE);
        i.creativeIris = true;
        return i;
    }

    public static IrisItem createShield() {
        IrisItem i = new IrisItem(new Item.Properties(), EnumIrisType.SHIELD);
        i.isShield = true;
        return i;
    }

    public static IrisItem createDurability(int durability, ItemStack repairableItem, EnumIrisType type) {
        IrisItem i = new IrisItem(new Item.Properties().durability(durability), type);
        i.durability = durability;
        i.repairableItem = repairableItem;
        return i;
    }

    public boolean creativeIris = false;
    public boolean isShield = false;
    public final EnumIrisType type;
    public int durability = 0;

    public ItemStack repairableItem = ItemStack.EMPTY;

    public IrisItem(Item.Properties props, EnumIrisType type) {
        super(props, CoreTabs.TAB_UPGRADES);
        this.type = type;
    }

    @Override
    public boolean isCreativeOnly() {
        return creativeIris;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (creativeIris) return;
        if (isShield) return;
        super.setDamage(stack, damage);
        if (getMaxDamage(stack) <= damage) stack.setCount(0);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        ItemHelper.applyGenericToolTip(this.getDescriptionId(), components, tooltipFlag);
    }

    @Override
    public boolean isRepairable(@NotNull ItemStack stack) {
        if (this.creativeIris) return false;
        if (this.isShield) return false;
        if (this.repairableItem == null || this.repairableItem.isEmpty()) return false;
        return (stack.getItem() == repairableItem.getItem());
    }

    @Override
    public boolean isCreative() {
        return creativeIris;
    }

    @Override
    public boolean isShield() {
        return isShield;
    }

    @Override
    public EnumIrisType getType() {
        return type;
    }
}
