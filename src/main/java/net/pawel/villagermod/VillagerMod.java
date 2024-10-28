package net.pawel.villagermod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.pawel.villagermod.commands.KillEntitiesCommand;
import net.pawel.villagermod.entity.ModEntities;
import net.pawel.villagermod.entity.custom.ExtravertedVillagerEntity;
import net.pawel.villagermod.entity.custom.IntrovertedVillagerEntity;
import net.pawel.villagermod.events.EnemySpawnScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VillagerMod implements ModInitializer {
    public static final String MOD_ID = "villagermod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final EnemySpawnScheduler enemySpawnScheduler = new EnemySpawnScheduler();

    @Override
    public void onInitialize() {
        LOGGER.info("Villager AI Mod initialized");
        FabricDefaultAttributeRegistry.register(ModEntities.DIAMOND_VILLAGER, ExtravertedVillagerEntity.createDiamondVillagerAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.WOODEN_VILLAGER, IntrovertedVillagerEntity.createWoodenVillagerAttributes());

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> KillEntitiesCommand.register(dispatcher));

        ServerEntityEvents.ENTITY_LOAD.register(VillagerMod::preventSlimeSpawn);
    }

    private void onServerStarted(MinecraftServer server) {
        enemySpawnScheduler.start(server.getOverworld());
    }

    private void onServerStopped(MinecraftServer server) {
        enemySpawnScheduler.stop();
    }

     private static void preventSlimeSpawn(Entity entity, ServerWorld world) {
        if (entity.getType() == EntityType.SLIME) {
            entity.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}