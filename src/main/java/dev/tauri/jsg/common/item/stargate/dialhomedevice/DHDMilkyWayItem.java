package dev.tauri.jsg.common.item.stargate.dialhomedevice;

import dev.tauri.jsg.client.renderer.item.dialhomedevice.DHDMilkyWayBEWLR;
import dev.tauri.jsg.core.client.renderer.AbstractItemBEWLR;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

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
