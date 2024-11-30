package net.pawel.villagermod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class KillAndRemoveWallsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("killandremovewalls")
                .then(CommandManager.argument("radius", IntegerArgumentType.integer())
                        .executes(context -> {
                            int radius = IntegerArgumentType.getInteger(context, "radius");
                            ServerCommandSource source = context.getSource();
                            if (radius > 50) {
                                return 0;
                            }
                            ServerWorld world = source.getWorld();
                            BlockPos pos = BlockPos.ofFloored(source.getPosition());

                            killEntities(world, pos, radius);

                            removeWalls(world, pos, radius);

                            return 1;
                        })));
    }

    private static void killEntities(ServerWorld world, BlockPos center, int radius) {
        ServerPlayerEntity player = world.getRandomAlivePlayer();
        if (player != null) {
            Box box = new Box(player.getBlockPos()).expand(radius);
            player.getWorld().getEntitiesByClass(net.minecraft.entity.Entity.class, box, entity -> !(entity instanceof ServerPlayerEntity)).forEach(Entity::kill);
        }
    }

    private static void removeWalls(World world, BlockPos center, int radius) {
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