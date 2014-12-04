/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.rendering;

import net.minecraft.block.Block;

/**
 * Contract interface for tile entities which are capable of being 'painted'
 * with other blocks.
 * 
 * @author AfterLifeLochie
 *
 */
public interface IBlockSkinnable {

	/**
	 * Sets the skin block to the provided block and metadata.
	 * 
	 * @param block
	 *            The block singleton.
	 * @param metadata
	 *            If the block has no metadata, 0 should be provided.
	 */
	public void setSkinBlock(Block block, int metadata);

	/**
	 * Get the current skin block singleton.
	 * 
	 * @return The current skin block singleton.
	 */
	public Block getSkinBlock();

	/**
	 * Get the current skin block metadata.
	 * 
	 * @return The current skin block metadata, or 0 if the block has no
	 *         metadata.
	 */
	public int getSkinBlockMetadata();

}
