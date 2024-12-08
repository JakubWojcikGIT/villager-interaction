package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.custom.VillagerAbstract;

public class NightDamageBoostGoal extends Goal {
    private final VillagerAbstract villager;
    private final World world;

    public NightDamageBoostGoal(VillagerAbstract villager) {
        this.villager = villager;
        this.world = villager.getWorld();
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public void tick() {
        if (world.isNight()) {
            villager.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(
                villager.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.5
            );
        } else {
            villager.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(
                villager.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) / 1.5
            );
        }
    }
}