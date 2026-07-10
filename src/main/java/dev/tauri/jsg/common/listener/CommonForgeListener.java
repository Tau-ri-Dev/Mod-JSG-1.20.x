package dev.tauri.jsg.common.listener;

import net.neoforged.fml.common.EventBusSubscriber;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.blockentity.dialhomedevice.DHDAbstractBE;
import dev.tauri.jsg.common.entity.behaviour.DialGateBehaviour;
import dev.tauri.jsg.common.recipes.PageAndUniverseDialerRecipe;
import dev.tauri.jsg.common.recipes.StargateOrlinBaseBlockRecipe;
import dev.tauri.jsg.common.recipes.UniverseDialerCloneRecipe;
import dev.tauri.jsg.common.registry.JSGVillagers;
import dev.tauri.jsg.common.registry.util.VillagerUtil;
import dev.tauri.jsg.common.stargate.network.StargateNetwork;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.util.CreativeItemsChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import dev.tauri.jsg.core.common.registry.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;


@EventBusSubscriber(modid = JSG.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CommonForgeListener {
    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre e) {
        var p = e.getPlayer();
        var i = e.getItemEntity().getItem();
        if (CreativeItemsChecker.canInteractWith(i, p.isCreative())) return;
        e.setCanPickup(net.neoforged.neoforge.common.util.TriState.FALSE);
    }

    @SubscribeEvent
    public static void onVillagerSpawn(net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) return;
        if (JSGVillagers.isPriest(villager.getVillagerData().getProfession())) {
            villager.getBrain().addActivity(Activity.IDLE, ImmutableList.of(Pair.of(0, new DialGateBehaviour())));
        }

        // todo: fix activity not being set after profession change
        if (villager.getVillagerData().getProfession() == JSGVillagers.SLAVE_MINER.get()) {
            villager.getBrain().addActivityWithConditions(Activity.WORK, VillagerUtil.getWorkPackageForSlave((blockState) -> CoreBlocks.ORE_NAQUADAH.values().stream().map(RegistryObject::get).anyMatch(block -> block == blockState.getBlock()), 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
            villager.getInventory().setItem(0, new ItemStack(Items.IRON_PICKAXE));
        }
        JSGVillagers.checkAndGetCustomType(villager).ifPresent(villagerType -> villager.setVillagerData(villager.getVillagerData().setType(villagerType)));
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        JSGVillagers.registerTrades(event);
    }

    @SubscribeEvent
    public static void onArrowHit(ProjectileImpactEvent event) {
        boolean isSnowball = event.getProjectile() instanceof Snowball;
        var result = event.getRayTraceResult();
        if (result.getType() != HitResult.Type.BLOCK) return;
        if (event.getEntity().level().isClientSide) return;
        var be = event.getProjectile().level().getBlockEntity(BlockPos.containing(event.getRayTraceResult().getLocation()));
        var be2 = event.getProjectile().level().getBlockEntity(BlockPos.containing(event.getRayTraceResult().getLocation().add(0, -1, 0)));
        DHDAbstractBE dhdTile;
        if (!(be instanceof DHDAbstractBE dhdTile1)) {
            if (!(be2 instanceof DHDAbstractBE dhdTile2)) return;
            else dhdTile = dhdTile2;
        } else dhdTile = dhdTile1;
        if (!dhdTile.isLinked()) return;
        var gate = dhdTile.getLinkedDevice();
        if (gate != null) {
            if (gate.getDialingManager().getDialedAddress().size() == 6) {
                dhdTile.pushSymbolButton(dhdTile.getSymbolType().getOrigin(), null, false);
                return;
            }
            if (gate.getDialingManager().getDialedAddress().size() == 7 && dhdTile.getSymbolType().getBRB() != null) {
                dhdTile.pushSymbolButton(dhdTile.getSymbolType().getBRB(), null, false);
                return;
            }
        }

        int symbolsCount = isSnowball ? 3 : 1;
        var r = new Random();
        for (int i = 0; i < symbolsCount; i++)
            dhdTile.pushSymbolButton(dhdTile.getSymbolType().getRandomSymbol(r), null, false);
    }

    @SubscribeEvent
    public static void onServerResourceReload(AddReloadListenerEvent event) {
        event.addListener((PreparableReloadListener.PreparationBarrier pPreparationBarrier,
                           ResourceManager pResourceManager,
                           ProfilerFiller pPreparationsProfiler,
                           ProfilerFiller pReloadProfiler,
                           Executor pBackgroundExecutor,
                           Executor pGameExecutor) -> pPreparationBarrier.wait(Unit.INSTANCE).thenRun(() -> {
            var recipesManager = event.getServerResources().getRecipeManager();
            var recipes = recipesManager.getRecipes();

            recipes.add(new net.minecraft.world.item.crafting.RecipeHolder<>(UniverseDialerCloneRecipe.ID, new UniverseDialerCloneRecipe()));
            recipes.add(new net.minecraft.world.item.crafting.RecipeHolder<>(PageAndUniverseDialerRecipe.ID, new PageAndUniverseDialerRecipe()));
            recipes.add(new net.minecraft.world.item.crafting.RecipeHolder<>(StargateOrlinBaseBlockRecipe.ID, new StargateOrlinBaseBlockRecipe()));

            recipesManager.replaceRecipes(recipes);
            JSG.logger.info("Recipes successfully reloaded!");
        }));
    }

    private static final List<ResourceKey<LevelStem>> REMOVED_LEVELS = new ArrayList<>();

    @SubscribeEvent
    public static void checkSGNIntegrity(ServerAboutToStartEvent event) {
        var server = event.getServer();
        var registry = server.registryAccess().registry(Registries.LEVEL_STEM);
        REMOVED_LEVELS.clear();
        if (registry.isEmpty()) return;
        if (!(registry.get() instanceof MappedRegistry<LevelStem> mappedRegistry)) return;
        mappedRegistry.registryKeySet().stream()
                .filter(registryEntry -> !DimensionType.getStorageFolder(ResourceKey.create(Registries.DIMENSION, registryEntry.location()), server.getWorldPath(LevelResource.ROOT)).toFile().exists())
                .forEach(REMOVED_LEVELS::add);
    }

    @SubscribeEvent
    public static void updateSGNIntegrity(ServerStartedEvent event) {
        REMOVED_LEVELS.forEach(registryEntry -> StargateNetwork.INSTANCE.removeStargatesCauseDimDeleted(registryEntry));
        REMOVED_LEVELS.clear();
        StargateNetwork.INSTANCE.checkForInvalidDims();
    }
}
