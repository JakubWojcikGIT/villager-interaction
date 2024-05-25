package pawel.villagermod.util;

import net.minecraft.world.entity.LivingEntity;
import pawel.villagermod.enums.VindicatorType;

public interface IVindicator {
    void updateVindicator();
    LivingEntity getLastHurtByMob();
    double distanceToSqr(LivingEntity entity);
    void setTarget(LivingEntity target);
    VindicatorType getVindicatorType();
}
