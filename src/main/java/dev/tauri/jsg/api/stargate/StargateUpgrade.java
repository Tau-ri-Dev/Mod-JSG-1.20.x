package dev.tauri.jsg.api.stargate;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.core.common.item.IUpgradeItem;
import dev.tauri.jsg.core.common.util.IUpgrade;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class StargateUpgrade implements IUpgrade {
    protected static final Map<ResourceLocation, StargateUpgrade> UPGRADES = new HashMap<>();
    public final ResourceLocation id;

    public StargateUpgrade(ResourceLocation id) {
        this.id = id;
        UPGRADES.put(id, this);
    }


    public static final StargateUpgrade MILKYWAY_GLYPHS = new StargateUpgrade(JSGMapping.rl(JSGApi.MOD_ID, "milkyway"));
    public static final StargateUpgrade PEGASUS_GLYPHS = new StargateUpgrade(JSGMapping.rl(JSGApi.MOD_ID, "pegasus"));
    public static final StargateUpgrade UNIVERSE_GLYPHS = new StargateUpgrade(JSGMapping.rl(JSGApi.MOD_ID, "universe"));
    public static final StargateUpgrade CHEVRON_UPGRADE = new StargateUpgrade(JSGMapping.rl(JSGApi.MOD_ID, "chevron"));

    public static boolean contains(Item item) {
        if (!(item instanceof IUpgradeItem upgradeItem)) return false;
        return UPGRADES.values().stream().anyMatch((u) -> upgradeItem.getUpgrade() == u);
    }
}
