package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.custom.VillagerAbstract;

import java.util.EnumSet;

public class VillagerPairGoal extends Goal {
    private static int count = 0;
    private final VillagerAbstract villager;
    private final double speed;
    private VillagerAbstract mate;
    private final World world;


    public VillagerPairGoal(VillagerAbstract villager, double speed) {
        this.villager = villager;
        this.speed = speed;
        this.world = villager.getWorld();
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.villager.getMate() == null || !this.villager.getMate().isAlive()) {
            this.mate = this.findMate();
            if (this.mate != null) {
                this.villager.setCustomName(Text.of("Has mate " + count));
                this.mate.setCustomName(Text.of("Has mate " + count));
                this.villager.setMate(this.mate);
                this.mate.setMate(this.villager);
                VillagerAbstract.pairs.put(this.villager, this.mate);
                VillagerAbstract.pairs.put(this.mate, this.villager);
                count++;
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        if (this.mate == null || !this.mate.isAlive()) {
            this.stop();
            this.canStart();
        } else {
            this.villager.getLookControl().lookAt(this.mate, 10.0F, (float) this.villager.getMaxLookPitchChange());
            this.villager.getNavigation().startMovingTo(this.mate, this.speed);
        }
    }

     private VillagerAbstract findMate() {
        return this.world.getEntitiesByClass(VillagerAbstract.class, this.villager.getBoundingBox().expand(8.0D), villager -> villager != this.villager && villager.getMate() == null && !VillagerAbstract.pairs.containsKey(villager))
                .stream()
                .findFirst()
                .orElse(null);
    }
}