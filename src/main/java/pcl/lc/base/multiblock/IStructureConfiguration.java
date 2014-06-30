package pcl.lc.base.multiblock;

import pcl.common.util.ImmutableTuple;

/**
 * Represents a configuration setup for a particular multiblock structure.
 * 
 * @author AfterLifeLochie
 * 
 */
public interface IStructureConfiguration {

	/**
	 * Get the absolute XYZ dimensions of the structure layout. The values of
	 * this tuple should be immutable and should represent the absolute
	 * dimensions of the structure. Dimensions should be real, positive numbers,
	 * such that the dimensions represent the magnitude of the structure's
	 * dimension vector.
	 * 
	 * @return The XYZ dimensions of the structure layout.
	 */
	public ImmutableTuple<Integer, Integer, Integer> getStructureDimensions();

	/**
	 * Get the absolute XYZ center of the structure layout. In the event the
	 * structure has a center orientation (eg, the center of the block is the
	 * middle of the structure), this should return a vector dimension from the
	 * normal (0, 0, 0) to the center of the structure. If the structure is
	 * baseline (has no center), return the normal instead.
	 * 
	 * @return The XYZ coordinate of the structure layout.
	 */
	public ImmutableTuple<Integer, Integer, Integer> getStructureCenter();

	/**
	 * Get the layout of the structure. Returns a three-dimensional collection
	 * of integers which represent identifiers, not block identifiers, for the
	 * structure. The number of entries in each dimension should match that of @link
	 * {@link IStructureConfiguration#getStructureDimensions()}.
	 * 
	 * @return The structure layout
	 */
	public int[][][] getStructureLayout();

	/**
	 * Get the mappings between IDs and Block objects
	 * 
	 * @return The mappings of the structure
	 */
	public BlockFilter[] getBlockMapping();

}
