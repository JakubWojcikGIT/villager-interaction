package net.pawel.villagermod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.ai.IntrovertGroupSeekingGoal;
import net.pawel.villagermod.entity.ai.VillagerAvoidCrowdGoal;
import net.pawel.villagermod.entity.ai.VillagerGroupSeekingGoal;
import net.pawel.villagermod.entity.ModEntities;
import net.pawel.villagermod.utils.VillagerUtils;
import org.jetbrains.annotations.Nullable;


public class IntrovertedVillagerEntity extends VillagerAbstract {

    public IntrovertedVillagerEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new VillagerAvoidCrowdGoal(this, 1.0D, 3, 3, 40));
        this.goalSelector.add(2, new IntrovertGroupSeekingGoal(this, 1.0D, 3, 3));
        this.goalSelector.add(3, new TemptGoal(this, 1.25D, Ingredient.ofItems(Items.BEETROOT), false));

        this.goalSelector.add(4, new FollowParentGoal(this, 1.15D));

        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 4f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
    }

    public static DefaultAttributeContainer.Builder createWoodenVillagerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_ARMOR, 0.5f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            super.setupAnimationStates();
        }
//        updateStressLevel();
    }

    private void updateStressLevel() {
        if (VillagerUtils.isCrowded(this, this.getEntityWorld(), this.PERSONAL_SPACE_RADIUS, this.CROWD_THRESHOLD)) {
            this.stressLevel++;
        } else if (this.stressLevel > 0) {
            this.stressLevel--;
        }
        System.out.println("Stress level (Introvert): " + this.stressLevel);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.WOODEN_VILLAGER.create(world);
    }
}
