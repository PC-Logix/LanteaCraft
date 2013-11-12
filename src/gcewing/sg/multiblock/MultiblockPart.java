package gcewing.sg.multiblock;

import gcewing.sg.util.Vector3;
import net.minecraft.tileentity.TileEntity;

/**
 * MultiblockPart acts as a root container for all multi-block structure,
 * tile-entities which act as multi-block structures can use these methods to
 * provide integration with multi-block structures.
 * 
 * @author AfterLifeLochie
 * 
 */
public abstract class MultiblockPart {

	protected boolean isClient = false;

	protected final TileEntity host;

	public MultiblockPart(TileEntity host) {
		this.host = host;
	}

	/**
	 * Called by the host tile-entity to tick this part.
	 */
	public void tick() {
		if (host.worldObj != null)
			isClient = host.worldObj.isRemote;
	}

	/**
	 * Called by the host multi-block part to get a reference to the
	 * GenericMultiblock instance in range. The GenericMultiblock may not always
	 * be linked to the structure, so local scanning should be implemented.
	 * 
	 * @param allowScanning
	 *            Flag set to disallow scanning for host multi-blocks.
	 * @return Any host GenericMultiblock objects in range, or null if there are
	 *         no hosts linked or in range of this block part.
	 */
	public abstract GenericMultiblock findHostMultiblock(boolean allowScanning);

	/**
	 * Called when the GenericMultiblock wants to test if this part can be added
	 * to a particular structure. If this part cannot be merged, returning false
	 * will indicate the GenericMultiblock should not attempt a merge.
	 * 
	 * @param structure
	 *            The structure to join with
	 * @return If the structure is legal, and, if this block object is capable
	 *         of merging.
	 */
	public abstract boolean canMergeWith(GenericMultiblock structure);

	/**
	 * Called when the GenericMultiblock wants to add this part to a particular
	 * structure. If this part is already engaged or cannot be merged, returning
	 * false should abort the merge of the entire structure.
	 * 
	 * @param structure
	 *            The structure to join with.
	 */
	public abstract boolean mergeWith(GenericMultiblock structure);

	/**
	 * Called anywhere to establish if this multi-block is part of a structure.
	 * This should not be used to test if a merge can be performed with a
	 * structure!
	 * 
	 * @return If this multi-block is already part of a structure.
	 */
	public abstract boolean isMerged();

	/**
	 * Called when the host GenericMultiblock wants to release this part from
	 * the currently held structure. If this part is engaged, the part should
	 * perform any disbanding operations, including releasing references to the
	 * structure. If this part is not engaged, this does nothing.
	 */
	public abstract void release();

	/**
	 * Called by anywhere to get the type of this multi-block part's type name.
	 * 
	 * @return The name of the multi-block part's name.
	 */
	public abstract String getType();

	/**
	 * Gets the absolute location of this part as a Vector3 location.
	 * 
	 * @return A Vector3 location of this part.
	 */
	public abstract Vector3 getVectorLoc();

	/**
	 * Called by the host multi-block part to devalidate the host, such as when
	 * the part is placed or removed, moved or otherwise deleted from the world.
	 * If no host is in range, this will do nothing.
	 */
	public void devalidateHostMultiblock() {
		GenericMultiblock structure = findHostMultiblock(true);
		if (structure != null)
			structure.invalidate();
	}

}
