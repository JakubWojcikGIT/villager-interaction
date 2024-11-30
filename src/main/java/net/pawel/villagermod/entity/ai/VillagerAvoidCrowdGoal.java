package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.custom.VillagerAbstract;

import java.util.EnumSet;
import java.util.List;

public class VillagerAvoidCrowdGoal extends Goal {
    protected final VillagerAbstract villager;
    protected final World world;
    private final double speed;
    private final int personalSpaceRadius;
    private final int crowdThreshold;
    private final int avoidDuration;
    private int avoidTime;

    public VillagerAvoidCrowdGoal(VillagerAbstract villager, double speed, int personalSpaceRadius, int crowdThreshold, int avoidDuration) {
        this.villager = villager;
        this.world = villager.getWorld();
        this.speed = speed;
        this.personalSpaceRadius = personalSpaceRadius;
        this.crowdThreshold = crowdThreshold;
        this.avoidDuration = avoidDuration;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return isCrowded();
    }

    @Override
    public boolean shouldContinue() {
        return avoidTime > 0 && isCrowded();
    }

    @Override
    public void start() {
        avoidTime = avoidDuration;
    }

    @Override
    public void stop() {
        avoidTime = 0;
    }

    @Override
    public void tick() {
        if (avoidTime > 0) {
            avoidTime--;
            moveAwayFromCrowd();
        }
    }

    private boolean isCrowded() {
        List<? extends VillagerAbstract> list = this.world.getEntitiesByClass(VillagerAbstract.class, this.villager.getBoundingBox().expand(this.personalSpaceRadius), villager -> villager != this.villager);
        return list.size() > this.crowdThreshold;
    }

    private void moveAwayFromCrowd() {
        List<? extends VillagerAbstract> list = this.world.getEntitiesByClass(VillagerAbstract.class, this.villager.getBoundingBox().expand(this.personalSpaceRadius), villager -> villager != this.villager);
        if (list.isEmpty()) {
            return;
        }

        double avgX = 0;
        double avgZ = 0;
        for (VillagerAbstract villager : list) {
            avgX += villager.getX();
            avgZ += villager.getZ();
        }
        avgX /= list.size();
        avgZ /= list.size();

        double directionX = this.villager.getX() - avgX;
        double directionZ = this.villager.getZ() - avgZ;
        double length = Math.sqrt(directionX * directionX + directionZ * directionZ);

        if (length > 0) {
            directionX /= length;
            directionZ /= length;
        }

        this.villager.getNavigation().startMovingTo(this.villager.getX() + directionX * 8.0,
                this.villager.getY(),
                this.villager.getZ() + directionZ * 8.0,
                this.speed);
    }
}