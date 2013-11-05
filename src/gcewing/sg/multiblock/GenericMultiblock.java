package gcewing.sg.multiblock;

import java.util.HashMap;

import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;

/**
 * GenericMultiblock acts as a global declaration for all multi-block structure
 * objects in the world and contains all linked MultiblockPart references. It
 * also expects host tile-entity objects to be responsible for calling methods
 * which keep the structure synchronized with parts and the world.
 * 
 * @author AfterLifeLochie
 * 
 */
public abstract class GenericMultiblock {

	private EnumOrientations structureOrientation;
	private HashMap<String, MultiblockPart> structureParts;

	private boolean wasInvalidated = false;
	private boolean isValid = false;

	private int xCoord;
	private int yCoord;
	private int zCoord;

	/**
	 * Determine if the current arrangement of blocks around the base is a valid
	 * multi-block structure. Implementations which return true will be expected
	 * to then collectStructure() and validate the multi-block arrangement.
	 * 
	 * @return If the current arrangement of blocks is a valid multi-block
	 *         structure.
	 */
	public abstract boolean isValidStructure(World worldAccess, int baseX, int baseY, int baseZ);

	/**
	 * Collects all multi-block parts into the generic multi-block base.
	 * 
	 * @return If the collection of all blocks into the structure part map was a
	 *         success.
	 */
	public abstract boolean collectStructure(World worldAccess, int baseX, int baseY, int baseZ);

	/**
	 * Called by any code to get a particular part of the multi-block structure,
	 * referenced as a MultiblockPart object.
	 * 
	 * @param reference
	 *            The name, such as a coordinate or other value the multi-block
	 *            structure refers to the object as.
	 * @return The MultiblockPart object registered in the structure using the
	 *         reference, or, null if no such part exists.
	 */
	public abstract MultiblockPart getPart(String reference);

	/**
	 * Called internally to set the base location of this multi-block structure.
	 * 
	 * @param x
	 *            The x-coordinate of the finalized structure
	 * @param y
	 *            The y-coordinate of the finalized structure
	 * @param z
	 *            The z-coordinate of the finalized structure
	 */
	private void setLocation(int x, int y, int z) {
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
	}

	/**
	 * Called by the host tile-entity to trigger a redetection of the
	 * multi-block structure. This results in either a success, and the
	 * multi-block is 'merged', or fails and thus if any merge state is present,
	 * the multi-block disbands completely.
	 */
	public void validate(World world, int baseX, int baseY, int baseZ) {
		wasInvalidated = false;
		if (isValidStructure(world, baseX, baseY, baseZ)) {
			isValid = collectStructure(world, baseX, baseY, baseZ);
			if (isValid())
				setLocation(baseX, baseY, baseZ);
		}
	}

	/**
	 * Called by any child block when a new MultiblockPart is placed in or
	 * around the structure, or when a MultiblockPart is removed. The result of
	 * this triggers an expectation to a call to validate() from the host
	 * tile-entity object.
	 */
	public void invalidate() {
		isValid = false;
		wasInvalidated = true;
	}

	/**
	 * Called by anywhere to get the valid state of the multi-block structure.
	 * 
	 * @return The valid state of the multi-block structure.
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * Called by anywhere to check the state of any call to invalidate().
	 * 
	 * @return If the multi-block has been flagged as invalidated by any other
	 *         code, this will be set. Namely, the host tile-entity polls this
	 *         to test if a validate call is required.
	 */
	public boolean wasInvalidated() {
		return wasInvalidated;
	}

	/**
	 * Gets the current orientation of the multi-block structure.
	 * 
	 * @return The EnumOrientations object.
	 */
	public EnumOrientations getOrientation() {
		return structureOrientation;
	}

}
