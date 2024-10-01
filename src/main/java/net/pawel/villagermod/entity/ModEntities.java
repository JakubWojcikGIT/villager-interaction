package net.pawel.villagermod.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pawel.villagermod.VillagerMod;
import net.pawel.villagermod.entity.custom.DiamondVillagerEntity;
import net.pawel.villagermod.entity.custom.WoodenVillagerEntity;

public class ModEntities {
    public static final EntityType<DiamondVillagerEntity> DIAMOND_VILLAGER = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(VillagerMod.MOD_ID, "diamond_villager"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DiamondVillagerEntity::new)
            .dimensions(EntityDimensions.fixed(1f, 2f)).build());
    public static final EntityType<WoodenVillagerEntity> WOODEN_VILLAGER = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(VillagerMod.MOD_ID, "wooden_villager"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WoodenVillagerEntity::new)
            .dimensions(EntityDimensions.fixed(1f, 2f)).build());
}
