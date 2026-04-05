package dev.tauri.jsg.recipes.notebook;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.common.item.notebook.NotebookItem;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class NotebookCloneRecipe extends ShapelessRecipe {
    public NotebookCloneRecipe() {
        super(JSGMapping.rl(JSG.MOD_ID, "notebook_clone"), "JSG", CraftingBookCategory.MISC,
                NotebookRecipeUtils.NOTEBOOK2.copy(),
                NonNullList.of(
                        Ingredient.of(ItemStack.EMPTY),
                        Ingredient.of(new ItemStack(Items.BOOK)),
                        Ingredient.of(NotebookRecipeUtils.NOTEBOOK.copy())
                )
        );
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(CraftingContainer inv, Level pLevel) {
        int matchCount = 0;
        boolean hasNote = false;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            var stack = inv.getItem(i);
            var item = stack.getItem();

            if (item == CoreItems.NOTEBOOK_ITEM.get() || item == Items.BOOK) {
                matchCount++;
                if (!hasNote && item == CoreItems.NOTEBOOK_ITEM.get())
                    hasNote = true;
            } else if (!stack.isEmpty())
                return false;
        }

        return hasNote && matchCount >= 2;
    }

    @Override
    @NotNull
    @ParametersAreNonnullByDefault
    public ItemStack assemble(CraftingContainer inv, RegistryAccess pRegistryAccess) {
        int outputCount = 0;
        var pages = new ListTag();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            var stack = inv.getItem(i);
            var item = stack.getItem();

            if (item == CoreItems.NOTEBOOK_ITEM.get()) {
                if (stack.hasTag()) {
                    var notebookTags = stack.getOrCreateTag().getList("addressList", Tag.TAG_COMPOUND);

                    for (var tag : notebookTags) {
                        if (!NotebookRecipeUtils.tagListContains(pages, (CompoundTag) tag)) {
                            pages.add(tag);
                        }
                    }
                }

                outputCount++;
            } else if (item == Items.BOOK) {
                outputCount++;
            }
        }

        if (outputCount == 0)
            outputCount = 1;

        var output = NotebookItem.createNotebook(pages);
        output.setCount(outputCount);

        return output;
    }
}
