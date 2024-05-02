package pawel.villagermod.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;


@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    @Shadow public abstract Brain<Villager> getBrain();

    @Shadow public abstract void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt);

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);

    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void makePanic(CallbackInfo ci) {
        Optional<NearestVisibleLivingEntities> visibleEntitiesOpt = this.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

        if (visibleEntitiesOpt.isPresent()) {
            NearestVisibleLivingEntities visibleEntities = visibleEntitiesOpt.get();
            Iterable<LivingEntity> allVisibleEntities = visibleEntities.findAll(livingEntity -> true);

            for (LivingEntity visibleEntity : allVisibleEntities) {
                System.out.println(visibleEntity);
            }
        }
    }
}
