package pawel.villagermod.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pawel.villagermod.enums.VindicatorType;
import pawel.villagermod.util.IVindicator;

import java.util.List;
import java.util.Random;

@Mixin(Vindicator.class)
public abstract class VindicatorMixin extends AbstractIllager implements IVindicator {
    @Unique
    private int irritation_level = 0;
    @Unique
    private static Double AD = 5.0D;
    @Unique
    private static final Double maxHealth = 100.0D;
    @Unique
    private static final Double MS = 0.3499999940395355D;
    @Unique
    private VindicatorType vindicatorType;


    protected VindicatorMixin(EntityType<? extends AbstractIllager> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void modifyAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        builder.add(Attributes.MOVEMENT_SPEED, MS)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, maxHealth)
                .add(Attributes.ATTACK_DAMAGE, AD);
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
        if (isFighting()) {
            irritation_level = 0;
            return;
        }
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



    @Unique
    public void attackWhenVeryHighIrritation() {
        Vindicator nearestVindicator = getNearestVindicator();
        if (nearestVindicator != null) {
            this.setTarget(nearestVindicator);
            nearestVindicator.setTarget(this);
        }
    }

    @Unique
    public void attackOtherClan() {
        List<LivingEntity> visibleEntities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10), (livingEntity) -> true);
        for (LivingEntity visibleEntity : visibleEntities) {
            if (this.equals(visibleEntity)) {
                continue;
            }

            if (visibleEntity instanceof Vindicator vindicator) {
                if (((IVindicator) vindicator).getVindicatorType() != this.getVindicatorType()) {
                    this.setTarget(vindicator);
                    vindicator.setTarget(this);
                }
            }
        }
    }

    @Unique
    public void tryGetCloseToAnotherVindicator() {
        Vindicator nearestVindicator = getNearestVindicator();
        if (nearestVindicator != null) {
            this.getNavigation().moveTo(nearestVindicator, 0.6D);
        }
    }

    @Override
    public void updateVindicator() {
        System.out.println("Irritation " + irritation_level);
        updateIrritation();
        attackOtherClan();
        if (irritation_level < 100) {
            tryGetCloseToAnotherVindicator();
        }  else if (irritation_level > 200) {
            attackWhenVeryHighIrritation();
        }
    }

    @Override
    public void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance) {
        if (this.getCurrentRaid() == null) {
            vindicatorType = getRandomVindicatorType();
            ItemStack banner;
            switch (vindicatorType) {
                case VINDICATOR_WOOD:
                    AD = 13.0D;
                    ItemStack woodenAxe = new ItemStack(Items.WOODEN_AXE);
                    banner = new ItemStack(Items.BROWN_BANNER);
                    this.setItemSlot(EquipmentSlot.MAINHAND, woodenAxe);
                    this.setItemSlot(EquipmentSlot.HEAD, banner);
                    this.setCustomName(Component.nullToEmpty(vindicatorType.toString()));
                    break;
                case VINDICATOR_GOLD:
                    AD = 13.0D;
                    ItemStack goldenAxe = new ItemStack(Items.GOLDEN_AXE);
                    banner = new ItemStack(Items.YELLOW_BANNER);
                    this.setItemSlot(EquipmentSlot.MAINHAND, goldenAxe);
                    this.setItemSlot(EquipmentSlot.HEAD, banner);
                    this.setCustomName(Component.nullToEmpty(vindicatorType.toString()));
                    break;
                case VINDICATOR_IRON:
                    AD = 11.0D;
                    ItemStack ironAxe = new ItemStack(Items.IRON_AXE);
                    banner = new ItemStack(Items.CYAN_BANNER);
                    this.setItemSlot(EquipmentSlot.MAINHAND, ironAxe);
                    this.setItemSlot(EquipmentSlot.HEAD, banner);
                    this.setCustomName(Component.nullToEmpty(vindicatorType.toString()));
                    break;
                case VINDICATOR_NETHERITE:
                    AD = 10.0D;
                    ItemStack netheriteAxe = new ItemStack(Items.NETHERITE_AXE);
                    banner = new ItemStack(Items.BLACK_BANNER);
                    this.setItemSlot(EquipmentSlot.MAINHAND, netheriteAxe);
                    this.setItemSlot(EquipmentSlot.HEAD, banner);
                    this.setCustomName(Component.nullToEmpty(vindicatorType.toString()));
                    break;
            }
        }
    }

    @Unique
    private static VindicatorType getRandomVindicatorType() {
        VindicatorType[] types = VindicatorType.values();
        Random random = new Random();
        return types[random.nextInt(types.length)];
    }

    @Override
    public double distanceToSqr(LivingEntity entity) {
        double distance = this.distanceTo(entity);
        return distance * distance;
    }

    @Unique
    public boolean isFighting() {
        return this.getTarget() != null;
    }

    @Unique
    public VindicatorType getVindicatorType() {
        return vindicatorType;
    }
}
