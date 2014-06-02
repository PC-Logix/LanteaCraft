package pcl.common.multiblock;

import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.common.network.ModPacket;
import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;

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

	protected EnumOrientations structureOrientation;
	protected HashMap<Object, MultiblockPart> structureParts = new HashMap<Object, MultiblockPart>();
	protected HashMap<String, Object> metadata = new HashMap<String, Object>();

	protected boolean wasInvalidated = false;
	protected boolean isValid = false;
	protected boolean isClient = false;
	protected boolean hasUpdate = false;

	protected TileEntity host;

	protected int xCoord;
	protected int yCoord;
	protected int zCoord;

	public GenericMultiblock(TileEntity host) {
		this.host = host;
	}

	/**
	 * Called by the host tile-entity to tick this structure.
	 */
	public void tick() {
		if (host.getWorldObj() != null)
			isClient = host.getWorldObj().isRemote;
		if (isClient)
			if (!hasUpdate) {
				ModPacket packet = pollForUpdate();
				LanteaCraft.getProxy().sendToServer(packet);
				hasUpdate = true;
			}
		// if the structure is currently flagged as invalid, and we're not a
		// client, attempt a validate
		if (wasInvalidated() && !isClient)
			validate(host.getWorldObj(), host.xCoord, host.yCoord, host.zCoord);
	}

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
	 * @param worldAccess
	 *            The world object
	 * @param baseX
	 *            The base x-coordinate in the world
	 * @param baseY
	 *            The base y-coordinate in the world
	 * @param baseZ
	 *            The base z-coordinate in the world
	 * @return If the collection of all blocks into the structure part map was a
	 *         success.
	 */
	public abstract boolean collectStructure(World worldAccess, int baseX, int baseY, int baseZ);

	/**
	 * Called by internal code to totally disband the structure, usually when
	 * transitioning from an assembled, valid state, to a non-valid block-only
	 * state.
	 */
	public abstract void freeStructure();

	/**
	 * Called by any code to get a particular part of the multi-block structure,
	 * referenced as a MultiblockPart object.
	 * 
	 * @param reference
	 *            The object, such as a coordinate or other value the
	 *            multi-block structure refers to the object as.
	 * @return The MultiblockPart object registered in the structure using the
	 *         reference, or, null if no such part exists.
	 */
	public abstract MultiblockPart getPart(Object reference);

	/**
	 * Called by any code to get all parts of the multi-block structure.
	 * 
	 * @return All MultiblockPart objects.
	 */
	public abstract MultiblockPart[] getAllParts();

	/**
	 * Called when the multi-block transitions from it's current state to the
	 * new state specified.
	 * 
	 * @param oldState
	 *            The old state of the structure.
	 * @param newState
	 *            The new state of the structure.
	 */
	public abstract void validated(boolean oldState, boolean newState);

	/**
	 * Called by any code to disband the structure immediately, usually when the
	 * host tile entity is being disposed or broken by something in the world.
	 */
	public abstract void disband();

	/**
	 * Packs the structure into a new ModPacket.
	 * 
	 * @return The packed multi-block structure data.
	 */
	public abstract ModPacket pack();

	/**
	 * Unpacks the structure data from a ModPacket.
	 * 
	 * @param packet
	 *            The packed multi-block structure data.
	 */
	public abstract void unpack(ModPacket packet);

	/**
	 * Requests an update packet from the server.
	 * 
	 * @return The update request packet.
	 */
	public abstract ModPacket pollForUpdate();

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
	protected void setLocation(int x, int y, int z) {
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}

	/**
	 * Gets the base location of this multi-block structure.
	 * 
	 * @return The base location of this multi-block structure.
	 */
	public Vector3 getLocation() {
		return new Vector3(xCoord, yCoord, zCoord);
	}

	/**
	 * Called by the host tile-entity to trigger a re-detection of the
	 * multi-block structure. This results in either a success, and the
	 * multi-block is 'merged', or fails and thus if any merge state is present,
	 * the multi-block disbands completely.
	 */
	public void validate(World world, int baseX, int baseY, int baseZ) {
		wasInvalidated = false;
		if (isValidStructure(world, baseX, baseY, baseZ)) {
			boolean wasValid = isValid();
			isValid = collectStructure(world, baseX, baseY, baseZ);
			if (isValid)
				setLocation(baseX, baseY, baseZ);
			validated(wasValid, isValid);
		} else if (isValid()) {
			freeStructure();
			isValid = false;
			validated(isValid(), false);
		}
	}

	/**
	 * Called by any child block when a new MultiblockPart is placed in or
	 * around the structure, or when a MultiblockPart is removed. The result of
	 * this triggers an expectation to a call to validate() from the host
	 * tile-entity object.
	 */
	public void invalidate() {
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
	 * Called in the client only to set the validity of this multi-block
	 * structure.
	 */
	public void setValid(World world, boolean b) {
		if (!isClient)
			return;
		isValid = b;
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

	public void setOrientation(EnumOrientations orientation) {
		if (!isClient)
			return;
		structureOrientation = orientation;
	}

	public void setMetadata(String name, Object o) {
		synchronized (metadata) {
			metadata.put(name, o);
		}
	}

	public Object getMetadata(String name) {
		synchronized (metadata) {
			return metadata.get(name);
		}
	}

	public void removeMetadata(String name) {
		synchronized (metadata) {
			metadata.remove(name);
		}
	}

	public TileEntity getTileEntity() {
		return host;
	}
}
