package net.pawel.villagermod.utils;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.custom.VillagerAbstract;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class EntityLog {

    private static int generation = 1;
    private static final String LOG_DIRECTORY = System.getProperty("user.home") + "/Desktop";
    private static final String LOG_FILE = LOG_DIRECTORY + "/villagers_log.csv";

    public static void listEntitiesInRadius(World world, BlockPos center, int radius, boolean logToFile) {
        try {
            System.out.println("Listing entities in radius " + radius + " around " + center);

            Box searchBox = new Box(
                    center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                    center.getX() + radius, center.getY() + radius, center.getZ() + radius
            );

            List<VillagerAbstract> villagers = world.getEntitiesByClass(VillagerAbstract.class, searchBox, villager -> true);

            for (VillagerAbstract villager : villagers) {
                System.out.println("Villager found: " + villager.getName().getString());
            }

            if (logToFile) {
                StringBuilder csvContent = new StringBuilder();
                for (VillagerAbstract villager : villagers) {
                    csvContent.append(generation).append(",");
                    csvContent.append(villager.getHealth()).append(",");
                    csvContent.append(villager.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH)).append(",");
                    csvContent.append(villager.getAggressionTrait()).append(",");
                    csvContent.append(villager.getAgilityTrait()).append(",");
                    csvContent.append(villager.getResilienceTrait()).append(",");
                    csvContent.append(villager.getStrengthTrait()).append(",");
                    csvContent.append(villager.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)).append(",");
                    csvContent.append(villager.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)).append(",");
                    csvContent.append(villager.isPrimal()).append(",");
                    csvContent.append(villager.getType()).append("\n");
                }
                saveLogToFile(csvContent.toString());
                generation++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveLogToFile(String content) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initializeLogFile() {
        try (FileWriter writer = new FileWriter(LOG_FILE)) {
            writer.write("generation,health,maxHealth,aggressionTrait,agilityTrait,resilienceTrait,strengthTrait,attackDamage,movementSpeed,isPrimal,type\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}