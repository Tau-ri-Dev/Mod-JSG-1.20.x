package dev.tauri.jsg.item.admincontroller;

import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.blockentity.stargate.StargateAbstractMemberBE;
import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.helper.LinkingHelper;
import dev.tauri.jsg.core.common.helper.RayTraceHelper;
import dev.tauri.jsg.core.common.item.JSGItem;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import dev.tauri.jsg.packet.JSGPacketHandler;
import dev.tauri.jsg.packet.packets.AdminControllerGuiOpenToClient;
import dev.tauri.jsg.registry.JSGItems;
import dev.tauri.jsg.registry.tags.JSGBlockTags;
import dev.tauri.jsg.stargate.network.StargateNetwork;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class AdminControllerItem extends JSGItem {
    public AdminControllerItem() {
        super(new Properties().stacksTo(1), CoreTabs.TAB_TOOLS);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private static final AdminControllerBEWLR instance = new AdminControllerBEWLR();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return instance;
            }
        });
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        ItemHelper.applyGenericToolTip(Objects.requireNonNull(JSGItems.ADMIN_CONTROLLER.getId()).getPath(), components, tooltipFlag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return (oldStack != null && newStack != null && oldStack.getItem() != newStack.getItem());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean inHand) {
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide) {
            if (player instanceof ServerPlayer sp) {
                Object te = RayTraceHelper.rayTraceTileEntity(player, 20);

                if (te instanceof StargateAbstractMemberBE) {
                    // If member, get base block
                    te = ((StargateAbstractMemberBE) te).getBaseTile(world);
                }

                if (!(te instanceof Stargate<?>)) {
                    te = LinkingHelper.findClosestTile(world, player.blockPosition(), JSGBlockTags.ALL_STARGATE_BASES, Stargate.class, 20, 20);
                }

                if (te instanceof Stargate<?> baseTile && baseTile.isMerged()) {
                    // Set linked gate for updating
                    CompoundTag compound = player.getItemInHand(hand).getTag();
                    if (compound == null) compound = new CompoundTag();
                    compound.putLong("linkedGatePos", baseTile.blockPosition().asLong());
                    player.getItemInHand(hand).setTag(compound);

                    // Open GUI for the player
                    JSGPacketHandler.sendTo(new AdminControllerGuiOpenToClient(baseTile.blockPosition(), StargateNetwork.INSTANCE), sp);
                } else
                    JSGPacketHandler.sendTo(new AdminControllerGuiOpenToClient(StargateNetwork.INSTANCE), sp);
                return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), false);
            }
        }
        return super.use(world, player, hand);
    }
}
