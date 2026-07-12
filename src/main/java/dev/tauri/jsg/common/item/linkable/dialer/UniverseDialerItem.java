package dev.tauri.jsg.common.item.linkable.dialer;

import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.api.registry.JSGUniverseDialerModes;
import dev.tauri.jsg.client.renderer.item.dialer.UniverseDialerBEWLR;
import dev.tauri.jsg.client.screen.gui.DialerVirtualGui;
import dev.tauri.jsg.common.item.linkable.dialer.modes.UDMemoryMode;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.item.JSGItem;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

public class UniverseDialerItem extends JSGItem {
    public static final String C_MODE = "selectedMode";
    public static final String C_MODE_TAG = "_tag";
    public static final String C_BIOME_OVERLAY = "biomeOverlay";

    public UniverseDialerItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON), CoreTabs.TAB_TOOLS);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private static final UniverseDialerBEWLR instance = new UniverseDialerBEWLR();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return instance;
            }
        });
    }

    @Override
    @ParametersAreNonnullByDefault
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isClientSide) {
            if (Minecraft.getInstance().screen instanceof DialerVirtualGui && entity instanceof AbstractClientPlayer player)
                player.displayClientMessage(Component.translatable("item.jsg.universe_dialer.in_menu.alert"), true);
            return;
        }
        var compound = ItemNBT.getOrCreateTag(stack);
        boolean changed = false;
        if (!compound.contains(C_MODE, Tag.TAG_STRING)) {
            compound.putString(C_MODE, UniverseDialerMode.getDefault().id.toString());
            changed = true;
        }

        if (world.getGameTime() % 20 == 0) {
            compound.putString(C_BIOME_OVERLAY, BiomeOverlayInstance.getUpdatedBiomeOverlay(world, entity.blockPosition()).getId().toString());
            changed = true;
        }

        var mode = UniverseDialerMode.valueOf(JSGMapping.rl(compound.getString(C_MODE))).orElse(UniverseDialerMode.getDefault());
        if (!mode.id.toString().equals(compound.getString(C_MODE))) {
            compound.putString(C_MODE, mode.id.toString());
            changed = true;
        }
        mode.inventoryTick(stack, mode.getTag(compound), world, entity, itemSlot, isSelected);
        // ItemNBT tags are copies of the custom_data component - persist the mode's mutations too
        ItemNBT.setTag(stack, compound);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        ItemHelper.applyGenericToolTip(this.getDescriptionId(), components, tooltipFlag);

        var list = ItemNBT.getOrCreateTag(stack).getCompound(JSGUniverseDialerModes.MEMORY.get().id + C_MODE_TAG).getList(UDMemoryMode.C_ENTRIES, Tag.TAG_COMPOUND);
        components.add(Component.literal(ChatFormatting.GRAY + I18n.format("item.jsg.universe_dialer.saved_gates", list.size())));

        for (int i = 0; i < list.size(); i++) {
            var compound = list.getCompound(i);

            if (compound.contains(UDMemoryMode.C_E_NAME)) {
                components.add(Component.literal(ChatFormatting.AQUA + compound.getString(UDMemoryMode.C_E_NAME)));
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (stack.getItem() != this)
            return super.use(world, player, hand);

        var compound = ItemNBT.getOrCreateTag(stack);
        if (!compound.contains(C_MODE, Tag.TAG_STRING))
            return super.use(world, player, hand);

        var mode = UniverseDialerMode.valueOf(JSGMapping.rl(compound.getString(C_MODE))).orElse(UniverseDialerMode.getDefault());
        if (!mode.id.toString().equals(compound.getString(C_MODE)))
            return super.use(world, player, hand);

        var result = mode.onUse(mode.getTag(compound), stack, world, player, hand, player.isShiftKeyDown());
        if (!world.isClientSide)
            ItemNBT.setTag(stack, compound);
        if (result)
            return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
        return super.use(world, player, hand);
    }
}
