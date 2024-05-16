package pawel.villagermod.mixin;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void findTarget(CallbackInfo ci) {
        List<LivingEntity> visibleEntities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10), (livingEntity) -> true);
        for (LivingEntity visibleEntity : visibleEntities) {
            if (visibleEntity instanceof Vindicator) {
                System.out.println(this.getType() + " sees " + visibleEntity.getType());
            }
        }
    }
}