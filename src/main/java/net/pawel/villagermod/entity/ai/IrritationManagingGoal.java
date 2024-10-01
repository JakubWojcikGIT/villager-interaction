package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.text.Text;
import net.pawel.villagermod.entity.custom.VillagerAbstract;
import net.pawel.villagermod.utils.VillagerUtils;

import java.util.EnumSet;

public class IrritationManagingGoal extends Goal {
    private final VillagerAbstract villager;

    public IrritationManagingGoal(VillagerAbstract villager) {
        this.villager = villager;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return true;
    }


    public void updateIrritation() {
        if (VillagerUtils.isFighting(this.villager)) {
            villager.setIrritation(0);
            return;
        }
        VillagerAbstract nearestVillager = VillagerUtils.getNearestVillager(this.villager);
        double v = 0;
        if (nearestVillager != null) {
            v = VillagerUtils.distanceToSqr(this.villager, nearestVillager);
        }
        if (v < 50.0D && v > 0.0D) {
            villager.setIrritation(villager.getIrritation() + 1);
        } else if (v > 50.0D || v == 0.0D) {
            villager.setIrritation(villager.getIrritation() - 1);
        }
        if (villager.getIrritation() < 0) {
            villager.setIrritation(0);
        }
    }

    @Override
    public void tick() {
        updateIrritation();
        this.villager.setCustomName(Text.of("Irritation: " + villager.getIrritation()));
    }

}