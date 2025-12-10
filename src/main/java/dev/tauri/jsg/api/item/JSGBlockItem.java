package dev.tauri.jsg.api.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JSGBlockItem extends BlockItem implements ITabbedItem {

    private final List<RegistryObject<CreativeModeTab>> tabs;
    protected final Block rawBlock;
    public JSGBlockItem(Block pBlock, Properties pProperties, @Nullable List<RegistryObject<CreativeModeTab>> tabs) {
        super(pBlock, pProperties);
        this.tabs = tabs;
        this.rawBlock = pBlock;
    }

    @Override
    public List<RegistryObject<CreativeModeTab>> getTabs(){
        return tabs;
    }

    public void addAdditional(CreativeModeTab.Output output){}
}
