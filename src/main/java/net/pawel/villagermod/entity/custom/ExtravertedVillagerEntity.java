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
import net.pawel.villagermod.entity.ai.*;
import net.pawel.villagermod.utils.VillagerUtils;
import org.jetbrains.annotations.Nullable;

public class ExtravertedVillagerEntity extends VillagerAbstract {

    public ExtravertedVillagerEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new VillagerAttackGoal(this, 1.0D, true));
        this.goalSelector.add(2, new ExtrovertGroupSeekingGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.25D, Ingredient.ofItems(Items.BEETROOT), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.15D));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 4f));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.goalSelector.add(0, new VillagerPairGoal(this, 1D)); // Ensure unique priority
        this.goalSelector.add(0, new VillagerBreedGoal(this, 1D)); // Ensure unique priority

        this.targetSelector.add(1, (new RevengeGoal(this, VillagerAbstract.class)).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, VindicatorEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        this.setupAnimationStates();
        updateSocialBattery();
    }

    private void updateSocialBattery() {
        int currentCrowdSize = VillagerUtils.countNearbyVillagers(this, PERSONAL_SPACE_RADIUS * 1.5);
        double logisticFactor = 1 / (1 + Math.exp(-0.1 * (socialBattery - 500))); // Logistic function centered at 50

        int change = (int) (logisticFactor * 3); // Scale the change by the logistic factor

        if (currentCrowdSize >= CROWD_THRESHOLD) {
            socialBattery = Math.min(1000, socialBattery + change);
        } else {
            socialBattery = Math.max(0, socialBattery - change);
        }

        previousCrowdSize = currentCrowdSize;
    }

    public static DefaultAttributeContainer.Builder createExtravertVillagerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_ARMOR, 0.5f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 40);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        ExtravertedVillagerEntity customChild = ModEntities.EXTRAVERTED_VILLAGER.create(world);
        return customChild;
    }
}