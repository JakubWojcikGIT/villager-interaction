package net.pawel.villagermod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.ModEntities;
import net.pawel.villagermod.entity.custom.ExtravertedVillagerEntity;
import net.pawel.villagermod.entity.custom.IntrovertedVillagerEntity;
import net.pawel.villagermod.enums.TraitType;
import net.pawel.villagermod.events.EnemySpawnScheduler;
import net.pawel.villagermod.events.EntityLogScheduler;
import net.pawel.villagermod.utils.Allele;

import java.util.Map;

public class StartExperimentCommand {
    private static final EnemySpawnScheduler enemySpawnScheduler = new EnemySpawnScheduler();
    private static final EntityLogScheduler entityLogScheduler = new EntityLogScheduler();
    private static final int RECTANGLE_LENGTH = 10;
    private static final int RECTANGLE_HEIGHT = 2;
    private static final int RECTANGLE_WIDTH = 20;
    private static final int SEPARATION = 10;
    private static final int NUMBER_OF_VILLAGERS = 6;
    private static final int PERIOD = 120;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("start_experiment")
                .then(CommandManager.argument("waves", IntegerArgumentType.integer(1))
                .executes(context -> {
                    int waves = IntegerArgumentType.getInteger(context, "waves");
                    System.out.println("Executing start command with " + waves + " waves");
                    execute(context.getSource(), waves);
                    return 1;
                })));
    }

    public static void execute(ServerCommandSource source, int waves) {
        ServerWorld world = source.getWorld();
        BlockPos pos = BlockPos.ofFloored(source.getPosition());

        createWalls(world, pos);

        summonExtroverts(world, pos);

        summonIntroverts(world, pos.add(RECTANGLE_LENGTH + SEPARATION, 0, 0));

        startSpawningVindicators(world, pos, waves);

        startLogging(world, pos, waves);
    }

    private static void createWalls(ServerWorld world, BlockPos startPos) {
        createRectangle(world, startPos);

        BlockPos secondStartPos = startPos.add(RECTANGLE_LENGTH + SEPARATION, 0, 0);
        createRectangle(world, secondStartPos);
    }

    private static void createRectangle(World world, BlockPos startPos) {
        int minX = startPos.getX();
        int minY = startPos.getY();
        int minZ = startPos.getZ();
        int maxX = minX + RECTANGLE_LENGTH - 1;
        int maxY = minY + RECTANGLE_HEIGHT - 1;
        int maxZ = minZ + RECTANGLE_WIDTH - 1;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                world.setBlockState(new BlockPos(x, y, minZ), Blocks.STONE.getDefaultState());
                world.setBlockState(new BlockPos(x, y, maxZ), Blocks.STONE.getDefaultState());
            }
        }

        for (int z = minZ; z <= maxZ; z++) {
            for (int y = minY; y <= maxY; y++) {
                world.setBlockState(new BlockPos(minX, y, z), Blocks.STONE.getDefaultState());
                world.setBlockState(new BlockPos(maxX, y, z), Blocks.STONE.getDefaultState());
            }
        }
    }

    private static void summonExtroverts(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < NUMBER_OF_VILLAGERS; i++) {
            ExtravertedVillagerEntity extrovert = new ExtravertedVillagerEntity(ModEntities.EXTRAVERTED_VILLAGER, world);
            Map<TraitType, Allele> traits = extrovert.villagerTraits.getTraits();
            traits.clear();
            traits.put(TraitType.AGGRESSION, new Allele('a', 'a'));
            traits.put(TraitType.AGILITY, new Allele('a', 'a'));
            traits.put(TraitType.COURAGE, new Allele('a', 'a'));
            traits.put(TraitType.LEADERSHIP, new Allele('A', 'a'));
            traits.put(TraitType.SPEED, new Allele('A', 'a'));
            traits.put(TraitType.NIGHT_VISION, new Allele('A', 'a'));

            BlockPos spawnPos = pos.add(world.random.nextInt(RECTANGLE_LENGTH - 3), 1, world.random.nextInt(RECTANGLE_WIDTH - 3));
            extrovert.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
            world.spawnEntity(extrovert);
        }
    }

    private static void summonIntroverts(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < NUMBER_OF_VILLAGERS; i++) {
            IntrovertedVillagerEntity introvert = new IntrovertedVillagerEntity(ModEntities.INTROVERTED_VILLAGER, world);
            Map<TraitType, Allele> traits = introvert.villagerTraits.getTraits();
            traits.clear();
            traits.put(TraitType.AGGRESSION, new Allele('A', 'a'));
            traits.put(TraitType.AGILITY, new Allele('A', 'a'));
            traits.put(TraitType.COURAGE, new Allele('A', 'a'));
            traits.put(TraitType.LEADERSHIP, new Allele('a', 'a'));
            traits.put(TraitType.SPEED, new Allele('a', 'a'));
            traits.put(TraitType.NIGHT_VISION, new Allele('a', 'a'));

            BlockPos spawnPos = pos.add(world.random.nextInt(RECTANGLE_LENGTH - 3), 1, world.random.nextInt(RECTANGLE_WIDTH - 3));
            introvert.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
            world.spawnEntity(introvert);
        }
    }

    private static void startSpawningVindicators(ServerWorld world, BlockPos pos, int waves) {
        BlockPos firstRectanglePos = pos.add(RECTANGLE_LENGTH / 2, 1, RECTANGLE_WIDTH / 2);
        BlockPos secondRectanglePos = pos.add(RECTANGLE_LENGTH + SEPARATION + RECTANGLE_LENGTH / 2, 1, RECTANGLE_WIDTH / 2);

        enemySpawnScheduler.start(world, firstRectanglePos, PERIOD, waves);
        enemySpawnScheduler.start(world, secondRectanglePos, PERIOD, waves);
    }

    private static void startLogging(ServerWorld world, BlockPos pos, int waves) {
        entityLogScheduler.start(world, pos, PERIOD, waves);
    }
}