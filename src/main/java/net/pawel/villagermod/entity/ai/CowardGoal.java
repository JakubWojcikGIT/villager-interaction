package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.pawel.villagermod.entity.custom.VillagerAbstract;

import java.util.EnumSet;
import java.util.List;

public class CowardGoal extends Goal {
    private final AnimalEntity villager;
    private LivingEntity closestEnemy;

    public CowardGoal(AnimalEntity villager) {
        this.villager = villager;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        List<LivingEntity> nearbyEnemies = villager.getWorld().getEntitiesByClass(LivingEntity.class, villager.getBoundingBox().expand(10.0D), entity -> entity instanceof MobEntity || entity instanceof PlayerEntity);
        List<AnimalEntity> nearbyVillagers = villager.getWorld().getEntitiesByClass(AnimalEntity.class, villager.getBoundingBox().expand(10.0D), entity -> entity instanceof VillagerAbstract);

        if (nearbyEnemies.size() > nearbyVillagers.size()) {
            this.closestEnemy = nearbyEnemies.get(0);
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        double x = villager.getX() - closestEnemy.getX();
        double z = villager.getZ() - closestEnemy.getZ();
        this.villager.getNavigation().startMovingTo(x, 0, z, 1.5);
    }

    @Override
    public void stop() {
        this.closestEnemy = null;
        this.villager.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.closestEnemy != null) {
            this.villager.getLookControl().lookAt(this.closestEnemy, 30.0F, 30.0F);
            double x = villager.getX() - closestEnemy.getX();
            double z = villager.getZ() - closestEnemy.getZ();
            this.villager.getNavigation().startMovingTo(x, 0, z, 1.5);
        }
    }
}