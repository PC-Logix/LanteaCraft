package lc.common.base;

import lc.api.defs.ILanteaCraftRenderer;
import lc.client.DefaultTileRenderer;
import lc.common.LCLog;
import lc.common.impl.registry.DefinitionRegistry;
import lc.common.impl.registry.DefinitionRegistry.RendererType;
import lc.core.LCRuntime;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * Tile entity rendering hook.
 *
 * @author AfterLifeLochie
 *
 */
public class LCTileRenderHook extends TileEntitySpecialRenderer {

	private DefinitionRegistry registry;
	private final DefaultTileRenderer defaultTileRenderer;

	/**
	 * Create a new rendering hook.
	 */
	public LCTileRenderHook() {
		registry = (DefinitionRegistry) LCRuntime.runtime.registries().definitions();
		defaultTileRenderer = new DefaultTileRenderer();
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		boolean flag = true;
		LCTile lct = (LCTile) tile;
		ILanteaCraftRenderer worker = registry.getRendererFor(RendererType.TILE, lct.getClass());
		if (worker == null && !(worker instanceof LCTileRenderer))
			flag = false;
		else {
			try {
				LCTileRenderer tileRenderer = (LCTileRenderer) worker;
				while (tileRenderer != null && !tileRenderer.renderTileEntityAt(lct, this, x, y, z, partialTickTime)) {
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
		}
		if (!flag)
			defaultTileRenderer.renderTileEntityAt(lct, this, x, y, z, partialTickTime);
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
