package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.pawel.villagermod.entity.custom.VillagerAbstract;
import net.pawel.villagermod.utils.VillagerUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class TargetIrritatingFriend extends TrackTargetGoal {
    private static final TargetPredicate VALID_AVOIDABLES_PREDICATE = TargetPredicate.createAttackable().ignoreVisibility().ignoreDistanceScalingFactor();


    public TargetIrritatingFriend(PathAwareEntity mob) {
        super(mob, true);
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        Optional<VillagerAbstract> nearestFriendVillager = getNearestFriendVillager();
        if (nearestFriendVillager.isPresent() && ((VillagerAbstract) this.mob).getIrritation() > VillagerAbstract.IRRITATION_THRESHOLD) {
            return this.canTrack(nearestFriendVillager.get(), VALID_AVOIDABLES_PREDICATE);
        }
        return false;
    }

    @Override
    public void start() {
        Optional<VillagerAbstract> nearestFriendVillager = getNearestFriendVillager();
        nearestFriendVillager.ifPresent(this.mob::setTarget);
        this.target = this.mob.getTarget();
        this.maxTimeWithoutVisibility = 300;
        super.start();
    }

    private Optional<VillagerAbstract> getNearestFriendVillager() {
        VillagerAbstract nearestVillager = null;
        double nearestDistance = 0.0D;
        List<VillagerAbstract> visibleVillagers = VillagerUtils.getVisibleVillagers((VillagerAbstract) this.mob);
        for (VillagerAbstract visibleVillager : visibleVillagers) {
            if (visibleVillager == this.mob) {
                continue;
            }

            if (visibleVillager instanceof VillagerAbstract && visibleVillager.getVillagerType() == ((VillagerAbstract) this.mob).getVillagerType()) {
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

