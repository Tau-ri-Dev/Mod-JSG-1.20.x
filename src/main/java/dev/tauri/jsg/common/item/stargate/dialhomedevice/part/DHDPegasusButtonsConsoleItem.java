package dev.tauri.jsg.common.item.stargate.dialhomedevice.part;

import dev.tauri.jsg.client.renderer.item.dialhomedevice.part.DHDPegasusButtonsConsoleBEWLR;
import dev.tauri.jsg.core.client.renderer.AbstractItemBEWLR;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import dev.tauri.jsg.core.common.registry.RegistryObject;

import java.util.List;
import java.util.function.Consumer;

public class DHDPegasusButtonsConsoleItem extends DHDAbstractPartItem {
    public DHDPegasusButtonsConsoleItem(Properties properties, List<RegistryObject<CreativeModeTab>> tabs, boolean mandatory, int raycasterId) {
        super(properties, tabs, mandatory, raycasterId);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(AbstractItemBEWLR.create(DHDPegasusButtonsConsoleBEWLR::new));
    }
}
