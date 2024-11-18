package net.pawel.villagermod.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListEntitiesCommand {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

        Map<String, Object> logContent = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (entities.isEmpty()) {
            player.sendMessage(Text.literal("Brak stworzeń w promieniu " + radius + " kratek."), false);
            logContent.put("message", "Brak stworzeń w promieniu " + radius + " kratek.");
        } else {
            player.sendMessage(Text.literal("Stworzenia w promieniu " + radius + " kratek:"), false);
            logContent.put("message", "Stworzenia w promieniu " + radius + " kratek:");
            List<Map<String, Object>> villagersInfo = new ArrayList<>();

            for (Entity entity : entities) {
                if (entity instanceof VillagerAbstract villager) {
                    String entityName = villager.getType().getTranslationKey().strip().replace("entity.villagermod.", "");

                    Map<String, Object> villagerAttributes = new HashMap<>();
                    villagerAttributes.put("name", capitalize(entityName));
                    villagerAttributes.put("health", villager.getHealth());
                    villagerAttributes.put("maxHealth", villager.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH));
                    villagerAttributes.put("armor", villager.getAttributeValue(EntityAttributes.GENERIC_ARMOR));
                    villagerAttributes.put("attackDamage", villager.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
                    villagerAttributes.put("movementSpeed", villager.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                    villagerAttributes.put("attackSpeed", villager.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED));
                    villagerAttributes.put("attackKnockback", villager.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK));
                    villagerAttributes.put("followRange", villager.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE));
                    villagerAttributes.put("knockbackResistance", villager.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                    villagerAttributes.put("scale", villager.getScaleFactor());
                    villagerAttributes.put("isPrimal", villager.isPrimal());
                    villagerAttributes.put("aggressionTrait", (villager.getAggressionTrait() != null) ? villager.getAggressionTrait().toString() : "NaN");
                    villagerAttributes.put("agilityTrait", (villager.getAgilityTrait() != null) ? villager.getAgilityTrait().toString() : "NaN");
                    villagerAttributes.put("resilienceTrait", (villager.getResilienceTrait() != null) ? villager.getResilienceTrait().toString() : "NaN");
                    villagerAttributes.put("strengthTrait", (villager.getStrengthTrait() != null) ? villager.getStrengthTrait().toString() : "NaN");

                    villagersInfo.add(villagerAttributes);

                    String entityInfo = String.format(" - %s has attributes: %s",
                            capitalize(entityName), gson.toJson(villagerAttributes));

                    player.sendMessage(Text.literal(entityInfo), false);
                }
            }
            logContent.put("villagers", villagersInfo);
        }

        if (logToFile) {
            String timestamp = LocalDateTime.now().format(formatter);
            logContent.put("timestamp", timestamp);
            saveLogToFile(gson.toJson(logContent));
        }
    }

    private static void saveLogToFile(String content) {
        Path logPath = Paths.get("../src/main/java/net/pawel/villagermod/logs/entities_log.json");
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
