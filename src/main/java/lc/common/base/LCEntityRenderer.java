package lc.common.base;

import lc.api.defs.IDefinitionReference;
import lc.api.defs.ILanteaCraftRenderer;
import lc.common.base.pipeline.LCEntityRenderPipeline;
import lc.common.impl.registry.DefinitionReference;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public abstract class LCEntityRenderer implements ILanteaCraftRenderer {

	@Override
	public IDefinitionReference ref() {
		return new DefinitionReference(this);
	}

	public abstract Class<? extends LCEntityRenderer> getParent();

	public abstract boolean doRender(LCEntityRenderPipeline render, Entity e, double rpx, double rpy, double rpz, float yaw, float frame);

	public abstract ResourceLocation getEntityTexture(LCEntityRenderPipeline render, Entity e);

}
