package dev.tauri.jsg.registry;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.core.common.registry.helper.TabBuilder;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static dev.tauri.jsg.JSG.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JSGTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER = JSGApi.REGISTRY_HELPER.tab();

    public static final RegistryObject<CreativeModeTab> TAB_MACHINES = REGISTER.register("machines",
            TabBuilder.create(JSGMapping.rl(MOD_ID, "machines"))
                    .withIcon(() -> JSGBlocks.PRINTER).build());

    /**
     * Handling vanilla recipes hook
     */
    @SubscribeEvent
    public static void buildTabsContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(JSGItems.EGG_MASTADGE);
        }
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(JSGItems.FOOD_CHOCOLATE_BAR);
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            for (var r : JSGItems.RECORDS.values()) {
                event.accept(r);
            }
        }
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(JSGBlocks.SG_REDSTONE_DIALER_I_BLOCK.get());
            event.accept(JSGBlocks.SG_REDSTONE_STATE_O_BLOCK.get());
        }
    }

    public static void init() {
    }
}
