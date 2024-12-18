package net.pawel.villagermod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.pawel.villagermod.commands.*;
import net.pawel.villagermod.entity.ModEntities;
import net.pawel.villagermod.entity.custom.DummyVillager;
import net.pawel.villagermod.entity.custom.ExtravertedVillagerEntity;
import net.pawel.villagermod.entity.custom.IntrovertedVillagerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VillagerMod implements ModInitializer {
    public static final String MOD_ID = "villagermod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Villager AI Mod initialized");
        FabricDefaultAttributeRegistry.register(ModEntities.EXTRAVERTED_VILLAGER, ExtravertedVillagerEntity.createExtravertVillagerAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.INTROVERTED_VILLAGER, IntrovertedVillagerEntity.createIntrovertedVillagerAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.DUMMY_VILLAGER, DummyVillager.createDummyVillagerAttributes());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            KillEntitiesCommand.register(dispatcher);
            CreateWallsCommand.register(dispatcher);
            RemoveWallsCommand.register(dispatcher);
            SummonIntrovertsCommand.register(dispatcher);
            SummonExtravertsCommand.register(dispatcher);
            StartExperimentCommand.register(dispatcher);
            KillAndRemoveWallsCommand.register(dispatcher);
            ListEntitiesCommand.register(dispatcher);
            StartDummyExperimentCommand.register(dispatcher);
        });

        ServerEntityEvents.ENTITY_LOAD.register(VillagerMod::preventSlimeSpawn);
    }

    private static void preventSlimeSpawn(Entity entity, ServerWorld world) {
        if (entity.getType() == EntityType.SLIME) {
            entity.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}