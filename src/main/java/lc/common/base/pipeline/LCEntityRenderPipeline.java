package lc.common.base.pipeline;

import lc.LCRuntime;
import lc.api.defs.ILanteaCraftRenderer;
import lc.common.LCLog;
import lc.common.base.LCEntityRenderer;
import lc.common.impl.registry.DefinitionRegistry;
import lc.common.impl.registry.DefinitionRegistry.RendererType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class LCEntityRenderPipeline extends Render {
	private final DefinitionRegistry registry;

	public LCEntityRenderPipeline() {
		registry = (DefinitionRegistry) LCRuntime.runtime.registries().definitions();
	}

	public void useEntityTexture(Entity e) {
		this.bindEntityTexture(e);
	}

	@Override
	public void doRender(Entity e, double rpx, double rpy, double rpz, float yaw, float frame) {
		boolean flag = true;
		ILanteaCraftRenderer worker = registry.getRendererFor(RendererType.ENTITY, e.getClass());
		if (worker == null || !(worker instanceof LCEntityRenderer))
			flag = false;
		else {
			LCEntityRenderer entityRenderer = (LCEntityRenderer) worker;
			while (entityRenderer != null && !entityRenderer.doRender(this, e, rpx, rpy, rpz, yaw, frame)) {
				worker = registry.getRenderer(RendererType.ENTITY, entityRenderer.getParent());
				if (worker == null || !(worker instanceof LCEntityRenderer)) {
					flag = false;
					break;
				}
				entityRenderer = (LCEntityRenderer) worker;
			}
		}

		if (!flag)
			LCLog.warn("Unable to render entity class %s.", e.getClass());

	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		ILanteaCraftRenderer worker = registry.getRendererFor(RendererType.ENTITY, e.getClass());
		if (worker == null || !(worker instanceof LCEntityRenderer))
			return null;
		LCEntityRenderer entityRenderer = (LCEntityRenderer) worker;
		return entityRenderer.getEntityTexture(this, e);
	}

}
