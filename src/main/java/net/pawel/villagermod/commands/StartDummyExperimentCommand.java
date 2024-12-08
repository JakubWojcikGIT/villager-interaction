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
import net.pawel.villagermod.utils.Allele;
import net.pawel.villagermod.utils.TraitType;

import java.util.Map;

public class StartDummyExperimentCommand {
    private static final EnemySpawnScheduler enemySpawnScheduler = new EnemySpawnScheduler();
    private static final EntityLogScheduler entityLogScheduler = new EntityLogScheduler();
    private static final int RECTANGLE_LENGTH = 10;
    private static final int RECTANGLE_HEIGHT = 2;
    private static final int RECTANGLE_WIDTH = 20;
    private static final int NUMBER_OF_VILLAGERS = 5;
    private static final int PERIOD = 120;

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

    // manualy assigned traits
    private static void summonDummyVillagers(ServerWorld world, BlockPos pos) {
        DummyVillager[] villagers = new DummyVillager[6];
        for (int i = 0; i < villagers.length; i++) {
            villagers[i] = new DummyVillager(ModEntities.DUMMY_VILLAGER, world);
            Map<TraitType, Allele> traits = villagers[i].villagerTraits.getTraits();
            traits.clear();
            if (i < villagers.length / 2) {
                traits.put(TraitType.AGGRESSION, new Allele('a', 'a'));
                traits.put(TraitType.AGILITY, new Allele('a', 'a'));
                traits.put(TraitType.SOCIABILITY, new Allele('a', 'a'));
                traits.put(TraitType.COURAGE, new Allele('a', 'a'));
                traits.put(TraitType.INTELLIGENCE, new Allele('a', 'a'));
                traits.put(TraitType.CURIOSITY, new Allele('a', 'a'));
                traits.put(TraitType.STRENGTH, new Allele('a', 'a'));
                traits.put(TraitType.LEADERSHIP, new Allele('a', 'a'));
                traits.put(TraitType.SPEED, new Allele('a', 'a'));
                traits.put(TraitType.NIGHT_VISION, new Allele('a', 'a'));
            } else {
                traits.put(TraitType.AGGRESSION, new Allele('A', 'A'));
                traits.put(TraitType.AGILITY, new Allele('A', 'A'));
                traits.put(TraitType.SOCIABILITY, new Allele('A', 'A'));
                traits.put(TraitType.COURAGE, new Allele('A', 'A'));
                traits.put(TraitType.INTELLIGENCE, new Allele('A', 'A'));
                traits.put(TraitType.CURIOSITY, new Allele('A', 'A'));
                traits.put(TraitType.STRENGTH, new Allele('A', 'A'));
                traits.put(TraitType.LEADERSHIP, new Allele('A', 'A'));
                traits.put(TraitType.SPEED, new Allele('A', 'A'));
                traits.put(TraitType.NIGHT_VISION, new Allele('A', 'A'));
            }
        }

        for (int i = 0; i < NUMBER_OF_VILLAGERS; i++) {
            BlockPos spawnPos = pos.add(world.random.nextInt(RECTANGLE_LENGTH - 3), 1, world.random.nextInt(RECTANGLE_WIDTH - 3));
            villagers[i % villagers.length].refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
            world.spawnEntity(villagers[i % villagers.length]);
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