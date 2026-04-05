package dev.tauri.jsg.entity;

import dev.tauri.jsg.registry.JSGVillagers;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VillagerTradesBuilder {
    private ItemStack input1;
    @Nullable
    private ItemStack input2;
    private ItemStack output;
    private int maxUses = 10;
    private int xpPerTrade = 1;
    private float priceMultiplier = 0.2f;
    private int[] forLevels = {1, 2, 3, 4, 5};
    private List<VillagerProfession> forProfessions = null;
    private boolean priestOnly = false;
    @NotNull
    private final VillagerTradesEvent event;

    private VillagerTradesBuilder(@NotNull VillagerTradesEvent event) {
        this.event = event;
    }

    public VillagerTradesBuilder setInput1(ItemStack stack) {
        this.input1 = stack;
        return this;
    }

    public VillagerTradesBuilder setInput2(ItemStack stack) {
        this.input2 = stack;
        return this;
    }

    public VillagerTradesBuilder setOutput(ItemStack stack) {
        this.output = stack;
        return this;
    }

    public VillagerTradesBuilder setMaxUses(int maxUses) {
        this.maxUses = maxUses;
        return this;
    }

    public VillagerTradesBuilder setXpPerTrade(int xpPerTrade) {
        this.xpPerTrade = xpPerTrade;
        return this;
    }

    public VillagerTradesBuilder setPriceMul(float priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
        return this;
    }

    public VillagerTradesBuilder setForLevels(int... levels) {
        this.forLevels = levels;
        return this;
    }

    public VillagerTradesBuilder setForProfessions(VillagerProfession... professions) {
        this.forProfessions = List.of(professions);
        return this;
    }

    public VillagerTradesBuilder setPriestOnly() {
        this.priestOnly = true;
        return this;
    }

    public VillagerTradesBuilder register(VillagerTrades.ItemListing offer) {
        if (priestOnly && !JSGVillagers.isPriest(event.getType())) return create(event);
        if (forProfessions != null && !forProfessions.isEmpty() && !forProfessions.contains(event.getType()))
            return create(event);
        var trades = event.getTrades();
        for (int level : forLevels) {
            trades.get(level).add(offer);
        }
        return create(event);
    }

    public VillagerTradesBuilder register() {
        if (input1 == null || output == null) {
            throw new NullPointerException("Tried to register villager trade with null input/output!");
        }
        if (priestOnly && !JSGVillagers.isPriest(event.getType())) return create(event);
        if (forProfessions != null && !forProfessions.isEmpty() && !forProfessions.contains(event.getType()))
            return create(event);
        var trades = event.getTrades();
        for (int level : forLevels) {
            VillagerTrades.ItemListing lambda;
            if (input2 != null)
                lambda = (trader, random) -> new MerchantOffer(input1, input2, output, maxUses, xpPerTrade, priceMultiplier);
            else
                lambda = (trader, random) -> new MerchantOffer(input1, output, maxUses, xpPerTrade, priceMultiplier);
            trades.get(level).add(lambda);
        }
        return create(event);
    }

    public static VillagerTradesBuilder create(@NotNull VillagerTradesEvent event) {
        return new VillagerTradesBuilder(event);
    }
}
