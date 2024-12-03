package net.pawel.villagermod.events;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
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

    public void start(ServerWorld world, BlockPos pos, int period) {
        if (isLogging(pos)) {
            return;
        }
        EntityLog.initializeLogFile();

        Runnable logTask = () -> {
            EntityLog.listEntitiesInRadius(world, pos, 50, true);
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
}