package net.pawel.villagermod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.pawel.villagermod.entity.ModEntities;
import net.pawel.villagermod.entity.custom.DiamondVillagerEntity;
import net.pawel.villagermod.entity.custom.WoodenVillagerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VillagerMod implements ModInitializer {
    public static final String MOD_ID = "villagermod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Villager AI Mod initialized");
        FabricDefaultAttributeRegistry.register(ModEntities.DIAMOND_VILLAGER, DiamondVillagerEntity.createDiamondVillagerAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.WOODEN_VILLAGER, WoodenVillagerEntity.createWoodenVillagerAttributes());
    }
}