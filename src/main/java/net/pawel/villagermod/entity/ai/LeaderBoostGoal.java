package net.pawel.villagermod.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.pawel.villagermod.entity.custom.VillagerAbstract;
import net.pawel.villagermod.utils.VillagerUtils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoostGoal extends Goal {
    private final VillagerAbstract leader;
    private final Map<VillagerAbstract, Double> originalSpeed = new HashMap<>();
    private final Map<VillagerAbstract, Double> originalAttackDamage = new HashMap<>();

    public LeaderBoostGoal(VillagerAbstract leader) {
        this.leader = leader;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public void tick() {
        List<VillagerAbstract> nearbyVillagers = VillagerUtils.getVisibleVillagers(leader, 10.0D);
        for (VillagerAbstract entity : nearbyVillagers) {
            if (entity != leader) {
                EntityAttributeInstance speedAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                EntityAttributeInstance attackDamageAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);

                if (!originalSpeed.containsKey(entity)) {
                    originalSpeed.put(entity, speedAttribute.getBaseValue());
                }
                if (!originalAttackDamage.containsKey(entity)) {
                    originalAttackDamage.put(entity, attackDamageAttribute.getBaseValue());
                }

                speedAttribute.setBaseValue(originalSpeed.get(entity) * 1.1);
                attackDamageAttribute.setBaseValue(originalAttackDamage.get(entity) * 1.1);
            }
        }

        for (VillagerAbstract villager : originalSpeed.keySet()) {
            if (!nearbyVillagers.contains(villager)) {
                villager.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(originalSpeed.get(villager));
                villager.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(originalAttackDamage.get(villager));
            }
        }

        originalSpeed.keySet().removeIf(villager -> !nearbyVillagers.contains(villager));
        originalAttackDamage.keySet().removeIf(villager -> !nearbyVillagers.contains(villager));
    }
}