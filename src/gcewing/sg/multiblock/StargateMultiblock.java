package gcewing.sg.multiblock;

import gcewing.sg.tileentity.TileEntityStargateRing;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class StargateMultiblock extends GenericMultiblock {

	private int rotation;

	private int[][] stargateModel = { { 2, 0, 3, 0, 2 }, { 1, 0, 0, 0, 1 }, { 2, 0, 0, 0, 2 }, { 1, 0, 0, 0, 1 },
			{ 2, 1, 2, 1, 2 }, };

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

	private boolean isGateTileEntity(TileEntity entity) {
		if (entity instanceof TileEntityStargateRing)
			return true;
		return false;
	}

	private EnumOrientations getOrientation(World worldAccess, int baseX, int baseY, int baseZ) {
		// Test North-South alignment along Z axis
		int zNorthSouthA = baseZ + 1, zNorthSouthB = baseZ - 1;
		if (isGateTileEntity(worldAccess.getBlockTileEntity(baseX, baseY, zNorthSouthA))
				&& isGateTileEntity(worldAccess.getBlockTileEntity(baseX, baseY, zNorthSouthB)))
			return EnumOrientations.NORTH_SOUTH;

		// Test East-West alignment along X axis
		int xEastWestA = baseX + 1, xEastWestB = baseX - 1;
		if (isGateTileEntity(worldAccess.getBlockTileEntity(xEastWestA, baseY, baseZ))
				&& isGateTileEntity(worldAccess.getBlockTileEntity(xEastWestB, baseY, baseZ)))
			return EnumOrientations.EAST_WEST;

		// Likely not a valid orientation at all
		return null;
	}

	@Override
	public boolean isValidStructure(World worldAccess, int baseX, int baseY, int baseZ) {
		EnumOrientations currentOrientation = getOrientation(worldAccess, baseX, baseY, baseZ);

		// North-South means the gate is aligned along Z
		if (currentOrientation == EnumOrientations.NORTH_SOUTH) {
			for (int x = 0; x < 5; x++) {
				for (int y = 0; y < 5; y++) {
					TileEntity entity = worldAccess.getBlockTileEntity(baseX + (x - 3), baseY + y, baseZ);
					if (!testIsValidForExpected(entity, stargateModel[y][x]))
						return false;
				}
			}
			return true;
		}

		// East-West means the gate is aligned along X
		if (currentOrientation == EnumOrientations.EAST_WEST) {
			for (int z = 0; z < 5; z++) {
				for (int y = 0; y < 5; y++) {
					TileEntity entity = worldAccess.getBlockTileEntity(baseX, baseY + y, baseZ + (z - 3));
					if (!testIsValidForExpected(entity, stargateModel[y][z]))
						return false;
				}
			}
			return true;
		}

		// Likely not a valid orientation at all
		return false;
	}

	private boolean testIsValidForExpected(TileEntity entity, int expectedType) {
		if (expectedType == 0)
			if (entity != null)
				return false;
		if (expectedType == 1 || expectedType == 2) {
			if (!(entity instanceof TileEntityStargateRing))
				return false;
			TileEntityStargateRing entityAsRing = (TileEntityStargateRing) entity;
			StargatePart teAsPart = null;
			// TODO: This method doesn't exist yet, but it should be added.
			// teAsPart = entityAsRing.getAsPart();
			if (expectedType == 1 && !teAsPart.getType().equals("partStargateBlock"))
				return false;
			if (expectedType == 2 && !teAsPart.getType().equals("partStargateChevron"))
				return false;
		}
		return true;
	}

	@Override
	public boolean collectStructure(World worldAccess, int baseX, int baseY, int baseZ) {
		EnumOrientations currentOrientation = getOrientation(worldAccess, baseX, baseY, baseZ);

		// North-South means the gate is aligned along Z
		if (currentOrientation == EnumOrientations.NORTH_SOUTH) {
			for (int x = 0; x < 5; x++) {
				for (int y = 0; y < 5; y++) {
					TileEntity entity = worldAccess.getBlockTileEntity(baseX + (x - 3), baseY + y, baseZ);
					if (stargateModel[y][x] != 0 && stargateModel[y][x] != 3) {
						
					}
				}
			}
			return true;
		}

		// East-West means the gate is aligned along X
		if (currentOrientation == EnumOrientations.EAST_WEST) {
			for (int z = 0; z < 5; z++) {
				for (int y = 0; y < 5; y++) {
					TileEntity entity = worldAccess.getBlockTileEntity(baseX, baseY + y, baseZ + (z - 3));
					if (!testIsValidForExpected(entity, stargateModel[y][z]))
						return false;
				}
			}
			return true;
		}

		// Likely not a valid orientation at all
		return false;
	}

	@Override
	public MultiblockPart getPart(String reference) {
		// TODO Auto-generated method stub
		return null;
	}

}
