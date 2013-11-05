package gcewing.sg.multiblock;

/**
 * MultiblockPart acts as a root container for all multi-block structure,
 * tile-entities which act as multi-block structures can use these methods to
 * provide integration with multi-block structures.
 * 
 * @author AfterLifeLochie
 * 
 */
public abstract class MultiblockPart {

	/**
	 * Called by the host multi-block part to get a reference to the
	 * GenericMultiblock instance in range. The GenericMultiblock may not always
	 * be linked to the structure, so local scanning should be implemented.
	 * 
	 * @return Any host GenericMultiblock objects in range, or null if there are
	 *         no hosts linked or in range of this block part.
	 */
	public abstract GenericMultiblock findHostMultiblock();

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
	 * Called when the host GenericMultiblock wants to release this part from
	 * the currently held structure. If this part is engaged, the part should
	 * perform any disbanding operations, including releasing references to the
	 * structure. If this part is not engaged, this does nothing.
	 */
	public abstract void release();

	/**
	 * Called by the host multi-block part to devalidate the host, such as when
	 * the part is placed or removed, moved or otherwise deleted from the world.
	 * If no host is in range, this will do nothing.
	 */
	public void devalidateHostMultiblock() {
		GenericMultiblock structure = findHostMultiblock();
		if (structure != null)
			structure.invalidate();
	}

}
