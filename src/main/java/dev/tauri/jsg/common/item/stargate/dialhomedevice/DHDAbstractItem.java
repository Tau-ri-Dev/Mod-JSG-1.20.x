package dev.tauri.jsg.common.item.stargate.dialhomedevice;

import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.item.JSGBlockItem;
import dev.tauri.jsg.core.common.item.JSGModelOBJInGUIRenderer;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

public abstract class DHDAbstractItem extends JSGBlockItem {
    public DHDAbstractItem(Block block) {
        super(block, new Item.Properties(), List.of(CoreTabs.TAB_TRANSPORTATION));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private static final JSGModelOBJInGUIRenderer instance = new JSGModelOBJInGUIRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                instance.renderPartInterface = getRenderPartInterface();
                return instance;
            }
        });
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        ItemHelper.applyGenericToolTip(this.getDescriptionId(), components, tooltipFlag);
    }

    public abstract JSGModelOBJInGUIRenderer.RenderPartInterface getRenderPartInterface();
}
