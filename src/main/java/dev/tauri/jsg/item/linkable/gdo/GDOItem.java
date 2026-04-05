package dev.tauri.jsg.item.linkable.gdo;

import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.stargate.iris.codesender.CodeSender;
import dev.tauri.jsg.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.helper.LinkingHelper;
import dev.tauri.jsg.core.common.item.JSGItem;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import dev.tauri.jsg.registry.tags.JSGBlockTags;
import dev.tauri.jsg.screen.gui.GDOVirtualGui;
import dev.tauri.jsg.screen.provider.GDOVirtualGuiProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GDOItem extends JSGItem {

    public GDOItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.COMMON), CoreTabs.TAB_TOOLS);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private static final GarageDoorOpenerBEWLR instance = new GarageDoorOpenerBEWLR();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return instance;
            }
        });
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    public boolean sendCode(ItemStack stack, CodeSender sender) {
        var compound = stack.getOrCreateTag();
        if (!compound.contains("linked_gate_pos")) return false;
        var signal = compound.getDouble("signal_strength");
        if (signal <= 0) return false;
        if (compound.getDouble("battery_percentage") <= 0) return false;
        BlockPos tilePos = BlockPos.of(compound.getLong("linked_gate_pos"));
        if (sender.getWorld() == null || !(sender.getWorld().getBlockEntity(tilePos) instanceof StargateAbstractBaseBE gateTile))
            return false;
        return gateTile.sendIrisCode(sender, stack.getOrCreateTag().getString("entered_code"));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isClientSide) {
            if (Minecraft.getInstance().screen instanceof GDOVirtualGui && entity instanceof AbstractClientPlayer player)
                player.displayClientMessage(Component.translatable("item.jsg.gdo.in_menu.alert"), true);
            return;
        }
        if (world.getGameTime() % 20 == 0) {
            BlockPos pos = entity.blockPosition();
            int reachSquared = JSGConfig.DialHomeDevice.universeDialerReach.get() * JSGConfig.DialHomeDevice.universeDialerReach.get() * 2;
            var compound = stack.getOrCreateTag();
            if (compound.contains("linked_gate_pos")) {
                BlockPos tilePos = BlockPos.of(compound.getLong("linked_gate_pos"));

                double signal = Math.max(0, (1 - (tilePos.distSqr(pos) / reachSquared)));
                compound.putDouble("signal_strength", signal);
                if (!world.getBlockState(tilePos).is(JSGBlockTags.ALL_STARGATE_BASES) || signal <= 0) {
                    compound.remove("linked_gate_pos");
                }
            }

            if (!compound.contains("battery_percentage"))
                compound.putDouble("battery_percentage", 1);

            boolean found = false;
            BlockPos targetPos;
            ArrayList<BlockPos> blacklist = new ArrayList<>();
            int loop = 0;
            do {
                loop++;
                targetPos = getNearest(world, pos, blacklist);
                if (targetPos == null)
                    break;

                var gateTile = (StargateAbstractBaseBE) world.getBlockEntity(targetPos);
                if (gateTile == null || !gateTile.isMerged()) {
                    blacklist.add(targetPos);
                    continue;
                }

                compound.putLong("linked_gate_pos", targetPos.asLong());
                break;
            }
            while (!found && loop < 100);

            stack.setTag(compound);
        }
    }

    public BlockPos getNearest(Level world, BlockPos pos, ArrayList<BlockPos> blacklist) {
        return LinkingHelper.findClosestPos(world, pos, new BlockPos(JSGConfig.DialHomeDevice.universeDialerReach.get(), 40, JSGConfig.DialHomeDevice.universeDialerReach.get()), JSGBlockTags.ALL_STARGATE_BASES, blacklist);
    }


    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        ItemHelper.applyGenericToolTip(this.getDescriptionId(), components, tooltipFlag);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (world.isClientSide) {
            //JSGPacketHandler.sendToServer(new GDOCodeKeyPressedToServer(hand, -1));
            GDOVirtualGuiProvider.open();
        }

        return super.use(world, player, hand);
    }
}
