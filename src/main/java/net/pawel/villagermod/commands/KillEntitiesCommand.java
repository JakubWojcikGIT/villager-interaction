package net.pawel.villagermod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

public class KillEntitiesCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("killentities")
                .then(CommandManager.argument("radius", IntegerArgumentType.integer(1))
                        .executes(context -> {
                            int radius = IntegerArgumentType.getInteger(context, "radius");
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = source.getPlayer();
                            if (player != null) {
                                Box box = new Box(player.getBlockPos()).expand(radius);
                                player.getWorld().getEntitiesByClass(net.minecraft.entity.Entity.class, box, entity -> !(entity instanceof ServerPlayerEntity)).forEach(Entity::kill);
                            }
                            return 1;
                        })));
    }
}