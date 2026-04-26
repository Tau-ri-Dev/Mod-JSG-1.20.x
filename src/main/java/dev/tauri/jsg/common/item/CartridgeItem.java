package dev.tauri.jsg.common.item;

import dev.tauri.jsg.core.common.item.JSGItem;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CartridgeItem extends JSGItem {
    public final Color renderColor;
    public final float inkPerPage;

    public CartridgeItem(Color renderColor, float inkPerPage) {
        super(new Properties(), CoreTabs.TAB_RESOURCES);
        this.renderColor = renderColor;
        this.inkPerPage = inkPerPage;
    }

    @Override
    public @NotNull String getDescriptionId(ItemStack stack) {
        if (stack.hasTag()) {
            var tag = stack.getOrCreateTag();
            if (tag.contains("inkStatus")) {
                if (tag.getDouble("inkStatus") < inkPerPage) {
                    return super.getDescriptionId(stack) + ".empty";
                }
            }
        }
        return super.getDescriptionId(stack);
    }
}
