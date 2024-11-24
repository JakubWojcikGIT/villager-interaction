package net.pawel.villagermod.entity.ai;

import net.pawel.villagermod.entity.custom.VillagerAbstract;
import net.pawel.villagermod.utils.VillagerUtils;

public class IntrovertGroupSeekingGoal extends VillagerGroupSeekingGoal {
    private final int crowdThreshold;

    public IntrovertGroupSeekingGoal(VillagerAbstract villager, double speed) {
        super(villager, speed);
        this.crowdThreshold = VillagerAbstract.CROWD_THRESHOLD;
    }

    @Override
    public boolean canStart() {
        this.groupLeader = super.findGroupLeader();
        return this.groupLeader != null && !VillagerUtils.isCrowded(this.groupLeader, super.personalSpaceRadius, this.crowdThreshold);
    }

    @Override
    public boolean shouldContinue() {
        return this.groupLeader != null && this.groupLeader.isAlive() && !this.groupLeader.isPanicking() && this.villager.squaredDistanceTo(this.groupLeader) > super.personalSpaceRadius * super.personalSpaceRadius && !VillagerUtils.isCrowded(this.groupLeader, super.personalSpaceRadius, this.crowdThreshold);
    }
}
