package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.custom.VillagerAbstract;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class VillagerBreedGoal extends Goal {
    protected final VillagerAbstract villager;
    protected final World world;
    @Nullable
    protected VillagerAbstract mate;
    private int timer;
    private final double speed;

    public VillagerBreedGoal(VillagerAbstract villager, double speed) {
        this.villager = villager;
        this.world = villager.getWorld();
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        if (!this.villager.isReadyToBreed()) {
            return false;
        } else {
            this.mate = this.findMate();
            return this.mate != null;
        }
    }

    public boolean shouldContinue() {
        if (this.mate == null) {
            return false;
        }
        return this.mate.isAlive() && this.mate.isReadyToBreed() && this.timer < 60 && !this.mate.isPanicking();
    }

    public void stop() {
        this.mate = null;
        this.timer = 0;
    }

    public void tick() {
        this.villager.getLookControl().lookAt(this.mate, 10.0F, (float) this.villager.getMaxLookPitchChange());
        this.villager.getNavigation().startMovingTo(this.mate, this.speed);
        ++this.timer;
        if (this.timer >= this.getTickCount(60) && this.villager.squaredDistanceTo(this.mate) < 9.0) {
            this.breed();
        }
    }

    @Nullable
    private VillagerAbstract findMate() {
        return VillagerAbstract.pairs.get(this.villager);
    }

    protected void breed() {
        this.villager.breed((ServerWorld) this.world, this.mate);
    }
}