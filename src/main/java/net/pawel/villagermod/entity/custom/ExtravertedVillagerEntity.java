package net.pawel.villagermod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.ModEntities;
import net.pawel.villagermod.entity.ai.ExtrovertGroupSeekingGoal;
import net.pawel.villagermod.entity.ai.VillagerAttackGoal;
import net.pawel.villagermod.entity.ai.VillagerGroupSeekingGoal;
import net.pawel.villagermod.entity.ai.VillagerPairGoal;
import org.jetbrains.annotations.Nullable;

public class ExtravertedVillagerEntity extends VillagerAbstract {

    public ExtravertedVillagerEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new VillagerAttackGoal(this, 1.0D, true));
        this.goalSelector.add(2, new ExtrovertGroupSeekingGoal(this, 1.0D, 3));
        this.goalSelector.add(3, new TemptGoal(this, 1.25D, Ingredient.ofItems(Items.BEETROOT), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.15D));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 4f));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.goalSelector.add(8, new VillagerPairGoal(this, 2D));
        this.targetSelector.add(1, (new RevengeGoal(this, VillagerAbstract.class)).setGroupRevenge());
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, VindicatorEntity.class, true));
//        this.goalSelector.add(0, new VillagerBreedGoal(this, 1.0D));
    }

    @Override
    public void tick() {
        super.tick();
        this.setupAnimationStates();
    }

    public static DefaultAttributeContainer.Builder createExtravertVillagerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_ARMOR, 0.5f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 50);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        ExtravertedVillagerEntity customChild = ModEntities.EXTRAVERTED_VILLAGER.create(world);
        return customChild;
    }
}