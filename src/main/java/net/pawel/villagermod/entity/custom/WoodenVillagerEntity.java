package net.pawel.villagermod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.pawel.villagermod.enums.VillagerType;
import net.pawel.villagermod.entity.ModEntities;
import org.jetbrains.annotations.Nullable;


public class WoodenVillagerEntity extends VillagerAbstract {

    public WoodenVillagerEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world, VillagerType.WOODEN);
    }

    public static DefaultAttributeContainer.Builder createWoodenVillagerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_ARMOR, 0.5f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.WOODEN_VILLAGER.create(world);
    }
}
