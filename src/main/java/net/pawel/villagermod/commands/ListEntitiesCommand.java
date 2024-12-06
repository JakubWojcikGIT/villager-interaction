package net.pawel.villagermod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.custom.VillagerAbstract;
import net.pawel.villagermod.utils.TraitType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListEntitiesCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("listentities")
                .then(CommandManager.argument("radius", IntegerArgumentType.integer(1, 100))
                        .then(CommandManager.argument("logToFile", BoolArgumentType.bool())
                                .executes(context -> {
                                    int radius = IntegerArgumentType.getInteger(context, "radius");
                                    boolean logToFile = BoolArgumentType.getBool(context, "logToFile");
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    if (player != null) {
                                        BlockPos playerPos = player.getBlockPos();
                                        World world = player.getWorld();
                                        listEntitiesInRadius(world, player, playerPos, radius, logToFile);
                                    }
                                    return 1;
                                }))));
    }

    private static void listEntitiesInRadius(World world, ServerPlayerEntity player, BlockPos center, int radius, boolean logToFile) {
        Box box = new Box(
                center.add(-radius, -radius, -radius),
                center.add(radius, radius, radius)
        );

        List<Entity> entities = world.getEntitiesByClass(Entity.class, box, entity -> true);

        StringBuilder logContent = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (entities.isEmpty()) {
            player.sendMessage(Text.literal("Brak stworzeń w promieniu " + radius + " kratek."), false);
            logContent.append("Brak stworzeń w promieniu ").append(radius).append(" kratek.\n");
        } else {
            player.sendMessage(Text.literal("Stworzenia w promieniu " + radius + " kratek:"), false);
            logContent.append("Stworzenia w promieniu ").append(radius).append(" kratek:\n");

            for (Entity entity : entities) {
                if (entity instanceof VillagerAbstract villager) {
                    String entityName = villager.getType().getTranslationKey().strip().replace("entity.villagermod.", "");

                    double maxHealth = villager.getHealth();
                    double movementSpeed = villager.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                    String isPrimal = villager.isPrimal() ? "true" : "false";

                    String aggressionTrait = villager.villagerTraits.describeTrait(TraitType.AGGRESSION);
                    String aggressiveAlleles = villager.villagerTraits.getAllele(TraitType.AGGRESSION).toString();

                    String agilityTrait = villager.villagerTraits.describeTrait(TraitType.AGILITY);
                    String agilityAlleles = villager.villagerTraits.getAllele(TraitType.AGILITY).toString();

                    String sociabilityTrait = villager.villagerTraits.describeTrait(TraitType.SOCIABILITY);
                    String sociabilityAlleles = villager.villagerTraits.getAllele(TraitType.SOCIABILITY).toString();

                    String courageTrait = villager.villagerTraits.describeTrait(TraitType.COURAGE);
                    String courageAlleles = villager.villagerTraits.getAllele(TraitType.COURAGE).toString();

                    String intelligenceTrait = villager.villagerTraits.describeTrait(TraitType.INTELLIGENCE);
                    String intelligenceAlleles = villager.villagerTraits.getAllele(TraitType.INTELLIGENCE).toString();

                    String curiosityTrait = villager.villagerTraits.describeTrait(TraitType.CURIOSITY);
                    String curiosityAlleles = villager.villagerTraits.getAllele(TraitType.CURIOSITY).toString();

                    String strengthTrait = villager.villagerTraits.describeTrait(TraitType.STRENGTH);
                    String strengthAlleles = villager.villagerTraits.getAllele(TraitType.STRENGTH).toString();

                    String leadershipTrait = villager.villagerTraits.describeTrait(TraitType.LEADERSHIP);
                    String leadershipAlleles = villager.villagerTraits.getAllele(TraitType.LEADERSHIP).toString();

                    String speedTrait = villager.villagerTraits.describeTrait(TraitType.SPEED);
                    String speedAlleles = villager.villagerTraits.getAllele(TraitType.SPEED).toString();

                    String nightVisionTrait = villager.villagerTraits.describeTrait(TraitType.NIGHT_VISION);
                    String nightVisionAlleles = villager.villagerTraits.getAllele(TraitType.NIGHT_VISION).toString();

                    String entityInfo = String.format(
                            " - %s has attributes: {ACTUAL HEALTH: %.1f, GENERIC_MOVEMENT_SPEED: %.2f, IS_PRIMAL: %s, " +
                                    "AGGRESSION_TRAIT: %s WITH %s, AGILITY_TRAIT: %s WITH %s, SOCIABILITY_TRAIT: %s WITH %s, " +
                                    "COURAGE_TRAIT: %s WITH %s, INTELLIGENCE_TRAIT: %s WITH %s, CURIOSITY_TRAIT: %s WITH %s, " +
                                    "STRENGTH_TRAIT: %s WITH %s, LEADERSHIP_TRAIT: %s WITH %s, SPEED_TRAIT: %s WITH %s, " +
                                    "NIGHT_VISION_TRAIT: %s WITH %s}",
                            capitalize(entityName), maxHealth, movementSpeed, isPrimal,
                            aggressionTrait, aggressiveAlleles, agilityTrait, agilityAlleles,
                            sociabilityTrait, sociabilityAlleles, courageTrait, courageAlleles,
                            intelligenceTrait, intelligenceAlleles, curiosityTrait, curiosityAlleles,
                            strengthTrait, strengthAlleles, leadershipTrait, leadershipAlleles,
                            speedTrait, speedAlleles, nightVisionTrait, nightVisionAlleles
                    );
                    player.sendMessage(Text.literal(entityInfo), false);
                    logContent.append(entityInfo).append("\n");
                }
            }
        }

        if (logToFile) {
            String timestamp = LocalDateTime.now().format(formatter);
            logContent.insert(0, "Data: " + timestamp + "\n");
            saveLogToFile(logContent.toString());
        }
    }

    private static void saveLogToFile(String content) {
        Path logPath = Paths.get("../src/main/java/net/pawel/villagermod/logs/entities_log.txt");
        File logFile = logPath.toFile();

        File logDirectory = logFile.getParentFile();
        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
        }

        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(content + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
