package dev.tauri.jsg.entity.animal;

import dev.tauri.jsg.registry.JSGEntities;
import dev.tauri.jsg.registry.JSGItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public class MastadgeEntity extends Camel {
    public MastadgeEntity(EntityType<? extends Camel> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new BreedGoal(this, 1.15D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, Ingredient.of(JSGItems.FOOD_CHOCOLATE_BAR.get()), false));

        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));

        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 3f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.ARMOR_TOUGHNESS, 0.1f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5f)
                .add(Attributes.ATTACK_DAMAGE, 2f)
                .add(Attributes.JUMP_STRENGTH, 0.42f);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (hasPassenger(passenger)) {
            double y = getY() + getPassengersRidingOffset() + passenger.getMyRidingOffset() + 0.5f;
            moveFunction.accept(passenger, getX(), y, getZ());
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void doPlayerRide(Player player) {
        if (isSaddled())
            super.doPlayerRide(player);
    }


    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return pPose == Pose.SITTING ? EntityDimensions.scalable(JSGEntities.MASTADGE.get().getWidth(), JSGEntities.MASTADGE.get().getHeight() - 1.43F).scale(this.getScale()) : super.getDimensions(pPose);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public MastadgeEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return JSGEntities.MASTADGE.get().create(pLevel);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(JSGItems.FOOD_CHOCOLATE_BAR.get());
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HOGLIN_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.DOLPHIN_DEATH;
    }
}
