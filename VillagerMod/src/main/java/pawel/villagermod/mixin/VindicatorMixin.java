package pawel.villagermod.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import pawel.villagermod.util.IVindicator;

import java.util.List;

@Mixin(Vindicator.class)
public abstract class VindicatorMixin extends AbstractIllager implements IVindicator {

    @Unique
    private int irritation_level = 0;

    protected VindicatorMixin(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
    }


    @Unique
    public double distanceToNearestVindicator() {
        double nearestDistance = 0.0D;
        List<LivingEntity> visibleEntities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10), (livingEntity) -> true);
        for (LivingEntity visibleEntity : visibleEntities) {
            if (visibleEntity instanceof IVindicator) {
                double distance = this.distanceToSqr(visibleEntity);
                if (nearestDistance == 0.0D || distance < nearestDistance) {
                    nearestDistance = distance;
                }
            }
        }
        System.out.println("Distance " + nearestDistance);

        return nearestDistance;
    }


    @Unique
    public void updateIrritation() {
        double v = distanceToNearestVindicator();
        if (v < 50.0D && v > 0.0D) {
            irritation_level++;
        } else if (v > 50.0D || v == 0.0D) {
            irritation_level--;
        }

        if (irritation_level < 0) {
            irritation_level = 0;
        }
    }


    @Override
    public void updateVindicator() {
        System.out.println("Irritation " + irritation_level);
        updateIrritation();
        List<LivingEntity> visibleEntities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10), (livingEntity) -> true);
        for (LivingEntity visibleEntity : visibleEntities) {
            if (this.equals(visibleEntity)) {
                continue;
            }
            if (visibleEntity instanceof Vindicator) {
                System.out.println(this.getType() + " sees " + visibleEntity.getType());
                this.setTarget(visibleEntity);
            }
        }
    }
}
