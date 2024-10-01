package net.pawel.villagermod.entity.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.pawel.villagermod.VillagerMod;

public class ModModelLayers {
    public static final EntityModelLayer DIAMOND_VILLAGER =
            new EntityModelLayer(new Identifier(VillagerMod.MOD_ID, "diamond_villager"), "main");
    public static final EntityModelLayer WOODEN_VILLAGER =
            new EntityModelLayer(new Identifier(VillagerMod.MOD_ID, "wooden_villager"), "main");
}
