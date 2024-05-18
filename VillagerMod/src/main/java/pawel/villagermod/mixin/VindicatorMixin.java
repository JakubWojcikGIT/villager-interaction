package pawel.villagermod.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import pawel.villagermod.goals.CustomPanicGoal;
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
    public Vindicator getNearestVindicator() {
        Vindicator nearestVindicator = null;
        double nearestDistance = 0.0D;
        List<LivingEntity> visibleEntities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10), (livingEntity) -> true);
        for (LivingEntity visibleEntity : visibleEntities) {
            if (this.equals(visibleEntity)) {
                continue;
            }

            if (visibleEntity instanceof Vindicator) {
                double distance = this.distanceToSqr(visibleEntity);
                if (nearestDistance == 0.0D || distance < nearestDistance) {
                    nearestVindicator = (Vindicator) visibleEntity;
                    nearestDistance = distance;
                }
            }
        }
        return nearestVindicator;
    }


    @Unique
    public void updateIrritation() {
        LivingEntity nearestVindicator = getNearestVindicator();
        double v = 0;
        if (nearestVindicator != null) {
            v = this.distanceToSqr(nearestVindicator);
        }
        if (v < 50.0D && v > 0.0D) {
            irritation_level++;
        } else if (v > 50.0D || v == 0.0D) {
            irritation_level--;
        }
        if (irritation_level < 0) {
            irritation_level = 0;
        }
    }

    // nie dziala
    @Unique
    public void runWhenHighIrritation() {
        if (irritation_level > 100 && irritation_level < 200) {
            Vindicator nearestVindicator = getNearestVindicator();
            if (nearestVindicator != null) {
//                this.moveTo(2,2,2);
//                nearestVindicator.moveTo(-2,-2,-2);
//                CustomPanicGoal customPanicGoal = new CustomPanicGoal(this, 0.6D);
//                this.goalSelector.addGoal(0, customPanicGoal);
            }
        }
    }

    @Unique
    public void updateTarget() {
        Vindicator nearestVindicator = getNearestVindicator();
        if (nearestVindicator != null) {
            this.setTarget(nearestVindicator);
            nearestVindicator.setTarget(this);
        }
    }

    @Unique
    public void attackWhenVeryHighIrritation() {
        if (irritation_level > 200) {
            updateTarget();
        }
    }

    @Unique
    public void attackOtherClan() {

    }

    @Unique
    public void tryGetCloseToAnotherVindicator() {
        Vindicator nearestVindicator = getNearestVindicator();
        if (nearestVindicator != null && irritation_level < 100) {
            // dodac warunek ze szuka najbliższego vindicatora z tego samego klanu
            this.getNavigation().moveTo(nearestVindicator, 0.6D);
        }
    }

    @Override
    public void updateVindicator() {
        // zrobić tu switch case dla nawigaji poziomu irytacji
        System.out.println("Irritation " + irritation_level);
        updateIrritation();
        // dont work
        //runWhenHighIrritation();
        tryGetCloseToAnotherVindicator();
        attackWhenVeryHighIrritation();
        attackOtherClan();
    }
}
