package net.pawel.villagermod.entity.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.attribute.EntityAttributes;
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
import net.pawel.villagermod.entity.ai.NightDamageBoostGoal;
import net.pawel.villagermod.entity.ai.VillagerBreedGoal;
import net.pawel.villagermod.utils.TraitType;
import net.pawel.villagermod.utils.VillagerTraits;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public abstract class VillagerAbstract extends AnimalEntity {
    private static final Random random = new Random();
    final public static int PERSONAL_SPACE_RADIUS = 3;
    final public static int CROWD_THRESHOLD = 3;
    public static final Map<VillagerAbstract, VillagerAbstract> pairs = new HashMap<>();
    private VillagerAbstract mate;
    private int breedCooldown = 0;
    protected int socialBattery = 500;
    protected int previousCrowdSize = 0;
    public VillagerTraits villagerTraits;


    private static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(VillagerAbstract.class, TrackedDataHandlerRegistry.BOOLEAN);
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private static final TrackedData<Boolean> PRIMAL = DataTracker.registerData(VillagerAbstract.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public VillagerAbstract(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.dataTracker.set(PRIMAL, true);
        this.villagerTraits = new VillagerTraits();
        applyTraits();
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

            child.villagerTraits = new VillagerTraits(this.villagerTraits, mate.villagerTraits);
        }
        return child;
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
        world.sendEntityStatus(this, (byte) 18);
        if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }

    }

    private void applyTraits() {
        for (TraitType traitType : TraitType.values()) {
            String trait = villagerTraits.describeTrait(traitType);
            switch (traitType) {
                case AGGRESSION:
                    String aggressionTrait = villagerTraits.describeTrait(TraitType.AGGRESSION);
                    if (aggressionTrait.equals("Aggressive")) {
                        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(
                                this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.2
                        );
                        this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(
                                this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE) + 0.1
                        );
                        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(
                                this.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH) * 0.9
                        );
                    } else if (aggressionTrait.equals("Peaceful")) {
                        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(
                                this.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH) * 1.2
                        );
                        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(
                                this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 0.8
                        );
                        this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(
                                this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE) - 0.1
                        );
                    }
                    break;
                case AGILITY:
                    if (trait.equals("Tanky")) {
                        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(
                                this.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH) * 2
                        );
                        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(
                                this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.9
                        );
                    } else if (trait.equals("Agile")) {
                        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(
                                this.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH) * 0.9
                        );
                        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(
                                this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 1.2
                        );
                    }
                    break;
                case COURAGE:
                    if (trait.equals("Brave")) {
                    } else if (trait.equals("Cowardly")) {
                    }
                    break;
                case STRENGTH:
                    if (trait.equals("Strong")) {
                    }
                    break;
                case LEADERSHIP:
                    if (trait.equals("Leader")) {
                        // boost to nearby villagers
                    }
                    break;
                case SPEED:
                    if (trait.equals("Swift")) {
                    } else if (trait.equals("Heavy")) {
                    }
                    break;
                case NIGHT_VISION:
                    if (trait.equals("Night Owl")) {
                        this.goalSelector.add(4, new NightDamageBoostGoal(this));
                    }
                    break;
            }
        }
    }
}
