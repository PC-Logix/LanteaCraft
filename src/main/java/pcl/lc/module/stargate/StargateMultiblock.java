package pcl.lc.module.stargate;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumStargateState;
import pcl.lc.base.multiblock.EnumOrientations;
import pcl.lc.base.multiblock.GenericMultiblock;
import pcl.lc.base.multiblock.MultiblockPart;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.base.network.packet.StandardModPacket;
import pcl.lc.core.WorldLog;
import pcl.lc.module.ModuleStargates;
import pcl.lc.module.stargate.tile.TileStargateRing;
import pcl.lc.util.ImmutablePair;
import pcl.lc.util.Vector3;
import pcl.lc.util.WorldLocation;

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
			log.log(Level.INFO, String.format("Stargate state changed to %s at (%s %s %s, dim: %s).", isValid, xCoord,
					yCoord, zCoord, host.getWorldObj().provider.dimensionId));
		}

		// People keep putting blocks inside Stargates after they're formed,
		// this isn't okay. Now we're going to watch what they do, so if they
		// do, the gate might explode violently.
		if (!isClient)
			if (0 > watcher) {
				watcher += 20;
				boolean result = isValidStructure(host.getWorldObj(), host.xCoord, host.yCoord, host.zCoord);
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
		if (entity instanceof TileStargateRing)
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
		if (isGateTileEntity(worldAccess.getTileEntity(baseX, baseY, baseZ + 1))
				&& isGateTileEntity(worldAccess.getTileEntity(baseX, baseY, baseZ - 1)))
			return EnumOrientations.NORTH_SOUTH;

		// Test East-West alignment along X axis
		if (isGateTileEntity(worldAccess.getTileEntity(baseX + 1, baseY, baseZ))
				&& isGateTileEntity(worldAccess.getTileEntity(baseX - 1, baseY, baseZ)))
			return EnumOrientations.EAST_WEST;

		// Likely not a valid orientation at all
		return null;
	}

	@Override
	public boolean isValidStructure(World worldAccess, int baseX, int baseY, int baseZ) {
		EnumOrientations currentOrientation = getOrientation(worldAccess, baseX, baseY, baseZ);

		// North-South means the gate is aligned along X
		if (currentOrientation == EnumOrientations.EAST_WEST) {
			LanteaCraft.getLogger().log(Level.DEBUG, "Testing EASTWEST");
			for (int y = 0; y < 7; y++)
				for (int x = 0; x < 7; x++) {
					Block block = worldAccess.getBlock(baseX + (x - 3), baseY + y, baseZ);
					TileEntity entity = worldAccess.getTileEntity(baseX + (x - 3), baseY + y, baseZ);
					if (!testIsValidForExpected(worldAccess, baseX + (x - 3), baseY + y, baseZ, entity, block,
							stargateModel[y][x]))
						return false;
				}
			return true;
		}

		// East-West means the gate is aligned along Z
		if (currentOrientation == EnumOrientations.NORTH_SOUTH) {
			LanteaCraft.getLogger().log(Level.DEBUG, "Testing NORTHSOUTH");
			for (int y = 0; y < 7; y++)
				for (int z = 0; z < 7; z++) {
					Block block = worldAccess.getBlock(baseX, baseY + y, baseZ + (z - 3));
					TileEntity entity = worldAccess.getTileEntity(baseX, baseY + y, baseZ + (z - 3));
					if (!testIsValidForExpected(worldAccess, baseX, baseY + y, baseZ + (z - 3), entity, block,
							stargateModel[y][z]))
						return false;
				}
			return true;
		}

		// Likely not a valid orientation at all
		return false;
	}

	private boolean testIsValidForExpected(World world, int x, int y, int z, TileEntity entity, Block block,
			int expectedType) {
		if (expectedType == 0)
			if (block == null)
				return true;
			else
				return block.isAir(world, x, y, z);
		if (expectedType == 1 || expectedType == 2) {
			if (!(entity instanceof TileStargateRing))
				return false;
			TileStargateRing entityAsRing = (TileStargateRing) entity;
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
			LanteaCraft.getLogger().log(Level.DEBUG, "Globbing EASTWEST");
			for (int x = 0; x < 7; x++)
				for (int y = 0; y < 7; y++) {
					TileEntity entity = worldAccess.getTileEntity(baseX + (x - 3), baseY + y, baseZ);
					if (stargateModel[y][x] != 0 && stargateModel[y][x] != 3) {
						TileStargateRing entityAsRing = (TileStargateRing) entity;
						StargatePart teAsPart = entityAsRing.getAsPart();
						if (!teAsPart.canMergeWith(this))
							return false;
						teAsPart.mergeWith(this);
						structureParts.put(new ImmutablePair<Integer, Integer>(x, y), teAsPart);
					}
				}
			LanteaCraft.getLogger().log(Level.DEBUG, "Merged in orientation EAST-WEST OK");
			structureOrientation = EnumOrientations.EAST_WEST;
			modified = true;
			return true;
		}

		// East-West means the gate is aligned along Z
		if (currentOrientation == EnumOrientations.NORTH_SOUTH) {
			LanteaCraft.getLogger().log(Level.DEBUG, "Globbing NORTHSOUTH");
			for (int z = 0; z < 7; z++)
				for (int y = 0; y < 7; y++) {
					TileEntity entity = worldAccess.getTileEntity(baseX, baseY + y, baseZ + (z - 3));
					if (stargateModel[y][z] != 0 && stargateModel[y][z] != 3) {
						TileStargateRing entityAsRing = (TileStargateRing) entity;
						StargatePart teAsPart = entityAsRing.getAsPart();
						if (!teAsPart.canMergeWith(this))
							return false;
						teAsPart.mergeWith(this);
						structureParts.put(new ImmutablePair<Integer, Integer>(z, y), teAsPart);
					}
				}
			LanteaCraft.getLogger().log(Level.DEBUG, "Merged in orientation NORTH-SOUTH OK");
			structureOrientation = EnumOrientations.NORTH_SOUTH;
			modified = true;
			return true;
		}

		// Likely not a valid orientation at all
		LanteaCraft.getLogger().log(Level.DEBUG, "Weird orientation result!");
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
			Block block = host.getWorldObj().getBlock(host.xCoord, host.yCoord, host.zCoord);
			if (block.equals(ModuleStargates.Blocks.stargateBaseBlock))
				ModuleStargates.Blocks.stargateBaseBlock.explode(host.getWorldObj(), host.xCoord, host.yCoord,
						host.zCoord, 500D);
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
		LanteaCraft.getLogger().log(Level.DEBUG, ((isClient) ? "[client]" : "[server]") + " Disbanding structure.");
		boolean wasValid = isValid();
		freeStructure();
		isValid = false;
		validated(wasValid, isValid());
	}

	@Override
	public ModPacket pack() {
		StandardModPacket packet = new StandardModPacket(new WorldLocation(host));
		packet.setIsForServer(false);
		packet.setType("LanteaPacket.MultiblockUpdate");
		packet.setValue("metadata", metadata);
		packet.setValue("isValid", isValid());
		packet.setValue("orientation", getOrientation());
		HashMap<Object, Vector3> gparts = new HashMap<Object, Vector3>();
		for (Entry<Object, MultiblockPart> part : structureParts.entrySet())
			gparts.put(part.getKey(), part.getValue().getVectorLoc());
		packet.setValue("parts", gparts);
		return packet;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void unpack(ModPacket packet) {
		StandardModPacket packetStandard = (StandardModPacket) packet;
		metadata = (HashMap<String, Object>) packetStandard.getValue("metadata");
		isValid = (Boolean) packetStandard.getValue("isValid");
		setOrientation((EnumOrientations) packetStandard.getValue("orientation"));
	}

	@Override
	public ModPacket pollForUpdate() {
		StandardModPacket packet = new StandardModPacket(new WorldLocation(host));
		packet.setIsForServer(true);
		packet.setType("LanteaPacket.UpdateRequest");
		return packet;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StargateMultiblock))
			return false;
		StargateMultiblock that = (StargateMultiblock) o;
		if (that.host == null || host == null)
			return false;
		return ((that.host.xCoord == host.xCoord) && (that.host.yCoord == host.yCoord) && (that.host.zCoord == host.zCoord));
	}
}
