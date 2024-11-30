package net.pawel.villagermod.entity.ai;

import java.util.EnumSet;
import java.util.Map;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.Goal.Control;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.pawel.villagermod.utils.VillagerUtils;
import org.jetbrains.annotations.Nullable;
import net.pawel.villagermod.entity.custom.VillagerAbstract;

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
        System.out.println("VillagerBreedGoal canStart");
        if (!this.villager.isReadyToBreed()) {
            return false;
        } else {
            this.mate = this.findMate();
            return this.mate != null;
        }
    }

    public boolean shouldContinue() {
        System.out.println("VillagerBreedGoal shouldContinue");
        if (this.mate == null) {
            return false;
        }
        return this.mate.isAlive() && this.mate.isReadyToBreed() && this.timer < 60 && !this.mate.isPanicking();
    }

    public void stop() {
        System.out.println("VillagerBreedGoal stop");
        this.mate = null;
        this.timer = 0;
    }

    public void tick() {
        System.out.println("VillagerBreedGoal tick");
        this.villager.getLookControl().lookAt(this.mate, 10.0F, (float)this.villager.getMaxLookPitchChange());
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
        System.out.println("VillagerBreedGoal breed");
        this.villager.breed((ServerWorld)this.world, this.mate);
    }
}