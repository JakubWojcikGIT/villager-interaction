package net.pawel.villagermod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pawel.villagermod.entity.ModEntities;
import net.pawel.villagermod.entity.custom.IntrovertedVillagerEntity;

public class SummonIntrovertsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("summonintroverts")
                .then(CommandManager.argument("count", IntegerArgumentType.integer(1))
                        .executes(context -> {
                            int count = IntegerArgumentType.getInteger(context, "count");
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                World world = player.getWorld();
                                BlockPos pos = player.getBlockPos().offset(player.getHorizontalFacing(), 2);
                                for (int i = 0; i < count; i++) {
                                    IntrovertedVillagerEntity introvert = new IntrovertedVillagerEntity(ModEntities.INTROVERTED_VILLAGER, world);
                                    introvert.refreshPositionAndAngles(pos, 0.0F, 0.0F);
                                    world.spawnEntity(introvert);
                                }
                            }
                            return 1;
                        })));
    }
}