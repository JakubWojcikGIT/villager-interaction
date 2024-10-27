package net.pawel.villagermod.entity.ai;

import net.pawel.villagermod.entity.custom.VillagerAbstract;

import java.util.List;

public class IntrovertGroupSeekingGoal extends VillagerGroupSeekingGoal {
    private final int crowdThreshold;

    public IntrovertGroupSeekingGoal(VillagerAbstract villager, double speed, int personalSpaceRadius, int crowdThreshold) {
        super(villager, speed, personalSpaceRadius);
        this.crowdThreshold = crowdThreshold;
    }

    @Override
    public boolean canStart() {
        this.groupLeader = super.findGroupLeader();
        return this.groupLeader != null && !this.isCrowded();
    }

    @Override
    public boolean shouldContinue() {
        return this.groupLeader != null && this.groupLeader.isAlive() && !this.groupLeader.isPanicking() && this.villager.squaredDistanceTo(this.groupLeader) > super.personalSpaceRadius * super.personalSpaceRadius && !this.isCrowded();
    }

    private boolean isCrowded() {
        if (this.groupLeader == null) {
            return false;
        }
        List<? extends VillagerAbstract> list = this.world.getEntitiesByClass(VillagerAbstract.class, this.groupLeader.getBoundingBox().expand(super.personalSpaceRadius), villager -> villager != this.groupLeader);
        return list.size() > this.crowdThreshold;
    }
}
