package pawel.villagermod.mixin;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pawel.villagermod.util.IVindicator;

@Mixin(Raider.class)
public abstract class RaiderMixin extends LivingEntity {

    protected RaiderMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void findTarget(CallbackInfo ci) {
        if (this.getType() == EntityType.VINDICATOR) {
            IVindicator vindicator = (IVindicator) this;
            vindicator.updateVindicator();
        }
    }
}