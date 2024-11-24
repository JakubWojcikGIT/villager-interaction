package net.pawel.villagermod.utils;

import net.minecraft.util.math.Box;
import net.pawel.villagermod.entity.custom.VillagerAbstract;

import java.util.List;

public class VillagerUtils {
    public static List<VillagerAbstract> getVisibleVillagers(VillagerAbstract villager, double range) {
        Box searchBox = new Box(villager.getX() - range, villager.getY() - range, villager.getZ() - range,
                villager.getX() + range, villager.getY() + range, villager.getZ() + range);

        return villager.getWorld().getEntitiesByClass(VillagerAbstract.class, searchBox, v ->
                villager.getVisibilityCache().canSee(v)
        );
    }

    public static int countNearbyVillagers(VillagerAbstract villager, double radius) {
        return getVisibleVillagers(villager, radius).size();
    }


    public static boolean isCrowded(VillagerAbstract villager, int personalSpaceRadius, int crowdThreshold) {
        List<? extends VillagerAbstract> list = getVisibleVillagers(villager, personalSpaceRadius);
        return list.size() >= crowdThreshold;
    }
}
