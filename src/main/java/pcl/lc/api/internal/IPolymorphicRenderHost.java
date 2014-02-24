/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package pcl.lc.api.internal;

import net.minecraft.block.Block;

/**
 * Internal polymorphic block agent. Hook to a {@link TileEntity} only; only
 * changes the Block rendering, won't affect TESR code.
 * 
 * @author AfterLifeLochie
 */
public interface IPolymorphicRenderHost {

	/**
	 * Get the Block singleton which should be rendered in lieu of the currently
	 * assigned renderer for this Block. Does not affect TESR code.
	 * 
	 * @return The Block singleton.
	 */
	public Block getPolymorphicTargetBlock();

	/**
	 * Get the metadata value to provide the renderer for the Block singleton.
	 * 
	 * @return The metadata value.
	 */
	public int getPolymorphicTargetBlockMetadata();

}
