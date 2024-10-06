package net.pawel.villagermod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.pawel.villagermod.VillagerMod;
import net.pawel.villagermod.entity.custom.WoodenVillagerEntity;

public class WoodenVillagerRenderer extends MobEntityRenderer<WoodenVillagerEntity, WoodenVillagerModel<WoodenVillagerEntity>> {
    private static final Identifier TEXTURE = new Identifier(VillagerMod.MOD_ID, "textures/entity/wooden_villager.png");

    public WoodenVillagerRenderer(EntityRendererFactory.Context context) {
        super(context, new WoodenVillagerModel<>(context.getPart(ModModelLayers.WOODEN_VILLAGER)), 0.6f);
    }

    @Override
    public Identifier getTexture(WoodenVillagerEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(WoodenVillagerEntity mobEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        if(mobEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}