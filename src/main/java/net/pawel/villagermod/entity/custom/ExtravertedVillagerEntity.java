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
import net.pawel.villagermod.entity.ai.ExtrovertGroupSeekingGoal;
import net.pawel.villagermod.entity.ai.VillagerAvoidCrowdGoal;
import net.pawel.villagermod.entity.ai.VillagerGroupSeekingGoal;
import net.pawel.villagermod.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

public class ExtravertedVillagerEntity extends VillagerAbstract {
    public ExtravertedVillagerEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new ExtrovertGroupSeekingGoal(this, 1.0D,3));
        this.goalSelector.add(3, new TemptGoal(this, 1.25D, Ingredient.ofItems(Items.BEETROOT), false));

        this.goalSelector.add(4, new FollowParentGoal(this, 1.15D));

        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 4f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
    }

    public static DefaultAttributeContainer.Builder createDiamondVillagerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_ARMOR, 0.5f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2);
    }

    //TODO create custom child for each villager type
    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        ExtravertedVillagerEntity customChild = ModEntities.DIAMOND_VILLAGER.create(world);
        return customChild;
    }
}
