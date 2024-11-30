package net.pawel.villagermod.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.ModEntities;
import net.pawel.villagermod.entity.custom.ExtravertedVillagerEntity;
import net.pawel.villagermod.entity.custom.IntrovertedVillagerEntity;
import net.pawel.villagermod.events.EnemySpawnScheduler;

public class StartExperimentCommand {
    private static final EnemySpawnScheduler enemySpawnScheduler = new EnemySpawnScheduler();
    private static final int RECTANGLE_LENGTH = 10;
    private static final int RECTANGLE_HEIGHT = 2;
    private static final int RECTANGLE_WIDTH = 20;
    private static final int SEPARATION = 10;
    private static final int NUMBER_OF_VILLAGERS = 5;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("start")
                .executes(context -> {
                    System.out.println("Executing start command");
                    execute(context.getSource());
                    return 1;
                }));
    }

    public static void execute(ServerCommandSource source) {
        ServerWorld world = source.getWorld();
        BlockPos pos = BlockPos.ofFloored(source.getPosition());

        createWalls(world, pos);

        summonExtroverts(world, pos);

        summonIntroverts(world, pos.add(RECTANGLE_LENGTH + SEPARATION, 0, 0));

        startSpawningVindicators(world, pos);
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
            BlockPos spawnPos = pos.add(world.random.nextInt(RECTANGLE_LENGTH - 2), 1, world.random.nextInt(RECTANGLE_WIDTH - 2));
            extrovert.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
            world.spawnEntity(extrovert);
        }
    }

    private static void summonIntroverts(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < NUMBER_OF_VILLAGERS; i++) {
            IntrovertedVillagerEntity introvert = new IntrovertedVillagerEntity(ModEntities.INTROVERTED_VILLAGER, world);
            BlockPos spawnPos = pos.add(world.random.nextInt(RECTANGLE_LENGTH - 2), 1, world.random.nextInt(RECTANGLE_WIDTH - 2));
            introvert.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
            world.spawnEntity(introvert);
        }
    }

    private static void startSpawningVindicators(ServerWorld world, BlockPos pos) {
        BlockPos firstRectanglePos = pos.add(RECTANGLE_LENGTH / 2, 1, RECTANGLE_WIDTH / 2);
        BlockPos secondRectanglePos = pos.add(RECTANGLE_LENGTH + SEPARATION + RECTANGLE_LENGTH / 2, 1, RECTANGLE_WIDTH / 2);

        enemySpawnScheduler.start(world, firstRectanglePos, 60);
        enemySpawnScheduler.start(world, secondRectanglePos, 60);
    }
}