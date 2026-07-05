package com.pclogix.lanteacraft.client.render;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.entity.TokraTraderEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TokraTraderRenderer extends MobRenderer<TokraTraderEntity, VillagerModel<TokraTraderEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/entity/tokra_trader.png");

    public TokraTraderRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TokraTraderEntity entity) {
        return TEXTURE;
    }
}
