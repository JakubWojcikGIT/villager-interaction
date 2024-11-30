package net.pawel.villagermod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pawel.villagermod.VillagerMod;

public class StartSpawningCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("startspawning")
                .then(CommandManager.argument("x", IntegerArgumentType.integer())
                        .then(CommandManager.argument("y", IntegerArgumentType.integer())
                                .then(CommandManager.argument("z", IntegerArgumentType.integer())
                                        .then(CommandManager.argument("period", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    int x = IntegerArgumentType.getInteger(context, "x");
                                                    int y = IntegerArgumentType.getInteger(context, "y");
                                                    int z = IntegerArgumentType.getInteger(context, "z");
                                                    int period = IntegerArgumentType.getInteger(context, "period");
                                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        World world = player.getWorld();
                                                        BlockPos pos = new BlockPos(x, y, z);
                                                        VillagerMod.getEnemySpawnScheduler().start((ServerWorld) world, pos, period);
                                                    }
                                                    return 1;
                                                }))))));
    }
}