package dev.tauri.jsg.recipes;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.mapping.JSGMapping;
import dev.tauri.jsg.item.linkable.dialer.UniverseDialerItem;
import dev.tauri.jsg.item.linkable.dialer.modes.UDMemoryMode;
import dev.tauri.jsg.item.linkable.dialer.modes.UniverseDialerModes;
import dev.tauri.jsg.recipes.notebook.NotebookRecipeUtils;
import dev.tauri.jsg.registry.JSGItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class UniverseDialerCloneRecipe extends ShapelessRecipe {
    public UniverseDialerCloneRecipe() {
        super(JSGMapping.rl(JSG.MOD_ID, "universe_dialer_clone"), "JSG", CraftingBookCategory.MISC,
                DialerRecipeUtils.DIALER_OUT.copy(),
                NonNullList.of(
                        Ingredient.EMPTY,
                        Ingredient.of(DialerRecipeUtils.DIALER1.copy()),
                        Ingredient.of(DialerRecipeUtils.DIALER2.copy())
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

            if (item == JSGItems.UNIVERSE_DIALER.get())
                matchCount++;
            else if (!stack.isEmpty())
                return false;
        }

        return matchCount >= 2;
    }

    @Override
    @NotNull
    @ParametersAreNonnullByDefault
    public ItemStack assemble(CraftingContainer inv, RegistryAccess pRegistryAccess) {
        int outputCount = 0;
        var addressTagList = new ListTag();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            var stack = inv.getItem(i);
            var item = stack.getItem();

            if (item == JSGItems.UNIVERSE_DIALER.get()) {
                var modeTag = UniverseDialerModes.MEMORY.getTag(stack.getOrCreateTag());
                var addressTags = modeTag.getList(UDMemoryMode.C_ENTRIES, Tag.TAG_COMPOUND);

                for (var tag : addressTags) {
                    if (!NotebookRecipeUtils.tagListContains(addressTagList, (CompoundTag) tag)) {
                        addressTagList.add(tag);
                    }
                }

                outputCount++;
            }
        }

        var output = new ItemStack(JSGItems.UNIVERSE_DIALER.get(), outputCount);
        var compound = new CompoundTag();
        var modeTag = UniverseDialerModes.MEMORY.getTag(compound);
        modeTag.put(UDMemoryMode.C_ENTRIES, addressTagList);
        compound.put(UniverseDialerModes.MEMORY.id + UniverseDialerItem.C_MODE_TAG, modeTag);
        output.setTag(compound);

        return output;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPELESS_RECIPE;
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }
}
