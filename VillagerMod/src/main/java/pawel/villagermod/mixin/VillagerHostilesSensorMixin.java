package pawel.villagermod.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pawel.villagermod.util.IVillager;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {

    @Inject(method = "isMatchingEntity", at = @At("HEAD"), cancellable = true)
    private void isMatchingEntityMixin(LivingEntity livingEntity, LivingEntity livingEntity2, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof Villager && livingEntity2 instanceof Villager ) {
            System.out.println(((IVillager) livingEntity).getIrritation_level());
            if (((IVillager) livingEntity).getIrritation_level() >= 100) {
                cir.setReturnValue(true);
            }
        }
    }
}
