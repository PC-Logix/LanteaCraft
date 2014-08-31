package lc.common.base;

import lc.api.defs.ILanteaCraftRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public abstract class LCBlockRenderer implements ILanteaCraftRenderer {

	/**
	 * Get the parent renderer. Called when a render function cannot be
	 * completed by the current renderer.
	 * 
	 * @return The parent renderer.
	 */
	public abstract Class<? extends LCBlockRenderer> getParent();

	/**
	 * Render a block in the player's inventory. If this operation cannot be
	 * completed by the renderer, it must return {@link false}. If the rendering
	 * can be completed, the renderer must return {@link true}. If the rendering
	 * is not completed, the parent renderer will be called to render the block.
	 * 
	 * @param block
	 *            The block
	 * @param renderer
	 *            The RenderBlocks instance
	 * @param metadata
	 *            The block's metadata
	 * @return If the rendering was completed
	 * @see {@link LCBlockRenderer#getParent()}
	 */
	public abstract boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata);

	/**
	 * Render a block in the world. If this operation cannot be completed by the
	 * renderer, it must return {@link false}. If the rendering can be
	 * completed, the renderer must return {@link true}. If the rendering is not
	 * completed, the parent renderer will be called to render the block.
	 * 
	 * @param block
	 *            The block
	 * @param renderer
	 *            The RenderBlocks instance
	 * @param world
	 *            The world access instance
	 * @param x
	 *            The x-coordinate of the block
	 * @param y
	 *            The y-coordinate of the block
	 * @param z
	 *            The z-coordinate of the block
	 * @return If the rendering was completed
	 * @see {@link LCBlockRenderer#getParent()}
	 */
	public abstract boolean renderWorldBlock(Block block, RenderBlocks renderer, IBlockAccess world, int x, int y, int z);

	/**
	 * Asks if this block renderer renders blocks in 3D or 2D inside
	 * inventories.
	 * 
	 * @return If this block renderer renders blocks in 3D or 2D inside
	 *         inventories.
	 */
	public abstract boolean renderInventoryItemAs3d();

}
