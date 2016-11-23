package lc.common.base.pipeline;

import lc.LCRuntime;
import lc.api.defs.ILanteaCraftRenderer;
import lc.client.render.fabs.DebugLayerTileRenderer;
import lc.client.render.fabs.DefaultTileRenderer;
import lc.common.LCLog;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderer;
import lc.common.impl.registry.DefinitionRegistry;
import lc.common.impl.registry.DefinitionRegistry.RendererType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * Tile entity rendering hook.
 *
 * @author AfterLifeLochie
 *
 */
public class LCTileRenderPipeline extends TileEntitySpecialRenderer {

	private DefinitionRegistry registry;
	private final DefaultTileRenderer defaultTileRenderer;
	private final DebugLayerTileRenderer debugTileRenderer;

	/**
	 * Create a new rendering hook.
	 */
	public LCTileRenderPipeline() {
		registry = (DefinitionRegistry) LCRuntime.runtime.registries().definitions();
		defaultTileRenderer = new DefaultTileRenderer();
		debugTileRenderer = new DebugLayerTileRenderer();
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		LCTile lct = (LCTile) tile;
		if (lct.shouldRender()) {
			boolean flag = true;
			ILanteaCraftRenderer worker = registry.getRendererFor(RendererType.TILE, lct.getClass());
			if (worker == null && !(worker instanceof LCTileRenderer))
				flag = false;
			else
				try {
					LCTileRenderer tileRenderer = (LCTileRenderer) worker;
					while (tileRenderer != null
							&& !tileRenderer.renderTileEntityAt(lct, this, x, y, z, partialTickTime)) {
						worker = tileRenderer.getParent();
						if (worker == null || !(worker instanceof LCTileRenderer)) {
							flag = false;
							break;
						}
					}
				} catch (Throwable t) {
					LCLog.warn("Uncaught tile rendering exception.", t);
					flag = false;
				}
			if (!flag)
				defaultTileRenderer.renderTileEntityAt(lct, this, x, y, z, partialTickTime);
		}

		try {
			debugTileRenderer.renderTileEntityAt(lct, this, x, y, z, partialTickTime);
		} catch (Exception e) {
			LCLog.warn("Uncaught debug layer tile rendering exception.", e);
		}
	}

	/**
	 * Bind a texture in OpenGL
	 *
	 * @param texture
	 *            The texture resource.
	 */
	public void bind(ResourceLocation texture) {
		bindTexture(texture);
	}

}
