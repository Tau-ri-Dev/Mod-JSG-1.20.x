package dev.tauri.jsg.api.stargate.network;

import dev.tauri.jsg.api.JSGApi;
import dev.tauri.jsg.api.chunkloader.ChunkManager;
import dev.tauri.jsg.api.helper.DimensionsHelper;
import dev.tauri.jsg.api.stargate.Stargate;
import dev.tauri.jsg.api.stargate.network.address.symbol.types.AbstractSymbolType;
import dev.tauri.jsg.api.stargate.type.StargateType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

public class StargatePos implements INBTSerializable<CompoundTag> {

    public boolean blacklisted = false;
    public ResourceKey<Level> dimension;
    public BlockPos gatePos;
    private AbstractSymbolType<?> gateSymbolType;
    private StargateType stargateType;

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return (name == null ? "" : name);
    }

    public StargatePos(ResourceKey<Level> dimension, BlockPos gatePos, AbstractSymbolType<?> gateSymbolType, StargateType stargateType) {
        this.dimension = dimension;
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

    public AbstractSymbolType<?> getGateSymbolType() {
        if (gateSymbolType != null) return gateSymbolType;
        gateSymbolType = getStargate().getSymbolType();
        return gateSymbolType;
    }

    public StargateType getStargateType() {
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
        compound.putLong("pos", gatePos.asLong());
        compound.putString("stargatePosName", (name == null ? "" : name));
        if (gateSymbolType != null)
            compound.putString("gateSymbolType", gateSymbolType.getId());
        if (stargateType != null)
            compound.putString("stargateTypeSaved", stargateType.getId());

        compound.putBoolean("blacklisted", blacklisted);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("dim")));
        gatePos = BlockPos.of(compound.getLong("pos"));
        name = compound.getString("stargatePosName");
        if (compound.contains("gateSymbolType")) {
            if (compound.contains("gateSymbolType", CompoundTag.TAG_BYTE)) {
                // old format - used byte as id
                gateSymbolType = AbstractSymbolType.byId(compound.getByte("gateSymbolType"));
            } else {
                // new format use string as id
                gateSymbolType = AbstractSymbolType.byId(compound.getString("gateSymbolType"));
            }
        }
        if (compound.contains("stargateTypeSaved")) {
            if (compound.contains("stargateTypeSaved", CompoundTag.TAG_INT)) {
                // old format - used int as id
                stargateType = StargateType.valueOf(compound.getInt("stargateTypeSaved"));
            } else {
                // new format use string as id
                stargateType = StargateType.valueOf(compound.getString("stargateTypeSaved"));
            }
        }
        blacklisted = compound.getBoolean("blacklisted");
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeLong(gatePos.asLong());
        if (name != null) {
            buf.writeBoolean(true);
            buf.writeInt(name.length());
            buf.writeCharSequence(name, StandardCharsets.UTF_8);
        } else
            buf.writeBoolean(false);
        if (gateSymbolType != null) {
            buf.writeBoolean(true);
            buf.writeInt(AbstractSymbolType.getId(gateSymbolType));
        } else
            buf.writeBoolean(false);
        if (stargateType != null) {
            buf.writeBoolean(true);
            buf.writeInt(stargateType.id);
        } else
            buf.writeBoolean(false);
        buf.writeBoolean(blacklisted);
    }

    public void fromBytes(FriendlyByteBuf buf) {
        dimension = buf.readResourceKey(Registries.DIMENSION);
        gatePos = BlockPos.of(buf.readLong());
        if (buf.readBoolean()) {
            int nameSize = buf.readInt();
            name = buf.readCharSequence(nameSize, StandardCharsets.UTF_8).toString();
        }
        if (buf.readBoolean()) {
            gateSymbolType = AbstractSymbolType.byId(buf.readInt());
        }
        if (buf.readBoolean()) {
            stargateType = StargateType.valueOf(buf.readInt());
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StargatePos other = (StargatePos) obj;
        if (dimension != other.dimension)
            return false;
        if (gatePos == null) {
            return other.gatePos == null;
        } else return gatePos.equals(other.gatePos);
    }
}
