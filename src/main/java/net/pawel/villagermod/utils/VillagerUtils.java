package net.pawel.villagermod.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.custom.VillagerAbstract;

import java.util.List;
import java.util.stream.Collectors;

public class VillagerUtils {
    public static List<LivingEntity> getNearbyVillagers(Entity entity, World world, double radius) {
        return world.getEntitiesByType(EntityType.VILLAGER, entity.getBoundingBox().expand(radius), e -> e != entity)
                .stream()
                .map(e -> (LivingEntity) e)
                .collect(Collectors.toList());
    }

    public static double distanceToSqr(VillagerAbstract villager1, VillagerAbstract villager2) {
        double distance = villager1.distanceTo(villager2);
        return distance * distance;
    }

    public static boolean isFighting(VillagerAbstract villager) {
        return villager.getTarget() != null;
    }

    public static List<VillagerAbstract> getVisibleVillagers(VillagerAbstract villager) {
        double range = 16.0; // Define the range within which to search for villagers
        Box searchBox = new Box(villager.getX() - range, villager.getY() - range, villager.getZ() - range,
                villager.getX() + range, villager.getY() + range, villager.getZ() + range);

        return villager.getWorld().getEntitiesByClass(VillagerAbstract.class, searchBox, v ->
                villager.getVisibilityCache().canSee(v)
        );
    }

    public static VillagerAbstract getNearestVillager(VillagerAbstract villager) {
        VillagerAbstract nearestVillager = null;
        double nearestDistance = 0.0D;
        List<VillagerAbstract> visibleVillagers = VillagerUtils.getVisibleVillagers(villager);
        for (VillagerAbstract visibleVillager : visibleVillagers) {
            if (visibleVillager == villager) {
                continue;
            }

            if (visibleVillager instanceof VillagerAbstract) {
                double distance = VillagerUtils.distanceToSqr(villager, visibleVillager);
                if (nearestDistance == 0.0D || distance < nearestDistance) {
                    nearestVillager = visibleVillager;
                    nearestDistance = distance;
                }
            }
        }
        return nearestVillager;
    }


    public static boolean isCrowded(VillagerAbstract groupLeader, World world, int personalSpaceRadius, int crowdThreshold) {
        if (groupLeader == null) {
            return false;
        }
        List<? extends VillagerAbstract> list = world.getEntitiesByClass(VillagerAbstract.class, groupLeader.getBoundingBox().expand(personalSpaceRadius), villager -> villager != groupLeader);
        return list.size() > crowdThreshold;
    }
}
