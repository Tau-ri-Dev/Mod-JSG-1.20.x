package dev.tauri.jsg.recipes.notebook;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.common.item.notebook.NotebookItem;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class NotebookCreationRecipe extends ShapelessRecipe {
    public NotebookCreationRecipe() {
        super(JSGMapping.rl(JSG.MOD_ID, "notebook_creation"), "JSG", CraftingBookCategory.MISC,
                NotebookRecipeUtils.NOTEBOOK.copy(),
                NonNullList.of(
                        Ingredient.of(ItemStack.EMPTY),
                        Ingredient.of(NotebookRecipeUtils.PAGE1.copy()),
                        Ingredient.of(NotebookRecipeUtils.PAGE2.copy())
                )
        );
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(CraftingContainer inv, Level pLevel) {
        int matchCount = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            var stack = inv.getItem(i);
            var item = stack.getItem();

            if (item == CoreItems.NOTEBOOK_PAGE_FILLED.get()) {
                matchCount++;
            } else if (!stack.isEmpty())
                return false;
        }

        return matchCount >= 2;
    }

    @NotNull
    @ParametersAreNonnullByDefault
    public ItemStack assemble(CraftingContainer inv, RegistryAccess pRegistryAccess) {
        var pages = new ListTag();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            var stack = inv.getItem(i);
            var item = stack.getItem();

            if (item == CoreItems.NOTEBOOK_PAGE_FILLED.get()) {
                CompoundTag compound = stack.getTag();

                if (!NotebookRecipeUtils.tagListContains(pages, compound)) {
                    pages.add(compound);
                }
            }
        }
        var output = NotebookItem.createNotebook(pages);
        output.setCount(1);
        return output;
    }
}
