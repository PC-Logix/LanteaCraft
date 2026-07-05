package com.pclogix.lanteacraft.client.render;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.client.model.GoauldSoldierModel;
import com.pclogix.lanteacraft.entity.GoauldSoldierEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoauldSoldierRenderer extends HumanoidMobRenderer<GoauldSoldierEntity, GoauldSoldierModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/entity/goauld_soldier.png");
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "goauld_soldier"),
            "main");

    public GoauldSoldierRenderer(EntityRendererProvider.Context context) {
        super(context, new GoauldSoldierModel(context.bakeLayer(MODEL_LAYER)), 0.55F);
        addLayer(new GoauldObjArmorLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(GoauldSoldierEntity entity) {
        return TEXTURE;
    }
}
