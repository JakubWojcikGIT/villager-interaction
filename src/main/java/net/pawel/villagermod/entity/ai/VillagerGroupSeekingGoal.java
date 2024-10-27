package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.custom.VillagerAbstract;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class VillagerGroupSeekingGoal extends Goal {
    public static final TargetPredicate VALID_GROUP_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0).ignoreVisibility();
    protected final VillagerAbstract villager;
    protected final World world;
    @Nullable
    protected VillagerAbstract groupLeader;
    private final double speed;
    protected final int personalSpaceRadius;

    public VillagerGroupSeekingGoal(VillagerAbstract villager, double speed, int personalSpaceRadius) {
        this.villager = villager;
        this.world = villager.getWorld();
        this.speed = speed;
        this.personalSpaceRadius = personalSpaceRadius;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.groupLeader = this.findGroupLeader();
        return this.groupLeader != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.groupLeader != null && this.groupLeader.isAlive() && !this.groupLeader.isPanicking() && this.villager.squaredDistanceTo(this.groupLeader) > this.personalSpaceRadius * this.personalSpaceRadius;
    }

    @Override
    public void stop() {
        this.groupLeader = null;
    }

    @Override
    public void tick() {
        if (this.villager.squaredDistanceTo(this.groupLeader) > this.personalSpaceRadius * this.personalSpaceRadius) {
            this.villager.getLookControl().lookAt(this.groupLeader, 10.0F, (float) this.villager.getMaxLookPitchChange());
            this.villager.getNavigation().startMovingTo(this.groupLeader, this.speed);
        }
    }

    @Nullable
    protected VillagerAbstract findGroupLeader() {
        List<? extends VillagerAbstract> list = this.world.getTargets(VillagerAbstract.class, VALID_GROUP_PREDICATE, this.villager, this.villager.getBoundingBox().expand(16.0));
        double d = Double.MAX_VALUE;
        VillagerAbstract closestVillager = null;

        for (VillagerAbstract villager : list) {
            if (this.villager.canSocializeWith(villager) && this.villager.squaredDistanceTo(villager) < d) {
                closestVillager = villager;
                d = this.villager.squaredDistanceTo(villager);
            }
        }
        return closestVillager;
    }
}