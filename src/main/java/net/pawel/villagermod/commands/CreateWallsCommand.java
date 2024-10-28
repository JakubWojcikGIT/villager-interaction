package net.pawel.villagermod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CreateWallsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("createwalls")
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
                                                createWalls(player.getWorld(), playerPos, width, height, depth);
                                            }
                                            return 1;
                                        })))));
    }

    private static void createWalls(World world, BlockPos startPos, int width, int height, int depth) {
        int minX = startPos.getX();
        int minY = startPos.getY();
        int minZ = startPos.getZ();
        int maxX = minX + width - 1;
        int maxY = minY + height - 1;
        int maxZ = minZ + depth - 1;

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
}