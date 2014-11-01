package lc.common.base;

import lc.api.defs.ILanteaCraftRenderer;
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

	/**
	 * Create a new rendering hook.
	 */
	public LCTileRenderHook() {
		this.registry = (DefinitionRegistry) LCRuntime.runtime.registries().definitions();
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		LCTile lct = (LCTile) tile;
		ILanteaCraftRenderer worker = registry.getRendererFor(RendererType.TILE, lct.getClass());
		if (worker != null && worker instanceof LCTileRenderer) {
			LCTileRenderer tileRenderer = (LCTileRenderer) worker;
			while (tileRenderer != null && !tileRenderer.renderTileEntityAt(lct, this, x, y, z, partialTickTime)) {
				worker = tileRenderer.getParent();
				if (worker == null || !(worker instanceof LCTileRenderer))
					break;
			}
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
