package dev.tauri.jsg.common.item.stargate.dialhomedevice;

import dev.tauri.jsg.client.renderer.item.dialhomedevice.DHDPegasusBEWLR;
import dev.tauri.jsg.core.client.renderer.AbstractItemBEWLR;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class DHDPegasusItem extends DHDAbstractItem {
    public DHDPegasusItem(Block block) {
        super(block);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public IClientItemExtensions createBEWLR() {
        return AbstractItemBEWLR.create(DHDPegasusBEWLR::new);
    }
}
