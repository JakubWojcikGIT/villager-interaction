package net.pawel.villagermod.events;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EnemySpawnScheduler {
    private final Map<BlockPos, ScheduledFuture<?>> spawnPoints = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void start(ServerWorld world, BlockPos pos, int period) {
        if (isSpawning(pos)) {
            return;
        }

        Runnable spawnTask = () -> {
            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;
            if (world.isChunkLoaded(chunkX, chunkZ)) {
                VindicatorEntity vindicator = new VindicatorEntity(EntityType.VINDICATOR, world);
                vindicator.refreshPositionAndAngles(pos, 0.0F, 0.0F);
                world.spawnEntity(vindicator);
            }
        };

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(spawnTask, 0, period, TimeUnit.SECONDS);
        spawnPoints.put(pos, future);
    }

    public void stop(BlockPos pos) {
        ScheduledFuture<?> future = spawnPoints.remove(pos);
        if (future != null) {
            future.cancel(false);
        }
    }

    public boolean isSpawning(BlockPos pos) {
        return spawnPoints.containsKey(pos);
    }
}