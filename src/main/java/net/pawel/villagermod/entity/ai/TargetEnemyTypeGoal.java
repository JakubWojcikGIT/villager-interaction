package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.GameRules;
import net.pawel.villagermod.entity.custom.VillagerAbstract;
import net.pawel.villagermod.utils.VillagerUtils;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class TargetEnemyTypeGoal extends TrackTargetGoal {
    private static final TargetPredicate VALID_AVOIDABLES_PREDICATE = TargetPredicate.createAttackable().ignoreVisibility().ignoreDistanceScalingFactor();

    public TargetEnemyTypeGoal(PathAwareEntity mob) {
        super(mob, true);
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        Optional<VillagerAbstract> nearestEnemyVillager = getNearestEnemyVillager();
        if (nearestEnemyVillager.isPresent()) {
            return this.canTrack(nearestEnemyVillager.get(), VALID_AVOIDABLES_PREDICATE);
        }
        return false;
    }

    @Override
    public void start() {
        Optional<VillagerAbstract> nearestEnemyVillager = getNearestEnemyVillager();
        nearestEnemyVillager.ifPresent(this.mob::setTarget);
        this.target = this.mob.getTarget();
        this.maxTimeWithoutVisibility = 300;
        super.start();
    }

    private Optional<VillagerAbstract> getNearestEnemyVillager() {
        VillagerAbstract nearestVillager = null;
        double nearestDistance = 0.0D;
        List<VillagerAbstract> visibleVillagers = VillagerUtils.getVisibleVillagers((VillagerAbstract) this.mob);
        for (VillagerAbstract visibleVillager : visibleVillagers) {
            if (visibleVillager == this.mob) {
                continue;
            }

            if (visibleVillager instanceof VillagerAbstract && visibleVillager.getVillagerType() != ((VillagerAbstract) this.mob).getVillagerType()) {
                double distance = VillagerUtils.distanceToSqr((VillagerAbstract) this.mob, visibleVillager);
                if (nearestDistance == 0.0D || distance < nearestDistance) {
                    nearestVillager = visibleVillager;
                    nearestDistance = distance;
                }
            }
        }
        return Optional.ofNullable(nearestVillager);
    }
}

