package net.pawel.villagermod.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pawel.villagermod.VillagerMod;
import net.pawel.villagermod.entity.custom.ExtravertedVillagerEntity;
import net.pawel.villagermod.entity.custom.IntrovertedVillagerEntity;

public class ModEntities {
    public static final EntityType<ExtravertedVillagerEntity> EXTRAVERTED_VILLAGER = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(VillagerMod.MOD_ID, "extraverted_villager"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ExtravertedVillagerEntity::new)
            .dimensions(EntityDimensions.fixed(1f, 2f)).build());
    public static final EntityType<IntrovertedVillagerEntity> INTROVERTED_VILLAGER = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(VillagerMod.MOD_ID, "introverted_villager"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, IntrovertedVillagerEntity::new)
            .dimensions(EntityDimensions.fixed(1f, 2f)).build());
}
