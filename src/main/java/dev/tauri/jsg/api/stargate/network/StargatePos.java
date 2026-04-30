package dev.tauri.jsg.api.stargate.network;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.core.common.chunkloader.ChunkManager;
import dev.tauri.jsg.core.common.helper.DimensionsHelper;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.mapping.JSGMapping;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Objects;
import java.util.function.Consumer;

public class StargatePos implements INBTSerializable<CompoundTag> {
    public boolean blacklisted = false;
    public ResourceKey<Level> dimension;
    public ResourceKey<Level> fakeGateDimension;
    public BlockPos gatePos;
    public BlockPos fakeGatePos;
    private SymbolType<?> gateSymbolType;
    private StargateType<?> stargateType;

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return (name == null ? "" : name);
    }

    public StargatePos(ResourceKey<Level> dimension, BlockPos gatePos, SymbolType<?> gateSymbolType, StargateType<?> stargateType) {
        this(dimension, dimension, gatePos, gatePos, gateSymbolType, stargateType);
    }

    public StargatePos(ResourceKey<Level> dimension, ResourceKey<Level> fakeGateDimension, BlockPos gatePos, BlockPos fakeGatePos, SymbolType<?> gateSymbolType, StargateType<?> stargateType) {
        this.dimension = dimension;
        this.fakeGateDimension = fakeGateDimension;
        this.fakeGatePos = fakeGatePos;
        this.gatePos = gatePos;
        this.gateSymbolType = gateSymbolType;
        this.stargateType = stargateType;
    }

    public StargatePos(CompoundTag compound) {
        deserializeNBT(compound);
    }

    public StargatePos(ByteBuf buf) {
        fromBytes(new FriendlyByteBuf(buf));
    }

    public SymbolType<?> getGateSymbolType() {
        if (gateSymbolType != null) return gateSymbolType;
        gateSymbolType = getStargate().getSymbolType();
        return gateSymbolType;
    }

    public StargateType<?> getStargateType() {
        if (stargateType != null) return stargateType;
        stargateType = StargateType.parse(getGateSymbolType());
        return stargateType;
    }

    public Level getWorld() {
        return Objects.requireNonNull(DimensionsHelper.getLevel(dimension));
    }

    protected void forceChunk() {
        ChunkManager.forceChunk((ServerLevel) getWorld(), new ChunkPos(gatePos));
    }

    protected void unforceChunk() {
        ChunkManager.unforceChunk((ServerLevel) getWorld(), new ChunkPos(gatePos));
    }

    public void forceChunkAndRun(Runnable runnable) {
        forceChunk();
        runnable.run();
        unforceChunk();
    }

    public void forceChunkAndCall(Consumer<Stargate<?>> consumer) {
        forceChunk();
        consumer.accept(getStargate());
        unforceChunk();
    }

    public Stargate<?> getStargate() {
        try {
            BlockEntity tile = getWorld().getBlockEntity(gatePos);
            if (tile == null) {
                forceChunk();
                tile = getWorld().getBlockEntity(gatePos);
                unforceChunk();
            }
            return (Stargate<?>) tile;
        } catch (Exception e) {
            JSGApi.logger.error("Error while getting tile entity from SG pos!", e);
            return null;
        }
    }

    public BlockState getBlockState() {
        return getWorld().getBlockState(gatePos);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();

        compound.putString("dim", dimension.location().toString());
        compound.putString("fakeDim", fakeGateDimension.location().toString());
        compound.putLong("pos", gatePos.asLong());
        compound.putLong("fakePos", fakeGatePos.asLong());
        compound.putString("stargatePosName", (name == null ? "" : name));
        if (gateSymbolType != null)
            compound.putString("gateSymbolType", gateSymbolType.getId().toString());
        if (stargateType != null)
            compound.putString("stargateTypeSaved", stargateType.getId().toString());

        compound.putBoolean("blacklisted", blacklisted);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        dimension = ResourceKey.create(Registries.DIMENSION, JSGMapping.rl(compound.getString("dim")));
        if (compound.contains("fakeDim"))
            fakeGateDimension = ResourceKey.create(Registries.DIMENSION, JSGMapping.rl(compound.getString("fakeDim")));
        else
            fakeGateDimension = dimension;

        gatePos = BlockPos.of(compound.getLong("pos"));
        if (compound.contains("fakePos"))
            fakeGatePos = BlockPos.of(compound.getLong("fakePos"));
        else
            fakeGatePos = gatePos;

        name = compound.getString("stargatePosName");
        if (compound.contains("gateSymbolType")) {
            gateSymbolType = SymbolType.byId(JSGMapping.rl(compound.getString("gateSymbolType")));
        }
        if (compound.contains("stargateTypeSaved")) {
            stargateType = StargateType.valueOf(JSGMapping.rl(compound.getString("stargateTypeSaved")));
        }
        blacklisted = compound.getBoolean("blacklisted");
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeResourceKey(fakeGateDimension);
        buf.writeLong(gatePos.asLong());
        buf.writeLong(fakeGatePos.asLong());
        if (name != null) {
            buf.writeBoolean(true);
            buf.writeUtf(name);
        } else
            buf.writeBoolean(false);
        if (gateSymbolType != null) {
            buf.writeBoolean(true);
            buf.writeResourceLocation(gateSymbolType.getId());
        } else
            buf.writeBoolean(false);
        if (stargateType != null) {
            buf.writeBoolean(true);
            buf.writeResourceLocation(stargateType.getId());
        } else
            buf.writeBoolean(false);
        buf.writeBoolean(blacklisted);
    }

    public void fromBytes(FriendlyByteBuf buf) {
        dimension = buf.readResourceKey(Registries.DIMENSION);
        fakeGateDimension = buf.readResourceKey(Registries.DIMENSION);
        gatePos = BlockPos.of(buf.readLong());
        fakeGatePos = BlockPos.of(buf.readLong());
        if (buf.readBoolean()) {
            name = buf.readUtf();
        }
        if (buf.readBoolean()) {
            gateSymbolType = SymbolType.byId(buf.readResourceLocation());
        }
        if (buf.readBoolean()) {
            stargateType = StargateType.valueOf(buf.readResourceLocation());
        }
        blacklisted = buf.readBoolean();
    }


    // ---------------------------------------------------------------------------------------------------
    // Hashing

    @Override
    public String toString() {
        return String.format("[dim=%s, pos=%s, name=%s]", dimension.location(), gatePos.toString(), getName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dimension.hashCode();
        result = prime * result + ((gatePos == null) ? 0 : gatePos.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (!(obj instanceof StargatePos other)) return false;
        if (dimension != other.dimension)
            return false;
        if (gatePos == null) {
            return other.gatePos == null;
        } else return gatePos.equals(other.gatePos);
    }
}
