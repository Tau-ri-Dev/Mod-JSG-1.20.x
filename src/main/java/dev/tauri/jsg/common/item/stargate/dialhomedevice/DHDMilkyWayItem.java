package dev.tauri.jsg.common.item.stargate.dialhomedevice;

import dev.tauri.jsg.client.renderer.item.dialhomedevice.DHDMilkyWayBEWLR;
import dev.tauri.jsg.core.client.renderer.AbstractItemBEWLR;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class DHDMilkyWayItem extends DHDAbstractItem {
    public DHDMilkyWayItem(Block block) {
        super(block);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public IClientItemExtensions createBEWLR() {
        return AbstractItemBEWLR.create(DHDMilkyWayBEWLR::new);
    }
}
