package pawel.villagermod.goals;

import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.monster.Vindicator;
import pawel.villagermod.mixin.VindicatorMixin;
import pawel.villagermod.util.IVindicator;

public class CustomPanicGoal extends PanicGoal {
    private final IVindicator vindicator;

    public CustomPanicGoal(IVindicator vindicator, double speedModifier) {
        super((Vindicator)vindicator, speedModifier);
        this.vindicator = vindicator;
    }

    @Override
    public boolean canUse() {
        return true;
    }
}
