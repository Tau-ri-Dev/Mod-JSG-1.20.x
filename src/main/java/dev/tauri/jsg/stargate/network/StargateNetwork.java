package dev.tauri.jsg.stargate.network;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.stargate.network.IStargateNetwork;
import dev.tauri.jsg.api.stargate.network.StargatePos;
import dev.tauri.jsg.api.stargate.network.address.StargateAddress;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.helper.DimensionsHelper;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.mapping.JSGMapping;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class StargateNetwork extends SavedData implements IStargateNetwork {

    public static StargateNetwork INSTANCE = new StargateNetwork();

    public static final String DATA_NAME = JSG.MOD_ID + "_stargates";


    public void register(@Nonnull DimensionDataStorage storage) {
        INSTANCE = this;
        storage.computeIfAbsent(INSTANCE::load, () -> INSTANCE, DATA_NAME);
    }

    public StargateNetwork() {
    }

    private final Map<StargatePos, Map<SymbolType<?>, StargateAddress>> GATES_MAP_BY_POS = new LinkedHashMap<>();
    private final Map<StargateAddress, StargatePos> GATES_MAP_BY_ADDRESS = new LinkedHashMap<>();

    public final List<ResourceLocation> generatedSpecialStructures = new ArrayList<>();

    public Map<StargatePos, Map<SymbolType<?>, StargateAddress>> getAll() {
        return GATES_MAP_BY_POS;
    }

    @Nonnull
    public Map<StargatePos, Map<SymbolType<?>, StargateAddress>> getStargatesByDimension(ResourceKey<Level> dimension) {
        var map = new HashMap<StargatePos, Map<SymbolType<?>, StargateAddress>>();
        for (var e : GATES_MAP_BY_POS.entrySet()) {
            if (e.getKey().dimension == dimension) {
                map.put(e.getKey(), e.getValue());
            }
        }
        return map;
    }

    @Nonnull
    public Optional<Map.Entry<StargatePos, Map<SymbolType<?>, StargateAddress>>> getStargateByDimension(ResourceKey<Level> dimension) {
        for (var e : GATES_MAP_BY_POS.entrySet()) {
            if (e.getKey().dimension == dimension) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    @Nullable
    public StargatePos getStargate(StargateAddress address) {
        if (address == null)
            return null;
        if (address.getSize() < 7)
            return null;

        StargatePos pos = GATES_MAP_BY_ADDRESS.get(address);
        if (pos == null) return null;
        if (pos.getWorld() == null) return null;

        // check 7th and 8th symbol
        var gateAddress = GATES_MAP_BY_POS.get(pos);
        if (gateAddress == null || !Objects.equals(gateAddress.get(address.getSymbolType()), address))
            return null;

        return pos;
    }

    @Nullable
    public Map<SymbolType<?>, StargateAddress> getAddresses(StargatePos pos) {
        if (pos == null) return null;
        return new HashMap<>(GATES_MAP_BY_POS.get(pos));
    }

    @Nullable
    public Pair<StargatePos, StargateAddress> getRandomAddress(RandomSource random, SymbolType<?> symbolTypeEnum, @Nullable StargateType<?> stargateType, @Nullable Predicate<StargatePos> customTest) {
        var size = GATES_MAP_BY_POS.size();
        if (size == 0) return null;
        var allowedList = new ArrayList<Pair<StargatePos, StargateAddress>>();
        for (var entry : GATES_MAP_BY_POS.entrySet()) {
            if (stargateType != null && stargateType != entry.getKey().getStargateType()) continue;
            var addresses = entry.getValue();
            if (!addresses.containsKey(symbolTypeEnum)) continue;
            var address = addresses.get(symbolTypeEnum);
            if (address == null) continue;
            if (customTest != null && !customTest.test(entry.getKey())) continue;
            allowedList.add(Pair.of(entry.getKey(), address));
        }
        if (allowedList.isEmpty()) return null;
        var rand = random.nextInt(allowedList.size());
        return allowedList.get(rand);
    }

    public void putStargate(StargateAddress address, StargatePos stargatePos) {
        var map = new HashMap<SymbolType<?>, StargateAddress>();
        map.put(address.getSymbolType(), address);
        putStargate(map, stargatePos);
    }

    public void putStargate(Map<SymbolType<?>, StargateAddress> addressMap, StargatePos stargatePos) {
        if (addressMap == null) {
            JSG.logger.warn("Tried to add NULL-address gate! Aborting...", new NullPointerException());
            return;
        }
        //JSG.logger.info("Adding stargate's addresses at " + stargatePos + " into the network!");

        var map = GATES_MAP_BY_POS.get(stargatePos);
        if (map == null)
            GATES_MAP_BY_POS.put(stargatePos, new HashMap<>(addressMap));
        else {
            map.putAll(addressMap);
            GATES_MAP_BY_POS.put(stargatePos, map);
        }
        for (var address : addressMap.values()) {
            GATES_MAP_BY_ADDRESS.put(address, stargatePos);
        }
        checkForInvalidDims();
        setDirty();
    }

    public void removeStargate(StargatePos stargatePos) {
        if (stargatePos == null) return;
        JSG.logger.debug("Removing stargate's addresses at {} from the network!", stargatePos);

        GATES_MAP_BY_POS.remove(stargatePos);

        var del = new ArrayList<StargateAddress>();
        for (var e : GATES_MAP_BY_ADDRESS.entrySet()) {
            if (e.getValue() == stargatePos) {
                del.add(e.getKey());
            }
        }

        for (var a : del) {
            GATES_MAP_BY_ADDRESS.remove(a);
        }

        setDirty();
    }

    private void checkForInvalidDims() {
        if (JSGCore.currentServer == null) return; // we are on client - do not check
        var map = new HashMap<>(GATES_MAP_BY_POS);
        for (var e : map.entrySet()) {
            var pos = e.getKey();
            if (DimensionsHelper.getLevel(pos.dimension) == null) {
                JSG.logger.warn("Removing stargate at {} from the network as dim is INVALID!", pos);
                removeStargate(pos);
            }
        }
    }

    public void renameStargate(StargatePos pos, String newName) {
        var map = getAddresses(pos);
        removeStargate(pos);
        JSG.logger.debug("Setting gate's name at {} to: {}", pos, newName);
        pos.setName(newName);
        putStargate(map, pos);
        setDirty();
    }

    // ---------------------------------------------------------------------------------------------------------
    // Reading and writing

    public StargateNetwork load(CompoundTag compound) {
        // create new - clear old data
        INSTANCE.fromNBT(compound);
        return INSTANCE;
    }

    public void fromNBT(CompoundTag compound) {
        GATES_MAP_BY_POS.clear();
        GATES_MAP_BY_ADDRESS.clear();
        ListTag stargateTagList = compound.getList("stargates", Tag.TAG_COMPOUND);

        if (compound.contains("version") && compound.getString("version").equalsIgnoreCase("2.0")) {
            for (Tag stargateTag : stargateTagList) {
                CompoundTag stargateCompound = (CompoundTag) stargateTag;

                StargatePos stargatePos = new StargatePos(stargateCompound.getCompound("pos"));
                var tagMap = stargateCompound.getList("addressMap", Tag.TAG_COMPOUND);
                for (var addressTag : tagMap) {
                    putStargate(new StargateAddress((CompoundTag) addressTag), stargatePos);
                }
            }
        } else {
            // load OLD network
            StargatePos stargatePos = null;
            for (Tag baseTag : stargateTagList) {
                CompoundTag stargateCompound = (CompoundTag) baseTag;

                StargateAddress stargateAddress = new StargateAddress(stargateCompound.getCompound("address"));
                if (stargatePos == null)
                    stargatePos = new StargatePos(stargateCompound.getCompound("pos"));

                putStargate(stargateAddress, stargatePos);
            }
        }

        generatedSpecialStructures.clear();
        var structuresSize = compound.getInt("specialStructureGenerated_size");
        for (int i = 0; i < structuresSize; i++) {
            generatedSpecialStructures.add(JSGMapping.rl(compound.getString("specialStructureGenerated_" + i)));
        }
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        JSG.logger.info("Saving SGN: Started");
        compound.putString("version", "2.0");
        ListTag stargateTagList = new ListTag();

        for (var stargate : GATES_MAP_BY_POS.entrySet()) {
            var sgTag = new CompoundTag();
            sgTag.put("pos", stargate.getKey().serializeNBT());
            var mapList = new ListTag();
            for (var address : stargate.getValue().entrySet()) {
                mapList.add(address.getValue().serializeNBT());
            }
            sgTag.put("addressMap", mapList);
            stargateTagList.add(sgTag);
        }
        compound.put("stargates", stargateTagList);

        var structuresSize = generatedSpecialStructures.size();
        compound.putInt("specialStructureGenerated_size", structuresSize);
        for (int i = 0; i < structuresSize; i++) {
            compound.putString("specialStructureGenerated_" + i, generatedSpecialStructures.get(i).toString());
        }
        JSG.logger.info("Saving SGN: Done");

        return compound;
    }


    public void toBytes(ByteBuf buff) {
        FriendlyByteBuf buf = new FriendlyByteBuf(buff);
        // Write addresses
        buf.writeInt(GATES_MAP_BY_POS.size());
        for (var stargate : GATES_MAP_BY_POS.entrySet()) {
            stargate.getKey().toBytes(buf);
            buf.writeInt(stargate.getValue().size());
            for (var address : stargate.getValue().values()) {
                address.toBytes(buf);
            }
        }
    }

    public void fromBytes(ByteBuf buf) {
        // Read addresses
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            var pos = new StargatePos(buf);
            var addSize = buf.readInt();
            for (int j = 0; j < addSize; j++) {
                putStargate(new StargateAddress(buf), pos);
            }
        }
    }
}
