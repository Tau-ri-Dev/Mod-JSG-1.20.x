package dev.tauri.jsg.recipes;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.entity.StargateAddressData;
import dev.tauri.jsg.api.registry.JSGNotebookPageTypes;
import dev.tauri.jsg.api.registry.JSGSymbolTypes;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.registry.CoreItems;
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
import java.util.Objects;

public class PageAndUniverseDialerRecipe extends ShapelessRecipe {
    public PageAndUniverseDialerRecipe() {
        super(JSGMapping.rl(JSG.MOD_ID, "dialer_page_combination"), "JSG", CraftingBookCategory.MISC,
                DialerRecipeUtils.DIALER_OUT_PAGE.copy(),
                NonNullList.of(
                        Ingredient.EMPTY,
                        Ingredient.of(DialerRecipeUtils.DIALER1.copy()),
                        Ingredient.of(NotebookRecipeUtils.PAGE5.copy())
                )
        );
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(CraftingContainer inv, Level pLevel) {
        int dialerCount = 0;
        int pagesCount = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            var stack = inv.getItem(i);
            var item = stack.getItem();

            if (item == JSGItems.UNIVERSE_DIALER.get())
                dialerCount++;
            else if (item == CoreItems.NOTEBOOK_PAGE_FILLED.get()) {
                var tag = stack.getOrCreateTag();
                var type = NotebookPageType.pageTypeFromCompound(tag);
                if (Objects.equals(type, JSGNotebookPageTypes.STARGATE_ADDRESS.get()) && type.deserializeNBT(tag) instanceof StargateAddressData stargateAddressData) {
                    if (stargateAddressData.getAddress().getSymbolType() == JSGSymbolTypes.UNIVERSE.get()) {
                        pagesCount++;
                        continue;
                    }
                }
                return false;
            } else if (!stack.isEmpty())
                return false;
        }

        return dialerCount == 1 && pagesCount >= 1;
    }

    @Override
    @NotNull
    @ParametersAreNonnullByDefault
    public ItemStack assemble(CraftingContainer inv, RegistryAccess pRegistryAccess) {
        var addressTagList = new ListTag();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            var stack = inv.getItem(i);
            var item = stack.getItem();
            var compound = stack.getTag();

            if (item == JSGItems.UNIVERSE_DIALER.get()) {
                if (compound == null) return ItemStack.EMPTY;
                var modeTag = UniverseDialerModes.MEMORY.getTag(compound);
                var addressTags = modeTag.getList(UDMemoryMode.C_ENTRIES, Tag.TAG_COMPOUND);

                for (var tag : addressTags) {
                    if (!NotebookRecipeUtils.tagListContains(addressTagList, (CompoundTag) tag)) {
                        addressTagList.add(tag);
                    }
                }
            }
            if (item == CoreItems.NOTEBOOK_PAGE_FILLED.get()) {
                if (compound == null) return ItemStack.EMPTY;
                var addressData = JSGNotebookPageTypes.STARGATE_ADDRESS.get().deserializeNBT(compound);
                if (addressData == null) return ItemStack.EMPTY;
                var newTag = addressData.address.serializeNBT();
                newTag.putIntArray(UDMemoryMode.C_E_SYMBOLS, addressData.symbolsToDisplay);
                addressTagList.add(newTag);
            }
        }

        var output = new ItemStack(JSGItems.UNIVERSE_DIALER.get(), 1);
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
