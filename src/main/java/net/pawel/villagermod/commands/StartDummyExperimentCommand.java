package net.pawel.villagermod.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.pawel.villagermod.entity.ModEntities;
import net.pawel.villagermod.entity.custom.DummyVillager;
import net.pawel.villagermod.events.*;

public class StartDummyExperimentCommand {
    private static final EnemySpawnScheduler enemySpawnScheduler = new EnemySpawnScheduler();
    private static final EntityLogScheduler entityLogScheduler = new EntityLogScheduler();
    private static final int RECTANGLE_LENGTH = 10;
    private static final int RECTANGLE_HEIGHT = 2;
    private static final int RECTANGLE_WIDTH = 20;
    private static final int NUMBER_OF_VILLAGERS = 5;
    private static final int PERIOD = 40;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("start_dummy_experiment")
                .executes(context -> {
                    System.out.println("Executing startdummyexperiment command");
                    execute(context.getSource());
                    return 1;
                }));
    }

    public static void execute(ServerCommandSource source) {
        ServerWorld world = source.getWorld();
        BlockPos pos = BlockPos.ofFloored(source.getPosition());

        createWall(world, pos);

        summonDummyVillagers(world, pos);

        startSpawningVindicators(world, pos);

        startLogging(world, pos);
    }

    private static void createWall(ServerWorld world, BlockPos startPos) {
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

    private static void summonDummyVillagers(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < NUMBER_OF_VILLAGERS; i++) {
            DummyVillager dummyVillager = new DummyVillager(ModEntities.DUMMY_VILLAGER, world);
            BlockPos spawnPos = pos.add(world.random.nextInt(RECTANGLE_LENGTH - 3), 1, world.random.nextInt(RECTANGLE_WIDTH - 3));
            dummyVillager.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
            world.spawnEntity(dummyVillager);
        }
    }

    private static void startSpawningVindicators(ServerWorld world, BlockPos pos) {
        BlockPos spawnPos = pos.add(RECTANGLE_LENGTH / 2, 1, RECTANGLE_WIDTH / 2);
        enemySpawnScheduler.start(world, spawnPos, PERIOD);
    }

    private static void startLogging(ServerWorld world, BlockPos pos) {
        entityLogScheduler.start(world, pos, PERIOD);
    }
}