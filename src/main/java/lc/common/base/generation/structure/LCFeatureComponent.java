package lc.common.base.generation.structure;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

/**
 * LanteaCraft scattered feature component.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCFeatureComponent extends StructureComponent {
	/** The size of the bounding box for this feature in the X axis */
	protected int scatteredFeatureSizeX;
	/** The size of the bounding box for this feature in the Y axis */
	protected int scatteredFeatureSizeY;
	/** The size of the bounding box for this feature in the Z axis */
	protected int scatteredFeatureSizeZ;
	/** The h-position store */
	protected int hPos = -1;

	/** Default constructor */
	public LCFeatureComponent() {
	}

	/**
	 * Create a new structure component
	 *
	 * @param rng
	 *            The random number generator
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param z
	 *            The z-coordinate
	 * @param sx
	 *            The size on the x-axis
	 * @param sy
	 *            The size on the y-axis
	 * @param sz
	 *            The size on the z-axis
	 */
	protected LCFeatureComponent(Random rng, int x, int y, int z, int sx, int sy, int sz) {
		super(0);
		scatteredFeatureSizeX = sx;
		scatteredFeatureSizeY = sy;
		scatteredFeatureSizeZ = sz;
		coordBaseMode = rng.nextInt(4);

		switch (coordBaseMode) {
		case 0:
		case 2:
			boundingBox = new StructureBoundingBox(x, y, z, x + sx - 1, y + sy - 1, z + sz - 1);
			break;
		default:
			boundingBox = new StructureBoundingBox(x, y, z, x + sz - 1, y + sy - 1, z + sx - 1);
		}
	}

	@Override
	protected void func_143012_a(NBTTagCompound tag) {
		tag.setInteger("Width", scatteredFeatureSizeX);
		tag.setInteger("Height", scatteredFeatureSizeY);
		tag.setInteger("Depth", scatteredFeatureSizeZ);
		tag.setInteger("HPos", hPos);
	}

	@Override
	protected void func_143011_b(NBTTagCompound tag) {
		scatteredFeatureSizeX = tag.getInteger("Width");
		scatteredFeatureSizeY = tag.getInteger("Height");
		scatteredFeatureSizeZ = tag.getInteger("Depth");
		hPos = tag.getInteger("HPos");
	}

	/**
	 * Determine if placement is allowed. Calculates the bottom layer of the
	 * bounding box so that it sits on the surface correctly.
	 *
	 * @param world
	 *            The world
	 * @param sbb
	 *            The bounding box
	 * @param offset
	 *            The height offset
	 * @return If the placement is legal
	 */
	protected boolean canPlaceAt(World world, StructureBoundingBox sbb, int offset) {
		if (hPos >= 0)
			return true;
		else {
			int j = 0, k = 0;
			for (int z = boundingBox.minZ; z <= boundingBox.maxZ; ++z)
				for (int x = boundingBox.minX; x <= boundingBox.maxX; ++x)
					if (sbb.isVecInside(x, 64, z)) {
						j += Math.max(world.getTopSolidOrLiquidBlock(x, z), world.provider.getAverageGroundLevel());
						++k;
					}
			if (k == 0)
				return false;
			else {
				hPos = j / k;
				boundingBox.offset(0, hPos - boundingBox.minY + offset, 0);
				return true;
			}
		}
	}
}