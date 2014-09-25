package lc.common.base;

import lc.api.defs.ILanteaCraftRenderer;

public abstract class LCTileRenderer implements ILanteaCraftRenderer {

	/**
	 * Get the parent renderer. Called when a render function cannot be
	 * completed by the current renderer.
	 * 
	 * @return The parent renderer.
	 */
	public abstract LCTileRenderer getParent();

	/**
	 * Render a tile-entity in the world. If this operation cannot be completed
	 * by the renderer, it must return <code>false</code>. If the rendering can
	 * be completed, the renderer must return <code>true</code>. If the
	 * rendering is not completed, the parent renderer will be called to render
	 * the block.
	 * 
	 * @param tile
	 *            The tile entity object
	 * @param x
	 *            The x-coordinate of the tile
	 * @param y
	 *            The y-coordinate of the tile
	 * @param z
	 *            The z-coordinate of the tile
	 * @param partialTickTime
	 *            The number of frames in this tick
	 * @return If the rendering was completed
	 * @see lc.common.base.LCBlockRenderer#getParent()
	 */
	public abstract boolean renderTileEntityAt(LCTile tile, double x, double y, double z, float partialTickTime);

}
