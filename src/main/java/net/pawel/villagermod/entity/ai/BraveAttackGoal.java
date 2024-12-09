package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;

import java.util.EnumSet;

public class BraveAttackGoal extends Goal {
    private final MobEntity mob;
    private LivingEntity target;

    public BraveAttackGoal(MobEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.TARGET));
    }

    @Override
    public boolean canStart() {
        this.target = this.mob.getTarget();
        return this.target != null && this.target.isAlive();
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.target, 1.5);
    }

    @Override
    public void stop() {
        this.target = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
        if (this.mob.squaredDistanceTo(this.target) < 2.0D) {
            this.mob.tryAttack(this.target);
        } else {
            this.mob.getNavigation().startMovingTo(this.target, 1.5);
        }
    }
}