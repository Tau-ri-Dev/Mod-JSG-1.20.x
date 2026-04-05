package dev.tauri.jsg.stargate.rig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tauri.jsg.JSG;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.util.ParameterReplacer;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.registries.ForgeRegistries;

public class RIGEntity {
    public static final Codec<RIGEntity> CODEC = RecordCodecBuilder.create(rigEntityInstance -> rigEntityInstance.group(
            Codec.STRING.fieldOf("name").forGetter((rigEntity) -> rigEntity.name),
            Codec.INT.fieldOf("weight").forGetter((rigEntity) -> rigEntity.weight),
            CompoundTag.CODEC.fieldOf("nbt").forGetter((rigEntity) -> rigEntity.nbt)
    ).apply(rigEntityInstance, RIGEntity::new));
    public String name;
    public int weight;
    public CompoundTag nbt = new CompoundTag();

    public RIGEntity(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public RIGEntity(String name, int weight, String nbt) {
        this(name, weight);
        try {
            this.nbt = TagParser.parseTag(nbt);
        } catch (Exception e) {
            JSGCore.logger.error("Error while creating RIGEntity {}:", name, e);
        }
    }

    public RIGEntity(String name, int weight, CompoundTag nbt) {
        this(name, weight);
        this.nbt = nbt;
    }

    public Entity get(ServerLevel level, ParameterReplacer replacer) {
        Entity entity = null;
        var rlString = JSGMapping.rl(replacer.apply(name));
        try {
            var type = ForgeRegistries.ENTITY_TYPES.getValue(rlString);
            if (type != null) {
                CompoundTag compoundtag = nbt.copy();
                compoundtag.putString("id", replacer.apply(name));
                entity = EntityType.loadEntityRecursive(compoundtag, level, e -> e);
            }
        } catch (Exception e) {
            JSG.logger.error("Error while parsing entity from RIG config! Entity in config: {}", replacer.apply(name));
            JSG.logger.error("Stacktrace:", e);
        }
        if (entity == null)
            entity = new Zombie(level);
        return entity;
    }
}
