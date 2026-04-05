package dev.tauri.jsg.recipes;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.entity.StargateAddressData;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.recipes.notebook.NotebookRecipeUtils;
import dev.tauri.jsg.registry.JSGBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class StargateOrlinBaseBlockRecipe extends ShapedRecipe {
    @SuppressWarnings("all")
    public static ItemStack getOrlinGate(ItemStack page) {
        var stack = new ItemStack(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get());
        var tag = stack.getOrCreateTag();
        tag.put("notebook_page", page.getOrCreateTag().copy());
        stack.setTag(tag);
        return stack;
    }

    public StargateOrlinBaseBlockRecipe() {
        super(JSGMapping.rl(JSG.MOD_ID, "orlin_base_block"), "JSG", CraftingBookCategory.BUILDING, 3, 3, NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(new ItemStack(Items.COPPER_INGOT)), Ingredient.of(NotebookRecipeUtils.PAGE1.copy()), Ingredient.of(new ItemStack(Items.COPPER_INGOT)),
                Ingredient.of(new ItemStack(Items.COPPER_INGOT)), Ingredient.of(new ItemStack(JSGBlocks.TOASTER.get())), Ingredient.of(new ItemStack(Items.COPPER_INGOT)),
                Ingredient.of(new ItemStack(Items.REPEATER)), Ingredient.of(new ItemStack(CoreItems.TITANIUM_DUST.get())), Ingredient.of(new ItemStack(Items.REPEATER))
        ), getOrlinGate(NotebookRecipeUtils.PAGE1.copy()));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(CraftingContainer inv, Level world) {
        for (int i = 0; i < inv.getContainerSize(); i++) {
            var stack = inv.getItem(i);
            var item = stack.getItem();
            if (getIngredients().get(i).getItems()[0].getItem() != item) return false;
            if (i == 1) {
                // slot where notebook page should be at
                if (!stack.hasTag() || stack.getTag() == null) return false;
                var dataWrapper = NotebookPageType.pageDataFromCompound(stack.getOrCreateTag());
                if (dataWrapper == null) return false;
                if (!(dataWrapper.data() instanceof StargateAddressData stargateAddressData) || stargateAddressData.getAddress().getSymbolType() != StargateTypes.ORLIN.get().symbolType.get())
                    return false;
            }
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull ItemStack assemble(CraftingContainer inv, RegistryAccess level) {
        var page = inv.getItem(1);
        if (page.isEmpty()) return ItemStack.EMPTY;
        if (page.getItem() != CoreItems.NOTEBOOK_PAGE_FILLED.get()) return ItemStack.EMPTY;
        var tag = page.getTag();
        if (tag == null) return ItemStack.EMPTY;
        return getOrlinGate(page);
    }
}
