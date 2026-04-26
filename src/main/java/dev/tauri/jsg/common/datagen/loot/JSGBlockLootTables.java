package dev.tauri.jsg.common.datagen.loot;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.common.registry.JSGBlocks;
import dev.tauri.jsg.core.common.blockstate.JSGProperties;
import dev.tauri.jsg.core.common.registry.CoreItems;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class JSGBlockLootTables extends BlockLootSubProvider {
    protected static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));

    public JSGBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(JSGBlocks.SG_REDSTONE_DIALER_I_BLOCK.get());
        dropSelf(JSGBlocks.SG_REDSTONE_STATE_O_BLOCK.get());

        dropSelf(JSGBlocks.ABYDOS_SAND.get());
        dropSelf(JSGBlocks.ABYDOS_SAND.get());
        dropSelf(JSGBlocks.DHD_MILKYWAY.get());
        dropSelf(JSGBlocks.DHD_PEGASUS.get());
        dropSelf(JSGBlocks.TOASTER.get());
        dropSelf(JSGBlocks.PRINTER.get());


        dropAndCopyNBT(JSGBlocks.STARGATE_MILKYWAY_BASE_BLOCK.get(), CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("itemHandler", "itemHandler").copy("config", "config").copy("stargateEnergyManager", "stargateEnergyManager"));
        dropSelf(JSGBlocks.STARGATE_MILKYWAY_CHEVRON_BLOCK.get());
        dropSelf(JSGBlocks.STARGATE_MILKYWAY_RING_BLOCK.get());

        dropAndCopyNBT(JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK.get(), CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("itemHandler", "itemHandler").copy("config", "config").copy("stargateEnergyManager", "stargateEnergyManager"));
        dropSelf(JSGBlocks.STARGATE_PEGASUS_CHEVRON_BLOCK.get());
        dropSelf(JSGBlocks.STARGATE_PEGASUS_RING_BLOCK.get());

        dropAndCopyNBT(JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK.get(), CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("itemHandler", "itemHandler").copy("config", "config").copy("stargateEnergyManager", "stargateEnergyManager"));
        dropSelf(JSGBlocks.STARGATE_UNIVERSE_CHEVRON_BLOCK.get());
        dropSelf(JSGBlocks.STARGATE_UNIVERSE_RING_BLOCK.get());

        dropAndCopyNBT(JSGBlocks.STARGATE_MOVIE_BASE_BLOCK.get(), CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("itemHandler", "itemHandler").copy("config", "config").copy("stargateEnergyManager", "stargateEnergyManager"));
        dropSelf(JSGBlocks.STARGATE_MOVIE_CHEVRON_BLOCK.get());
        dropSelf(JSGBlocks.STARGATE_MOVIE_RING_BLOCK.get());

        dropAndCopyNBT(JSGBlocks.STARGATE_TOLLAN_BASE_BLOCK.get(), CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("itemHandler", "itemHandler").copy("config", "config").copy("stargateEnergyManager", "stargateEnergyManager"));
        dropSelf(JSGBlocks.STARGATE_TOLLAN_CHEVRON_BLOCK.get());
        dropSelf(JSGBlocks.STARGATE_TOLLAN_RING_BLOCK.get());

        add(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get(), LootTable.lootTable()
                .withPool(applyExplosionCondition(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get(), LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(LootItem.lootTableItem(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get()).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("openCount", "openCount")))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(JSGProperties.ORLIN_BROKEN, false)))
                ))
                .withPool(applyExplosionCondition(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get(), LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(LootItem.lootTableItem(Items.COPPER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2))))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(JSGProperties.ORLIN_BROKEN, true)))
                ))
                .withPool(applyExplosionCondition(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get(), LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(LootItem.lootTableItem(Items.REDSTONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3))))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(JSGProperties.ORLIN_BROKEN, true)))
                ))
                .withPool(applyExplosionCondition(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get(), LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(LootItem.lootTableItem(Items.PAPER).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(JSGProperties.ORLIN_BROKEN, true)))
                ))
        );
        add(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get(), LootTable.lootTable()
                .withPool(applyExplosionCondition(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get(), LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(LootItem.lootTableItem(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get()).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("openCount", "openCount").copy("notebook_page", "notebook_page").copy("stargateEnergyManager", "stargateEnergyManager")))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(JSGProperties.ORLIN_BROKEN, false)))
                ))
                .withPool(applyExplosionCondition(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get(), LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(LootItem.lootTableItem(Items.COPPER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(JSGProperties.ORLIN_BROKEN, true)))
                ))
                .withPool(applyExplosionCondition(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get(), LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(LootItem.lootTableItem(Items.REDSTONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2))))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(JSGProperties.ORLIN_BROKEN, true)))
                ))
                .withPool(applyExplosionCondition(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get(), LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(LootItem.lootTableItem(CoreItems.NOTEBOOK_PAGE_EMPTY.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1))))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(JSGProperties.ORLIN_BROKEN, true)))
                ))
        );
    }

    @Override
    @NotNull
    protected Iterable<Block> getKnownBlocks() {
        return JSGApi.REGISTRY_HELPER.block().getEntries().stream().map(RegistryObject::get)::iterator;
    }

    protected void dropNothing(Block block) {
        add(block, LootTable.lootTable());
    }

    protected void dropOre(Block block, Item item) {
        add(block, createOreDrop(block, item));
    }

    protected void dropOre(Block block, Item item, NumberProvider dropsCount) {
        add(block, createSilkTouchDispatchTable(block, applyExplosionDecay(block, LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(dropsCount)).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))));
    }

    protected void dropAndCopyNBT(Block block, CopyNbtFunction.Builder copyNbtFunctionBuilder) {
        add(block, LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(LootItem.lootTableItem(block).apply(copyNbtFunctionBuilder))
                ))
        );
    }
}
