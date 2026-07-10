# JSG migration: 1.20.1 Forge → 1.21.1 NeoForge

Working document — plan, decisions, and current progress. Pick up from **Current status** below.

## Context

The mod (`jsg`, this repo, 474 Java files / ~49k LOC) hard-depends on the framework library **jsg-core**
(`eu.tauridev:jsg-core-1.20.1`, 419 source files / ~33.5k LOC, imported by 307 mod files). Its GitHub repo
(Tau-ri-Dev/Mod-JSGCore-public) is empty, but the full **sources jar is published on Maven Central**
(version `1.0.0.0-Dev20260705-233348`). No 1.21.1 core exists anywhere → **core must be ported first**.

## Fixed decisions

- jsg-core: ported in `Mod-JSGCore-1.21.x/` (subdirectory of this repo, own nested git repo, branch `main`).
  **No publishing** — the core build just produces a jar; the JSG mod build consumes it via a local file path
  (`Mod-JSGCore-1.21.x/build/libs/jsg-core-1.21.1-1.0.0.0-Dev.jar`; version deliberately not timestamped).
- All integrations stay: CC:Tweaked, JEI, Create ecosystem, StellarView, Mekanism.
  OC2 + Tinkers' Construct have no 1.21.x builds → compile-time disabled (`enable_oc2` gradle flag / dep removal),
  clean re-enable path kept.
- Mod migration happens on a new branch `1.21.1` in this repo (branch not yet created).
- Build tooling: **ModDevGradle 2.x** (not NeoGradle). Preserve core's public API shape wherever possible
  (RegistryHelper, JSGMapping.rl, SimplePacketHandler method names) to minimize edits across the 307 dependent files.

## Environment quirks (important)

- We run inside flatpak: **every java/gradle call = `flatpak-spawn --host ./gradlew <task>`**.
  Host default Java is 21 (OpenJDK 21.0.11) — verified.
- Some file writes get declined by the user's permission setup (observed: `build.gradle`,
  `accesstransformer*.cfg`, incl. reads). Workaround used so far: write content under a neutral name
  (e.g. `core.gradle`) and the user renames it manually, or paste content in chat.

## Verified version targets (resolved 2026-07-10)

| Dep | 1.21.1 target |
|---|---|
| NeoForge | 21.1.235 |
| ModDevGradle | 2.0.141 |
| Parchment | 1.21.1 / 2024.11.17 |
| JEI | 19.32.0.359 (`mezz.jei:jei-1.21.1-neoforge(-api)`, `jei-1.21.1-common-api`) |
| CC:Tweaked | 1.120.0 — **the 1.21.1 `-forge` artifacts ARE NeoForge builds** (`cc.tweaked:cc-tweaked-1.21.1-core-api/-forge-api/-forge`, maven.squiddev.cc) |
| StellarView | 0.5.2 NeoForge = `maven.modrinth:stellarview:QD13LaRR` |
| Create / Ponder / Flywheel / Registrate | 6.0.10+mc1.21.1 / ponder-neoforge 1.21.1 / flywheel-neoforge-1.21.1 1.0.6 / MC1.21-1.3.0+ (verify when reaching M0) |
| Mekanism (runtimeOnly) | 10.7.19.85 |
| MixinExtras / Embeddium / Oculus | dropped (no mixins in either codebase) |
| OC2 / Tinkers | disabled — no 1.21.x builds |

## Current status (2026-07-10)

### Done
- **Phase 0**: host Java 21 verified; core sources extracted from Maven Central sources jar into
  `Mod-JSGCore-1.21.x/`; nested git repo initialized, pristine sources committed (`dc140f6`, checkpoint C0).
- **Core C1 (build bootstrap) — nearly done**:
  - `build.gradle` (MDG 2.0.141, Java 21, parchment, runs, `enable_oc2` sourceset exclude, JEI/CCT/StellarView
    compileOnly deps; written as `core.gradle`, renamed by user). A stray `core.gradle` copy may still exist — delete it.
  - `settings.gradle`, `gradle.properties`, gradle wrapper (8.14.4) in place.
  - `META-INF/neoforge.mods.toml` written (static, modId `jsg_core`, loaderVersion `[4,)`, neoforge `[21.1.0,)`).
  - Old `mods.toml` + `pack.mcmeta` removed (NeoForge 1.21.1 mods need no pack.mcmeta).

### MOD IN PROGRESS (2026-07-10, session 2): branch `1.21.1`, 3144 -> 628 compile errors
M0 (build files), M1/M2 (mechanical renames + registries + datafixer aliases + JSG.java entrypoint),
M4 (capabilities: JSGCapabilityRegistration with instance-checked providers for all BE types;
JUB as BlockCapability; item-stack caps nullable) are committed. Misc: tick events split,
EventHooks, gui layers, BufHelper/ItemNBT.stackOf/saveStack in core for packet+NBT ItemStack io.
REMAINING (~628 errors, regenerate worklist with the python snippet in scratchpad usage below):
- stargate teleport: ITeleporter/PortalInfo -> DimensionTransition rework (biggest semantic piece,
  StargateTeleporter + travelers + IStargateTeleporter)
- per-file long tail in common/stargate managers, common/block, client/screen+renderer (same
  recipes as core: vertex API leftovers, blit, widgets), datagen providers (same as core's),
  common/recipes (CraftingInput/MapCodec for 2 custom recipes), JSG.java leftovers
- M7 worldgen injectors, M8 data/ sweep (same script as core C9), M9 integrations wiring
  (CCDevices/OCDevices load hooks; JEI plugin), M10 runtime gates.
Worklist regen: compile with `flatpak-spawn --host ./gradlew compileJava`, then the
python error+source lister used throughout (see git history of this file / scratchpad).

### CORE DONE (2026-07-10): jsg-core builds on NeoForge 1.21.1
`Mod-JSGCore-1.21.x/build/libs/jsg-core-1.21.1-1.0.0.0-Dev.jar` — `flatpak-spawn --host ./gradlew build` green.
All checkpoints C0-C9 committed (see git log in Mod-JSGCore-1.21.x). Additional key decisions beyond the plan:
- ItemStack NBT kept in minecraft:custom_data via `ItemNBT` util (returns copies; write back with setTag).
- Compat shims preserve 1.20.1 API shapes for the mod: `RegistryObject`, `JSGDeferredRegister`,
  `PacketContext`, `NetworkDirection` (core-owned), `TargetPoint` — mod migration should sed imports to
  `dev.tauri.jsg.core.common.registry.*` / `dev.tauri.jsg.core.common.packet.*`.
- Menu screens: bindScreenToMenu now only legal inside guiRegister runnable (runs during RegisterMenuScreensEvent).
- CC:T peripherals: CCIntegrationWrapper.registerPeripheralBE(event, beType) (PeripheralCapability); mod M4/M9
  must call it in its RegisterCapabilitiesEvent handler for stargate/printer BE types.
- CoreCapabilities auto-registers EnergyItem/JSGBucketItem item caps from the item registry (covers mod items);
  fluid BE types enqueue via CoreCapabilities.registerFluidHandlerBE.
- MissingMappingsEvent -> DeferredRegister.addAlias during construction (JSGDataFixers in mod: same pattern).
- Datapack: folder renames (recipes->recipe etc, tags singular), forge:->c:, result.item->result.id,
  forge/biome_modifier->neoforge/biome_modifier — same sweep needed for the mod's data/ in M8.
- Known deferred items: webp4j needs jarJar embedding before shipping; jukebox_song JSONs for music discs;
  runtime verification of AT'd jigsaw worldgen; runClient/runServer gates pending (M10).

### Session-2 progress notes (superseded)
C1..C5 committed in Mod-JSGCore-1.21.x (see git log there). Compile errors: initial wall -> 1120.
Key decisions taken:
- RegistryObject/JSGDeferredRegister/PacketContext/NetworkDirection/TargetPoint compat shims
  under dev.tauri.jsg.core.common.registry / .packet keep 1.20.1 call sites source-compatible.
- ItemStack NBT kept as CompoundTag inside minecraft:custom_data via ItemNBT util
  (returns copies; write-back via setTag — all sites audited). Old saves stay readable.
- CoreCapabilities central RegisterCapabilitiesEvent registrar (EnergyItem/JSGBucketItem
  auto-discovered from item registry; fluid BE types via registerFluidHandlerBE queue).
- INBTSerializable: provider-bridge default/overload methods; legacy signatures kept.
- Tooltip chain switched to Item.TooltipContext (TooltipApplier/HoverConsumer changed).
- Decompiled MC sources for API ground truth: gradle cache neoformruntime decompile_*.jar
  (extracted at scratchpad/mc-src); NeoForge sources at scratchpad/nf-src.
- AT descriptors verified correct against decompiled JigsawPlacement.
Remaining error buckets: client/renderer 282 (vertex API), client/screen 204, advancements
(datagen 148 + common 20), integration 102 (CC/OC/JEI), worldgen 46, config 24, loot 24+10,
recipes provider 22, datafixer 12, misc.

### BLOCKED / next immediate step
- **`Mod-JSGCore-1.21.x/src/main/resources/META-INF/accesstransformer.cfg` is missing** (old SRG version deleted;
  writes of the replacement were declined). The user will create it manually. Content:

```
public net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool templates
public-f net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool rawTemplates
public net.minecraft.server.MinecraftServer nextTickTimeNanos

public net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement getRandomNamedJigsaw(Lnet/minecraft/world/level/levelgen/structure/pools/StructurePoolElement;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/world/level/levelgen/WorldgenRandom;)Ljava/util/Optional;
public net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement addPieces(Lnet/minecraft/world/level/levelgen/RandomState;IZLnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;Ljava/util/List;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)V
```

  The two method descriptors are best-effort: private `addPieces` gained `PoolAliasLookup` (1.20.3) +
  `LiquidSettings` (1.21) params — **verify against decompiled 1.21.1 sources after the first Gradle run**
  and fix. AT users in core: `JSGJigsawPlacement`, `FixedRotationStructure`, `VoidDimensionStructure`,
  `JigsawExtraStructure`, `TemplatePoolInjector`, `AccessUtil`, `LinkingHelper`.
  ⚠ `nextTickTime` (ms, `Util.getMillis()`) → `nextTickTimeNanos` (ns): fix `AccessUtil.java:10` and
  `LinkingHelper.java:69` to use `Util.getNanos()` during C2.

- After the AT exists: run `flatpak-spawn --host ./gradlew compileJava` in `Mod-JSGCore-1.21.x/` —
  first gate; its error list is the C2/C3 worklist. Commit as C1.

## Phase plan (checkpoints = commits)

### Core (`Mod-JSGCore-1.21.x`, branch `main`)
- **C1** build files ✅ (pending AT + first compile attempt)
- **C2** mechanical renames — key table:
  `net.minecraftforge.api.distmarker` → `net.neoforged.api.distmarker`; `eventbus.api` → `net.neoforged.bus.api`;
  `Bus.FORGE` → `Bus.GAME`; `MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`;
  `@Mod.EventBusSubscriber` → `@EventBusSubscriber` (net.neoforged.fml.common);
  `RegistryObject<T>` → `DeferredHolder<R,T>` / `DeferredBlock` / `DeferredItem`;
  `ForgeRegistries.*` → `BuiltInRegistries.*`; `ForgeConfigSpec` → `ModConfigSpec`;
  `ToolAction(s)` → `ItemAbility`/`ItemAbilities`; energy/items/fluids packages → `net.neoforged.neoforge.…`;
  `ForgeFlowingFluid` → `BaseFlowingFluid`; `IForgeMenuType` → `IMenuTypeExtension`;
  `NetworkHooks.openScreen` → `player.openMenu`; `RegisterGuiOverlaysEvent` → `RegisterGuiLayersEvent`;
  `ConfigScreenHandler` → `IConfigScreenFactory`; `IClientItemExtensions` registration → `RegisterClientExtensionsEvent`;
  `DistExecutor` → `FMLEnvironment.dist.isClient()` + client-only hook classes;
  `new ResourceLocation(...)` → `ResourceLocation.fromNamespaceAndPath/parse` (mostly inside core's `JSGMapping.rl` — fix once);
  `forge:` tags → `c:` namespace (Java + JSON dirs `data/forge` → `data/c`).
- **C3** RegistryHelper: return `DeferredHolder`, keep method names; custom registries → NeoForge
  `RegistryBuilder` + `NewRegistryEvent`; no `onBake` → add post-freeze hook fired on `FMLCommonSetupEvent`
  (mod's `UniverseDialerMode.calculateModesSequence()` needs it).
- **C4** SimplePacketHandler v2 (highest-leverage): `JSGPacket implements CustomPacketPayload`;
  keep `registerPacketToServer/ToClient(Class)` reflective-ctor API; buffer type → `RegistryFriendlyByteBuf`;
  `handle(IPayloadContext)` replaces `NetworkEvent.Context`; registration deferred into one
  `RegisterPayloadHandlersEvent` listener (`event.registrar(version)`, `playToServer/ToClient`,
  `StreamCodec.of`); payloads now handled on **main thread** by default → audit `enqueueWork`;
  `PacketDistributor.TargetPoint` is gone → provide core `TargetPoint(level,pos,radius)` record mapping to
  `PacketDistributor.sendToPlayersNear` (≈10 mod call sites).
- **C5** capabilities: drop `CapabilityToken`/`@AutoRegisterCapability`/`LazyOptional`;
  custom cap → `BlockCapability.createSided`; `getCapability` overrides → one `RegisterCapabilitiesEvent`
  registrar; `level.invalidateCapabilities(pos)` replaces LazyOptional invalidation; `BlockCapabilityCache`
  for per-tick energy lookups; `FluidHandlerItemStack` is DataComponent-backed (`SimpleFluidContent`).
- **C6** config (`ModConfigSpec`, register via `ModContainer.registerConfig`), events, DistExecutor removal,
  datafixers: `MissingMappingsEvent` removed → NeoForge registry aliases (verify exact 21.1 API); accept residual loss.
- **C7** client framework: widgets/screens mostly compile; audit `blit(` sites for gui-atlas `blitSprite`;
  overlays → `RegisterGuiLayersEvent` + `LayeredDraw.Layer`.
- **C8** integrations plumbing: CC:T peripherals now exposed via `PeripheralCapability` in
  `RegisterCapabilitiesEvent`; JEI 19 fixes; OC2 already excluded.
- **C9** data: `data/forge` → `data/c`, `forge:conditions` → `neoforge:conditions`; gate =
  `flatpak-spawn --host ./gradlew build` green → jar at stable path.

### Mod (this repo, branch `1.21.1`)
- **M0** branch; build files (mirror core's; deps table above); `implementation files("Mod-JSGCore-1.21.x/build/libs/jsg-core-1.21.1-1.0.0.0-Dev.jar")`;
  drop `server/` subproject from settings.gradle (restore later as a resource-excluding `serverJar` task);
  mods.toml → neoforge.mods.toml (dep `jsg_core` `type="required"`); same AT translation as core; delete pack.mcmeta.
- **M1** mechanical renames + `JSG.java` entrypoint: ctor `JSG(IEventBus modEventBus, ModContainer container)`;
  drop `FMLJavaModLoadingContext`; `BuildCreativeModeTabContentsEvent` is MOD-bus; rewire 16 `@EventBusSubscriber` classes.
- **M2** registries (`common/registry/` 18 classes + `api/registry/` 7): `DeferredHolder` field types;
  `JSGRegistries.java` custom registries (`stargate_type`, `universe_dialer_modes`) + `rig_waves` stays
  datapack registry (`DataPackRegistryEvent.NewRegistry`); villager trades event moves.
- **M3** networking: 24 packet classes under `common/packet/packets/**` (ctor `RegistryFriendlyByteBuf`,
  `handle(IPayloadContext)`); `NetworkHooks.openScreen` (1 site, `StargateClassicBaseBlock`) → `openMenu`.
- **M4** capabilities: new `JSGCapabilityRegistration`; delete 18 `getCapability` overrides;
  energy → `Capabilities.EnergyStorage.BLOCK` (+cache) in `PowerUtils`/`StargateEnergyManager` etc. (21 files);
  `DHDFluidHandlerItemStack` → component-based.
- **M5** ItemStack NBT → DataComponents (~63 sites: universe dialer, GDO, notebook/page, admin controller,
  iris code, linked positions) via new `JSGDataComponents` (`DeferredRegister.DataComponents`, Codec+StreamCodec each).
  **World-breaking for 1.20.1 saves — documented, no custom DFU.** Also the 2 custom recipes →
  `CraftingRecipe` with `CraftingInput`, `MapCodec` serializers.
- **M6** client: 49 renderers + 28 screens; blitSprite audit; overlays → GUI layers; 8 BEWLR items →
  `RegisterClientExtensionsEvent`; `VertexConsumer` renames (`vertex` → `addVertex`); custom particles per compile errors.
- **M7** worldgen: `JSGTemplatePoolInjectors` (AT'd `templates`/`rawTemplates` fields), jigsaw callers pass
  `PoolAliasLookup.EMPTY` + default `LiquidSettings`; `LootTableLoadEvent` still exists (keys `ResourceKey<LootTable>`).
- **M8** datagen: providers take `(PackOutput, CompletableFuture<HolderLookup.Provider>)`; regenerate + diff.
- **M9** integrations: CC (via core PeripheralCapability), JEI 19 plugin, Create 6/StellarView drift fixes;
  OC2 excluded behind `enable_oc2`; Tinkers dep removed, `data/tconstruct` kept (inert).
- **M10** runtime gates:
  1. `runData` output sane; 2. `runClient` smoke — world → place gate+DHD → GUIs → dial → walk through →
  dialer GUI → CC `peripheral.find` + method → config screen → JEI; 3. `runServer` reaches "Done"
  (client-class leakage check), then networked dial client↔dedicated server (real payload encoding).

## Top risks
1. SimplePacketHandler redesign ripples (24 packets, thread-model change) — test on dedicated server.
2. NBT→DataComponents — world-breaking for old saves (accepted).
3. Core widget framework vs 1.21 gui atlas — silent mis-renders; visual pass over every GUI.
4. AT'd JigsawPlacement/StructureTemplatePool changes — verify structures generate in a fresh world.
5. `nextTickTimeNanos` ms→ns — silent 10^6 unit error.
6. Third-party API drift (Create 6, Ponder, StellarView, CC peripheral model).

Full original plan: `/home/tester/.claude/plans/we-want-to-migrate-sparkling-bird.md`
