package net.pawel.villagermod.events;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Timer;
import java.util.TimerTask;

public class EnemySpawnScheduler {

    private final Timer timer = new Timer();

    public void start(World world) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (world instanceof ServerWorld) {
                    spawnEnemies((ServerWorld) world);
                }
            }
        }, 0, 100000);
    }

    private void spawnEnemies(ServerWorld world) {
        BlockPos spawnPos = new BlockPos(world.getSpawnPos());
        for (int i = 0; i < 5; i++) {
            // spawn 5 enemies form standart minecraft entity
            VindicatorEntity enemy = new VindicatorEntity(EntityType.VINDICATOR, world);
            enemy.refreshPositionAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), world.random.nextFloat() * 360.0F, 0.0F);
            world.spawnEntity(enemy);
        }
    }

    public void stop() {
        timer.cancel();
    }
}