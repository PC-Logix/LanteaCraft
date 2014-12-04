/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.rendering;

import net.minecraft.world.IBlockAccess;

/**
 * Contract interface for block rendering information providers.
 *
 * @author AfterLifeLochie
 *
 */
public interface IBlockRenderInfo {

	/**
	 * Render the block in the world
	 *
	 * @param access
	 *            The world access
	 * @param data
	 *            The block data
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param z
	 *            The z-coordinate
	 * @return If the render was successful
	 */
	public boolean doWorldRender(IBlockAccess access, int data, int x, int y, int z);

	/**
	 * Determine if this item should be rendered in 3D in the inventory
	 *
	 * @param data
	 *            The metadata
	 * @return If the item should render as 3D in the inventory
	 */
	public boolean doInventoryRender(int data);

	/**
	 * Ask the renderer info if a render property should be observed or executed
	 * 
	 * @param property
	 *            The property name
	 *
	 * @param access
	 *            The world access
	 * @param data
	 *            The block data
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param z
	 *            The z-coordinate
	 * @param def
	 *            The default response if unknown
	 * @return If the property should be observed or executed.
	 */
	public boolean doProperty(String property, IBlockAccess access, int data, int x, int y, int z, boolean def);

}
