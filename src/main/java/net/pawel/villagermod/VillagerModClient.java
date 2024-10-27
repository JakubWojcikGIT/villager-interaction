package net.pawel.villagermod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.pawel.villagermod.entity.ModEntities;
import net.pawel.villagermod.entity.client.*;

public class VillagerModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.DIAMOND_VILLAGER, ExtravertedVillagerRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.DIAMOND_VILLAGER, ExtravertedVillagerModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.WOODEN_VILLAGER, IntrovertedVillagerRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WOODEN_VILLAGER, IntrovertedVillagerModel::getTexturedModelData);
    }
}
