package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.pawel.villagermod.entity.custom.VillagerAbstract;
import net.pawel.villagermod.utils.VillagerUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GroupRevengeGoal extends Goal {
    private final VillagerAbstract villager;

    public GroupRevengeGoal(VillagerAbstract villager) {
        this.villager = villager;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        return this.villager.getAttacker() != null;
    }

    @Override
    public void start() {
        List<VillagerAbstract> nearbyVillagers = VillagerUtils.getVisibleVillagers(this.villager);
        for (VillagerAbstract nearbyVillager : nearbyVillagers) {
            if (nearbyVillager instanceof VillagerAbstract) {
                AtomicReference<VillagerAbstract> villagerAbstract = new AtomicReference<>((VillagerAbstract) nearbyVillager);
                villagerAbstract.get().setTarget(this.villager.getAttacker());
            }
        }
        super.start();
    }
}