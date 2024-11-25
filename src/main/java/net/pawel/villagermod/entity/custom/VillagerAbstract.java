package net.pawel.villagermod.entity.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.ModEntities;
import net.pawel.villagermod.utils.VillagerUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class VillagerAbstract extends AnimalEntity {
    private static final Random random = new Random();
    final public static int PERSONAL_SPACE_RADIUS = 3;
    final public static int CROWD_THRESHOLD = 3;
    public static final Map<VillagerAbstract, VillagerAbstract> pairs = new HashMap<>();
    private VillagerAbstract mate;
    private int breedCooldown = 0;
    protected int socialBattery = 500;
    protected int previousCrowdSize = 0;


    private Trait aggressionTrait;
    private Trait agilityTrait;
    private Trait resilienceTrait;
    private Trait strengthTrait;

    public Trait getResilienceTrait() {return resilienceTrait;}
    public Trait getStrengthTrait() {return strengthTrait;}
    public Trait getAggressionTrait() {return aggressionTrait;}
    public Trait getAgilityTrait() {return agilityTrait;}




    public enum Trait {
        PEACEFUL, TANKY, AGRESSIVE, AGILE,
        HARDY, FRAGILE, STRONG, SWIFT
    }

    public enum TraitSet {
        PEACEFUL_TANKY_HARDY_STRONG(Trait.PEACEFUL, Trait.TANKY, Trait.HARDY, Trait.STRONG),
        PEACEFUL_TANKY_HARDY_SWIFT(Trait.PEACEFUL, Trait.TANKY, Trait.HARDY, Trait.SWIFT),
        PEACEFUL_TANKY_FRAGILE_STRONG(Trait.PEACEFUL, Trait.TANKY, Trait.FRAGILE, Trait.STRONG),
        PEACEFUL_TANKY_FRAGILE_SWIFT(Trait.PEACEFUL, Trait.TANKY, Trait.FRAGILE, Trait.SWIFT),
        PEACEFUL_AGILE_HARDY_STRONG(Trait.PEACEFUL, Trait.AGILE, Trait.HARDY, Trait.STRONG),
        PEACEFUL_AGILE_HARDY_SWIFT(Trait.PEACEFUL, Trait.AGILE, Trait.HARDY, Trait.SWIFT),
        PEACEFUL_AGILE_FRAGILE_STRONG(Trait.PEACEFUL, Trait.AGILE, Trait.FRAGILE, Trait.STRONG),
        PEACEFUL_AGILE_FRAGILE_SWIFT(Trait.PEACEFUL, Trait.AGILE, Trait.FRAGILE, Trait.SWIFT),
        AGRESSIVE_TANKY_HARDY_STRONG(Trait.AGRESSIVE, Trait.TANKY, Trait.HARDY, Trait.STRONG),
        AGRESSIVE_TANKY_HARDY_SWIFT(Trait.AGRESSIVE, Trait.TANKY, Trait.HARDY, Trait.SWIFT),
        AGRESSIVE_TANKY_FRAGILE_STRONG(Trait.AGRESSIVE, Trait.TANKY, Trait.FRAGILE, Trait.STRONG),
        AGRESSIVE_TANKY_FRAGILE_SWIFT(Trait.AGRESSIVE, Trait.TANKY, Trait.FRAGILE, Trait.SWIFT),
        AGRESSIVE_AGILE_HARDY_STRONG(Trait.AGRESSIVE, Trait.AGILE, Trait.HARDY, Trait.STRONG),
        AGRESSIVE_AGILE_HARDY_SWIFT(Trait.AGRESSIVE, Trait.AGILE, Trait.HARDY, Trait.SWIFT),
        AGRESSIVE_AGILE_FRAGILE_STRONG(Trait.AGRESSIVE, Trait.AGILE, Trait.FRAGILE, Trait.STRONG),
        AGRESSIVE_AGILE_FRAGILE_SWIFT(Trait.AGRESSIVE, Trait.AGILE, Trait.FRAGILE, Trait.SWIFT);


        public final Trait aggressionTrait;
        public final Trait agilityTrait;
        public final Trait resilienceTrait;
        public final Trait strengthTrait;

        TraitSet(Trait aggressionTrait, Trait agilityTrait, Trait resilienceTrait, Trait strengthTrait) {
            this.aggressionTrait = aggressionTrait;
            this.agilityTrait = agilityTrait;
            this.resilienceTrait = resilienceTrait;
            this.strengthTrait = strengthTrait;
        }
    }

    private void getTraits() {
        TraitSet[] sets = TraitSet.values();
        TraitSet selectedSet = sets[random.nextInt(sets.length)];
        aggressionTrait = selectedSet.aggressionTrait;
        agilityTrait = selectedSet.agilityTrait;
        resilienceTrait = selectedSet.resilienceTrait;
        strengthTrait = selectedSet.strengthTrait;
    }

    private static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(VillagerAbstract.class, TrackedDataHandlerRegistry.BOOLEAN);
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private static final TrackedData<Boolean> PRIMAL = DataTracker.registerData(VillagerAbstract.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public VillagerAbstract(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.dataTracker.set(PRIMAL, true);
        getTraits();
    }

    public boolean isPrimal() {
        return this.dataTracker.get(PRIMAL);
    }

    public void setPrimal(boolean primal) {
        this.dataTracker.set(PRIMAL, primal);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.mate != null && !this.mate.isAlive()) {
            pairs.remove(this);
            pairs.remove(this.mate);
            this.mate = null;
        }

        // Sigmoid function to adjust breedCooldown based on socialBatter
        double sigmoidFactor = 1 / (1 + Math.exp(-0.1 * (socialBattery - 500)));
        breedCooldown += (int) (sigmoidFactor * 10);
        System.out.println("Social battery: " + socialBattery);
        System.out.println("VillagerAbstract tick " + breedCooldown);
        System.out.println("Villager is crowded: " + VillagerUtils.isCrowded(this, PERSONAL_SPACE_RADIUS, CROWD_THRESHOLD));
    }

    public boolean canSocializeWith(VillagerAbstract other) {
        return other != this;
    }

    protected void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.attackAnimationTimeout <= 0) {
            this.attackAnimationTimeout = random.nextInt(40) + 80;
            this.attackAnimationState.start(this.age);
        } else {
            --this.attackAnimationTimeout;
        }

        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 40;
            attackAnimationState.start(this.age);
        } else {
            --this.attackAnimationTimeout;
        }

        if (!this.isAttacking()) {
            attackAnimationState.stop();
        }
    }

    @Override
    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2f);
    }


    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
        this.dataTracker.startTracking(PRIMAL, false);
    }

    public VillagerAbstract getMate() {
        return mate;
    }

    public void setMate(VillagerAbstract mate) {
        this.mate = mate;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity mateEntity) {
        VillagerAbstract child = ModEntities.DUMMY_VILLAGER.create(world);
        if (child != null && mateEntity instanceof VillagerAbstract mate) {
            child.setPrimal(false);

            child.aggressionTrait = determineTraitInheritance(this.aggressionTrait, mate.aggressionTrait);
            child.agilityTrait = determineTraitInheritance(this.agilityTrait, mate.agilityTrait);
            child.resilienceTrait = determineTraitInheritance(this.resilienceTrait, mate.resilienceTrait);
            child.strengthTrait = determineTraitInheritance(this.strengthTrait, mate.strengthTrait);
        }
        return child;
    }

    private Trait determineTraitInheritance(Trait parent1Trait, Trait parent2Trait) {
        Map<Trait, Boolean> dominanceMap = Map.of(
                Trait.PEACEFUL, false,
                Trait.TANKY, true,
                Trait.AGRESSIVE, true,
                Trait.AGILE, false,
                Trait.HARDY, true,
                Trait.FRAGILE, false,
                Trait.STRONG, true,
                Trait.SWIFT, false
        );

        boolean parent1Dominant = dominanceMap.get(parent1Trait);
        boolean parent2Dominant = dominanceMap.get(parent2Trait);

        if (parent1Dominant && !parent2Dominant) {
            return parent1Trait;
        } else if (!parent1Dominant && parent2Dominant) {
            return parent2Trait;
        }

        return random.nextBoolean() ? parent1Trait : parent2Trait;
    }


    public boolean isReadyToBreed() {
        return this.breedCooldown > 5000;
    }

    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        } else if (other.getClass() != this.getClass()) {
            return false;
        } else {
            return this.isReadyToBreed() && other.isReadyToBreed();
        }
    }

    public void breed(ServerWorld world, AnimalEntity other) {
        PassiveEntity passiveEntity = this.createChild(world, other);
        if (passiveEntity != null) {
            passiveEntity.setBaby(false);
            passiveEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            this.breed(world, other, passiveEntity);
            world.spawnEntityAndPassengers(passiveEntity);
        }
    }

    public void breed(ServerWorld world, AnimalEntity other, @Nullable PassiveEntity baby) {
        Optional.ofNullable(this.getLovingPlayer()).or(() -> Optional.ofNullable(other.getLovingPlayer())).ifPresent((player) -> {
            player.incrementStat(Stats.ANIMALS_BRED);
            Criteria.BRED_ANIMALS.trigger(player, this, other, baby);
        });
        this.setBreedingAge(100);
        other.setBreedingAge(100);
        this.breedCooldown = 0;
        ((VillagerAbstract) other).breedCooldown = 0;
        world.sendEntityStatus(this, (byte)18);
        if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }

    }
}
