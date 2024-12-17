package net.pawel.villagermod.events;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.pawel.villagermod.utils.EntityLog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EntityLogScheduler {
    private final Map<BlockPos, ScheduledFuture<?>> logPoints = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int waveCount = 0;

    public void start(ServerWorld world, BlockPos pos, int period, int maxWaves) {
        if (isLogging(pos)) {
            return;
        }
        EntityLog.initializeLogFile();

        Runnable logTask = () -> {
            if (waveCount < maxWaves) {
                EntityLog.listEntitiesInRadius(world, pos, 50, true);
                waveCount++;
            } else {
                killEntitiesInRange(world, pos, 50);
                removeWalls(world, pos, 40);
                stop(pos);
            }
        };

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(logTask, 0, period, TimeUnit.SECONDS);
        logPoints.put(pos, future);
    }

    public void stop(BlockPos pos) {
        ScheduledFuture<?> future = logPoints.remove(pos);
        if (future != null) {
            future.cancel(false);
        }
    }

    public boolean isLogging(BlockPos pos) {
        return logPoints.containsKey(pos);
    }

    private void killEntitiesInRange(ServerWorld world, BlockPos center, int radius) {
        Box box = new Box(center).expand(radius);
        world.getEntitiesByClass(Entity.class, box, entity -> !(entity instanceof PlayerEntity)).forEach(Entity::kill);
    }

    private void removeWalls(ServerWorld world, BlockPos center, int radius) {
        int minX = center.getX() - radius;
        int maxX = center.getX() + radius;
        int minY = center.getY() - radius;
        int maxY = center.getY() + radius;
        int minZ = center.getZ() - radius;
        int maxZ = center.getZ() + radius;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (world.getBlockState(pos).getBlock() == Blocks.STONE) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }
}