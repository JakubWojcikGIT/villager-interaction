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
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class VillagerAbstract extends AnimalEntity {
    private static final Random random = new Random();
    protected int PERSONAL_SPACE_RADIUS = 3;
    protected int CROWD_THRESHOLD = 3;
    public static final Map<VillagerAbstract, VillagerAbstract> pairs = new HashMap<>();
    private VillagerAbstract mate;
    private int breedCooldown = 0;

    public Trait getAggressionTrait() {
        return aggressionTrait;
    }

    public Trait getAgilityTrait() {
        return agilityTrait;
    }

    private Trait aggressionTrait;
    private Trait agilityTrait;


    public enum Trait {
        PEACEFUL, TANKY, AGRESSIVE, AGILE
    }

    public enum TraitSet {
        PEACEFUL_TANKY(Trait.PEACEFUL, Trait.TANKY),  // recesywne, dominujące
        AGRESSIVE_AGILE(Trait.AGRESSIVE, Trait.AGILE), // dominujące, recesywne
        AGRESSIVE_TANKY(Trait.AGRESSIVE, Trait.TANKY), // dominujące, dominujące
        PEACEFUL_AGILE(Trait.PEACEFUL, Trait.AGILE);   // recesywne, recesywne

        public final Trait aggressionTrait;
        public final Trait agilityTrait;

        TraitSet(Trait aggressionTrait, Trait agilityTrait) {
            this.aggressionTrait = aggressionTrait;
            this.agilityTrait = agilityTrait;
        }
    }

    private void getTraits() {
        TraitSet[] sets = TraitSet.values();
        TraitSet selectedSet = sets[random.nextInt(sets.length)];
        aggressionTrait = selectedSet.aggressionTrait;
        agilityTrait = selectedSet.agilityTrait;
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
        System.out.println("VillagerAbstract tick " + breedCooldown);
        if (this.mate != null && !this.mate.isAlive()) {
            pairs.remove(this);
            pairs.remove(this.mate);
            this.mate = null;
        }

            breedCooldown++;
    }

    public boolean canSocializeWith(VillagerAbstract other) {
        return other != this;
    }

    protected void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.attackAnimationTimeout <= 0) {
            this.attackAnimationTimeout = this.random.nextInt(40) + 80;
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
        }
        return child;
    }

    private Trait determineTraitInheritance(Trait parent1Trait, Trait parent2Trait) {
        Map<Trait, Boolean> dominanceMap = Map.of(
                Trait.PEACEFUL, false,
                Trait.TANKY, true,
                Trait.AGRESSIVE, true,
                Trait.AGILE, false
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
        return this.breedCooldown > 2000;
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
            passiveEntity.setBaby(true);
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
