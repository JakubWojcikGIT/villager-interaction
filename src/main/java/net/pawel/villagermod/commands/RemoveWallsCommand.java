package net.pawel.villagermod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RemoveWallsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("removewalls")
                .then(CommandManager.argument("width", IntegerArgumentType.integer())
                        .then(CommandManager.argument("height", IntegerArgumentType.integer())
                                .then(CommandManager.argument("depth", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int width = IntegerArgumentType.getInteger(context, "width");
                                            int height = IntegerArgumentType.getInteger(context, "height");
                                            int depth = IntegerArgumentType.getInteger(context, "depth");
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            if (player != null) {
                                                BlockPos playerPos = player.getBlockPos();
                                                Direction direction = player.getHorizontalFacing();
                                                removeWalls(player.getWorld(), playerPos, width, height, depth, direction);
                                            }
                                            return 1;
                                        })))));
    }

    private static void removeWalls(World world, BlockPos startPos, int width, int height, int depth, Direction direction) {
        int minX = startPos.getX();
        int minY = startPos.getY();
        int minZ = startPos.getZ();
        int maxX = minX + width - 1;
        int maxY = minY + height - 1;
        int maxZ = minZ + depth - 1;

        switch (direction) {
            case NORTH:
                minZ -= depth;
                maxZ = minZ + depth - 1;
                break;
            case SOUTH:
                maxZ += depth;
                break;
            case WEST:
                minX -= width;
                maxX = minX + width - 1;
                break;
            case EAST:
                maxX += width;
                break;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                }
            }
        }
    }
}