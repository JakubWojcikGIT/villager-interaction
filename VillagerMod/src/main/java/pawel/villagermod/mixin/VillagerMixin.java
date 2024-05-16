package pawel.villagermod.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pawel.villagermod.util.IVillager;

import java.util.Optional;


@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements IVillager {
    @Shadow
    protected abstract SoundEvent getHurtSound(DamageSource damageSource);

    private int irritation_level = 0;

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    public double distanceToNearestVillager() {
        Optional<NearestVisibleLivingEntities> visibleEntitiesOpt = this.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        double nearestDistance = 0.0D;
        if (visibleEntitiesOpt.isPresent()) {
            NearestVisibleLivingEntities visibleEntities = visibleEntitiesOpt.get();
            Iterable<LivingEntity> allVisibleEntities = visibleEntities.findAll(livingEntity -> true);

            for (LivingEntity visibleEntity : allVisibleEntities) {
                if (visibleEntity instanceof Villager) {
                    double distance = this.distanceToSqr(visibleEntity);
                    if (nearestDistance == 0.0D || distance < nearestDistance) {
                        nearestDistance = distance;
                    }
                }
            }
        }
        return nearestDistance;
    }

    @Unique
    public void updateIrritation() {
        double v = distanceToNearestVillager();
        if (v < 50.0D && v > 0.0D) {
            irritation_level++;
        } else if (v > 50.0D) {
            irritation_level--;
        }

        if (irritation_level < 0 || irritation_level > 200) {
            irritation_level = 0;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void makePanic(CallbackInfo ci) {
        updateIrritation();
    }


    public int getIrritation_level() {
        return irritation_level;
    }

}
