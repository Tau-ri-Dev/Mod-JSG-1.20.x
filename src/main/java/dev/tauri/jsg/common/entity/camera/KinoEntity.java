package dev.tauri.jsg.common.entity.camera;

import dev.tauri.jsg.common.registry.JSGPositionedSounds;
import dev.tauri.jsg.core.client.sound.JSGPositionedSound;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KinoEntity extends LivingEntity {
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.FLYING_SPEED, 0.1F)
                .add(Attributes.MOVEMENT_SPEED, 0.1F)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    protected final JSGPositionedSound flyingSound = new JSGPositionedSound(BlockPos.ZERO, JSGPositionedSounds.KINO_FLYBY.event.get(), SoundSource.NEUTRAL, RandomSource.create(), true, 1) {
        private float lastVolume = 0;

        @Override
        public Vec3 getPosition() {
            return KinoEntity.this.position();
        }

        @Override
        public double getX() {
            return KinoEntity.this.position().x;
        }

        @Override
        public double getY() {
            return KinoEntity.this.position().y;
        }

        @Override
        public double getZ() {
            return KinoEntity.this.position().z;
        }

        @Override
        public float getMaxVolume() {
            var volume = (float) (super.getMaxVolume() * Math.min(1, KinoEntity.this.getDeltaMovementSpeed() + 0.05f));
            if (volume - lastVolume > 0.01f) volume = lastVolume + 0.01f;
            lastVolume = volume;
            return volume;
        }
    };

    public KinoEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public float getEyeHeight(Pose pPose) {
        return getBbHeight() / 2f;
    }

    public double getDeltaMovementSpeed() {
        var movement = getDeltaMovement();
        return Math.sqrt(movement.x * movement.x + movement.y * movement.y + movement.z * movement.z);
    }

    @Override
    public void tick() {
        setNoGravity(true);
        super.tick();

        // play flying sound
        if (!level().isClientSide()) return;
        if (isDeadOrDying() || isRemoved()) {
            flyingSound.stopPlaying();
            return;
        }
        if (!flyingSound.isPlaying())
            flyingSound.play();
    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        if (!level().isClientSide()) return;
        flyingSound.stopPlaying();
    }

    @Override
    protected boolean canRide(Entity pVehicle) {
        return false;
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {

    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}
