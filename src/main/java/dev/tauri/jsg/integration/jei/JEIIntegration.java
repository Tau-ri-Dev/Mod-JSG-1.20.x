package dev.tauri.jsg.integration.jei;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.client.screen.inventory.DHDContainerGui;
import dev.tauri.jsg.client.screen.inventory.StargateContainerGui;
import dev.tauri.jsg.core.common.integration.jei.JEIAdvancedGuiHandler;
import dev.tauri.jsg.core.mapping.JSGMapping;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
@SuppressWarnings("unused")
public final class JEIIntegration implements IModPlugin {
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(StargateContainerGui.class, new JEIAdvancedGuiHandler());
        registration.addGuiContainerHandler(DHDContainerGui.class, new JEIAdvancedGuiHandler());
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return JSGMapping.rl(JSG.MOD_ID, "jei_plugin");
    }
}
