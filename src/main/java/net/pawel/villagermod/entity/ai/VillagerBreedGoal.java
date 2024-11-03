package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.custom.VillagerAbstract;

import java.util.EnumSet;

public class VillagerBreedGoal extends Goal {
    private static final TargetPredicate VALID_MATE_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(8.0).ignoreVisibility();
    protected final VillagerAbstract villager;
    protected final World world;
    private VillagerAbstract mate;
    private int timer;
    private final double speed;

    public VillagerBreedGoal(VillagerAbstract villager, double speed) {
        this.villager = villager;
        this.world = villager.getWorld();
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return false;
    }
}
