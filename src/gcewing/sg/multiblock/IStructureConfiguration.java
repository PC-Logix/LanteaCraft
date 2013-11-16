package gcewing.sg.multiblock;

import net.minecraft.block.Block;
import gcewing.sg.util.ImmutableTuple;

/**
 * Represents a configuration setup for a particular multiblock structure.
 * 
 * @author AfterLifeLochie
 * 
 */
public interface IStructureConfiguration {

	/**
	 * Get the absolute XYZ dimensions of the structure layout.
	 * 
	 * @return The XYZ dimensions of the structure layout.
	 */
	public ImmutableTuple<Integer, Integer, Integer> getStructureDimensions();

	/**
	 * Get the absolute XYZ center of the structure layout.
	 * 
	 * @return The XYZ coordinate of the structure layout.
	 */
	public ImmutableTuple<Integer, Integer, Integer> getStructureCenter();

	/**
	 * Get the layout of the structure. Return IDs, not Blocks.
	 * 
	 * @return The structure layout
	 */
	public int[][][] getStructureLayout();

	/**
	 * Get the mappings between IDs and Block objects
	 * 
	 * @return The mappings of the structure
	 */
	public Block[] getBlockMapping();

}
