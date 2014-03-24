package pcl.lc.multiblock;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.common.multiblock.EnumOrientations;
import pcl.common.multiblock.GenericMultiblock;
import pcl.common.multiblock.MultiblockPart;
import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import pcl.common.util.ImmutablePair;
import pcl.common.util.Vector3;
import pcl.common.util.WorldLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumStargateState;
import pcl.lc.core.WorldLog;
import pcl.lc.tileentity.TileEntityStargateRing;

public class StargateMultiblock extends GenericMultiblock {

	private int rotation;
	private int watcher = 0;
	private boolean watcherLast = false;
	private boolean modified = false;

	private int[][] stargateModel = { { 1, 2, 1, 3, 1, 2, 1 }, { 2, 0, 0, 0, 0, 0, 2 }, { 1, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 1 }, { 2, 0, 0, 0, 0, 0, 2 }, { 1, 0, 0, 0, 0, 0, 1 }, { 1, 2, 1, 2, 1, 2, 1 } };

	public StargateMultiblock(TileEntity host) {
		super(host);
	}

	@Override
	public void tick() {
		super.tick();
		if (!isClient && modified) {
			modified = !modified;
			host.getDescriptionPacket();
			WorldLog log = LanteaCraft.getProxy().getWorldLog();
			log.log(Level.INFO,
					String.format("Stargate state changed to %s at (%s %s %s).", isValid, xCoord, yCoord, zCoord));
		}

		// People keep putting blocks inside Stargates after they're formed,
		// this
		// isn't okay. Now we're going to watch what they do, so if they do, the
		// gate might explode violently.
		if (!isClient)
			if (0 > watcher) {
				watcher += 20;
				boolean result = isValidStructure(host.worldObj, host.xCoord, host.yCoord, host.zCoord);
				if (result != watcherLast) {
					watcherLast = result;
					invalidate();
				}
			} else
				watcher--;
	}

	/**
	 * Gets the Stargate's base-block rotation value.
	 * 
	 * @return The rotation integer.
	 */
	public int getRotation() {
		return rotation;
	}

	/**
	 * Sets the Stargate's base-block rotation value.
	 * 
	 * @param rotation
	 *            The rotation integer.
	 */
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	/**
	 * Gets the number of parts in this Stargate
	 * 
	 * @return The number of parts
	 */
	public int getPartCount() {
		return structureParts.size();
	}

	/**
	 * Determines if the passed tile entity object is any valid Stargate part
	 * 
	 * @param entity
	 *            The part object
	 * @return If the tile entity part object is a Stargate part
	 */
	private boolean isGateTileEntity(TileEntity entity) {
		if (entity instanceof TileEntityStargateRing)
			return true;
		return false;
	}

	/**
	 * Calculates an orientation to test at a given coordinate set in a world
	 * 
	 * @param worldAccess
	 *            The world to test
	 * @param baseX
	 *            The x-coordinate of the base
	 * @param baseY
	 *            The y-coordinate of the base
	 * @param baseZ
	 *            The z-coordinate of the base
	 * @return Any valid gate orientation, or null if no valid orientation is
	 *         found
	 */
	private EnumOrientations getOrientation(World worldAccess, int baseX, int baseY, int baseZ) {
		// Test North-South alignment along Z axis
		if (isGateTileEntity(worldAccess.getBlockTileEntity(baseX, baseY, baseZ + 1))
				&& isGateTileEntity(worldAccess.getBlockTileEntity(baseX, baseY, baseZ - 1)))
			return EnumOrientations.NORTH_SOUTH;

		// Test East-West alignment along X axis
		if (isGateTileEntity(worldAccess.getBlockTileEntity(baseX + 1, baseY, baseZ))
				&& isGateTileEntity(worldAccess.getBlockTileEntity(baseX - 1, baseY, baseZ)))
			return EnumOrientations.EAST_WEST;

		// Likely not a valid orientation at all
		return null;
	}

	@Override
	public boolean isValidStructure(World worldAccess, int baseX, int baseY, int baseZ) {
		EnumOrientations currentOrientation = getOrientation(worldAccess, baseX, baseY, baseZ);

		// North-South means the gate is aligned along X
		if (currentOrientation == EnumOrientations.EAST_WEST) {
			LanteaCraft.getLogger().log(Level.FINE, "Testing EASTWEST");
			for (int y = 0; y < 7; y++)
				for (int x = 0; x < 7; x++) {
					int blockId = worldAccess.getBlockId(baseX + (x - 3), baseY + y, baseZ);
					TileEntity entity = worldAccess.getBlockTileEntity(baseX + (x - 3), baseY + y, baseZ);
					if (!testIsValidForExpected(entity, blockId, stargateModel[y][x]))
						return false;
				}
			return true;
		}

		// East-West means the gate is aligned along Z
		if (currentOrientation == EnumOrientations.NORTH_SOUTH) {
			LanteaCraft.getLogger().log(Level.FINE, "Testing NORTHSOUTH");
			for (int y = 0; y < 7; y++)
				for (int z = 0; z < 7; z++) {
					int blockId = worldAccess.getBlockId(baseX, baseY + y, baseZ + (z - 3));
					TileEntity entity = worldAccess.getBlockTileEntity(baseX, baseY + y, baseZ + (z - 3));
					if (!testIsValidForExpected(entity, blockId, stargateModel[y][z]))
						return false;
				}
			return true;
		}

		// Likely not a valid orientation at all
		return false;
	}

	private boolean testIsValidForExpected(TileEntity entity, int blockId, int expectedType) {
		if (expectedType == 0)
			if (blockId != 0 || entity != null)
				return false;
		if (expectedType == 1 || expectedType == 2) {
			if (!(entity instanceof TileEntityStargateRing))
				return false;
			TileEntityStargateRing entityAsRing = (TileEntityStargateRing) entity;
			StargatePart teAsPart = null;
			teAsPart = entityAsRing.getAsPart();
			if (expectedType == 1) {
				if (teAsPart.getType() == null || !teAsPart.getType().equals("partStargateBlock"))
					return false;
				if (!teAsPart.canMergeWith(this))
					return false;
			}
			if (expectedType == 2) {
				if (teAsPart.getType() == null || !teAsPart.getType().equals("partStargateChevron"))
					return false;
				if (!teAsPart.canMergeWith(this))
					return false;
			}
		}
		return true;
	}

	@Override
	public boolean collectStructure(World worldAccess, int baseX, int baseY, int baseZ) {
		EnumOrientations currentOrientation = getOrientation(worldAccess, baseX, baseY, baseZ);

		// North-South means the gate is aligned along X
		if (currentOrientation == EnumOrientations.EAST_WEST) {
			LanteaCraft.getLogger().log(Level.FINE, "Globbing EASTWEST");
			for (int x = 0; x < 7; x++)
				for (int y = 0; y < 7; y++) {
					TileEntity entity = worldAccess.getBlockTileEntity(baseX + (x - 3), baseY + y, baseZ);
					if (stargateModel[y][x] != 0 && stargateModel[y][x] != 3) {
						TileEntityStargateRing entityAsRing = (TileEntityStargateRing) entity;
						StargatePart teAsPart = entityAsRing.getAsPart();
						if (!teAsPart.canMergeWith(this))
							return false;
						teAsPart.mergeWith(this);
						structureParts.put(new ImmutablePair<Integer, Integer>(x, y), teAsPart);
					}
				}
			LanteaCraft.getLogger().log(Level.FINE, "Merged in orientation EAST-WEST OK");
			structureOrientation = EnumOrientations.EAST_WEST;
			modified = true;
			return true;
		}

		// East-West means the gate is aligned along Z
		if (currentOrientation == EnumOrientations.NORTH_SOUTH) {
			LanteaCraft.getLogger().log(Level.FINE, "Globbing NORTHSOUTH");
			for (int z = 0; z < 7; z++)
				for (int y = 0; y < 7; y++) {
					TileEntity entity = worldAccess.getBlockTileEntity(baseX, baseY + y, baseZ + (z - 3));
					if (stargateModel[y][z] != 0 && stargateModel[y][z] != 3) {
						TileEntityStargateRing entityAsRing = (TileEntityStargateRing) entity;
						StargatePart teAsPart = entityAsRing.getAsPart();
						if (!teAsPart.canMergeWith(this))
							return false;
						teAsPart.mergeWith(this);
						structureParts.put(new ImmutablePair<Integer, Integer>(z, y), teAsPart);
					}
				}
			LanteaCraft.getLogger().log(Level.FINE, "Merged in orientation NORTH-SOUTH OK");
			structureOrientation = EnumOrientations.NORTH_SOUTH;
			modified = true;
			return true;
		}

		// Likely not a valid orientation at all
		LanteaCraft.getLogger().log(Level.FINE, "Weird orientation result!");
		return false;
	}

	@Override
	public void freeStructure() {
		LanteaCraft.getLogger().log(Level.INFO,
				((isClient) ? "[client]" : "[server]") + " Releasing multiblock structure.");
		for (Entry<Object, MultiblockPart> part : structureParts.entrySet())
			part.getValue().release();
		EnumStargateState stateOf = (EnumStargateState) getMetadata("state");
		if (!isClient && (stateOf == EnumStargateState.Connected || stateOf == EnumStargateState.Disconnecting)) {
			LanteaCraft.getLogger().log(Level.INFO, "Creating explosion: gate destroyed while connected!");
			int k = host.worldObj.getBlockId(host.xCoord, host.yCoord, host.zCoord);
			if (k == LanteaCraft.Blocks.stargateBaseBlock.blockID)
				LanteaCraft.Blocks.stargateBaseBlock
						.explode(host.worldObj, host.xCoord, host.yCoord, host.zCoord, 500D);
		}
		structureParts.clear();
		modified = true;
	}

	@Override
	public MultiblockPart getPart(Object reference) {
		return structureParts.get(reference);
	}

	@Override
	public MultiblockPart[] getAllParts() {
		return structureParts.values().toArray(new MultiblockPart[0]);
	}

	@Override
	public void validated(boolean oldState, boolean newState) {
		// send an update packet only when the server does something, the client
		// shouldn't be firing this behavior
		if (!isClient)
			host.getDescriptionPacket();
	}

	@Override
	public void disband() {
		LanteaCraft.getLogger().log(Level.FINE, ((isClient) ? "[client]" : "[server]") + " Disbanding structure.");
		boolean wasValid = isValid();
		freeStructure();
		isValid = false;
		validated(wasValid, isValid());
	}

	@Override
	public ModPacket pack() {
		StandardModPacket packet = new StandardModPacket(new WorldLocation(host));
		packet.setIsForServer(false);
		packet.setType("LanteaPacket.TileUpdate");
		packet.setValue("metadata", metadata);
		packet.setValue("isValid", isValid());
		packet.setValue("orientation", getOrientation());
		HashMap<Object, Vector3> gparts = new HashMap<Object, Vector3>();
		for (Entry<Object, MultiblockPart> part : structureParts.entrySet())
			gparts.put(part.getKey(), part.getValue().getVectorLoc());
		packet.setValue("parts", gparts);
		return packet;
	}

	@Override
	public void unpack(ModPacket packet) {
		StandardModPacket packetStandard = (StandardModPacket) packet;
		metadata = (HashMap<String, Object>) packetStandard.getValue("metadata");
		isValid = (Boolean) packetStandard.getValue("isValid");
		setOrientation((EnumOrientations) packetStandard.getValue("orientation"));
		if (!isValid)
			freeStructure();
		else {
			setLocation(host.xCoord, host.yCoord, host.zCoord);
			collectStructure(host.worldObj, host.xCoord, host.yCoord, host.zCoord);
		}

	}

	@Override
	public ModPacket pollForUpdate() {
		StandardModPacket packet = new StandardModPacket(new WorldLocation(host));
		packet.setIsForServer(true);
		packet.setType("LanteaPacket.UpdateRequest");
		return packet;
	}

}
