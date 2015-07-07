package lc.common.base.generation.scattered;

import java.util.Random;

import lc.common.util.math.Vector3;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

/**
 * LanteaCraft scattered feature stub class. Used for the creation of scattered
 * structures using addComponentParts.
 *
 * @author AfterLifeLochie
 */
public abstract class LCScatteredFeature extends StructureComponent {

	/** The size of the bounding box for this feature in the X axis */
	protected int scatteredFeatureSizeX;
	/** The size of the bounding box for this feature in the Y axis */
	protected int scatteredFeatureSizeY;
	/** The size of the bounding box for this feature in the Z axis */
	protected int scatteredFeatureSizeZ;
	/** The h-position store */
	protected int hPos = -1;

	/** Default constructor. */
	public LCScatteredFeature() {
		coordBaseMode = 0;
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
	protected LCScatteredFeature(Random rng, int x, int y, int z, int sx, int sy, int sz) {
		super(0);
		coordBaseMode = 0;
		scatteredFeatureSizeX = sx;
		scatteredFeatureSizeY = sy;
		scatteredFeatureSizeZ = sz;
		boundingBox = new StructureBoundingBox(x, y, z, x + sz - 1, y + sy - 1, z + sx - 1);

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
	 * Calculates the bottom layer of the bounding box so that it sits on the
	 * surface correctly.
	 *
	 * @param world
	 *            The world
	 * @param sbb
	 *            The bounding box
	 * @param offset
	 *            The height offset
	 */
	protected void recalcHeightOffsets(World world, StructureBoundingBox sbb, int offset) {
		if (hPos >= 0)
			return;
		int seenBlocks = 0, totalHeight = 0;
		for (int z = boundingBox.minZ; z <= boundingBox.maxZ; ++z) {
			for (int x = boundingBox.minX; x <= boundingBox.maxX; ++x) {
				totalHeight += Math.max(world.getTopSolidOrLiquidBlock(x, z), world.provider.getAverageGroundLevel());
				seenBlocks++;
			}
		}
		if (seenBlocks == 0)
			return;
		hPos = totalHeight / seenBlocks;
		boundingBox.offset(0, hPos - boundingBox.minY + offset, 0);
	}

	/**
	 * Build the component into the target world using the random number
	 * generator provided.
	 */
	@Override
	public abstract boolean addComponentParts(World world, Random random, StructureBoundingBox structureboundingbox);

	/**
	 * Fill a region with blocks
	 * 
	 * @param w
	 *            The world to write to
	 * @param bb
	 *            The structure bounding-box
	 * @param v0
	 *            The start vector
	 * @param v1
	 *            The end vector
	 * @param b0
	 *            The block to fill with
	 */
	protected void fill(World w, StructureBoundingBox bb, Vector3 v0, Vector3 v1, Block b0) {
		fill(w, bb, v0, v1, b0, b0);
	}

	/**
	 * Fill a region with blocks
	 * 
	 * @param w
	 *            The world to write to
	 * @param bb
	 *            The structure bounding-box
	 * @param v0
	 *            The start vector
	 * @param v1
	 *            The end vector
	 * @param b0
	 *            The block to fill with
	 * @param m0
	 *            The metadata to fill with
	 */
	protected void fill(World w, StructureBoundingBox bb, Vector3 v0, Vector3 v1, Block b0, int m0) {
		fill(w, bb, v0, v1, b0, m0, b0, m0);
	}

	/**
	 * Fill a region with blocks
	 * 
	 * @param w
	 *            The world to write to
	 * @param bb
	 *            The structure bounding-box
	 * @param v0
	 *            The start vector
	 * @param v1
	 *            The end vector
	 * @param b0
	 *            The block to fill with
	 * @param b1
	 *            The block to replace blocks with
	 */
	protected void fill(World w, StructureBoundingBox bb, Vector3 v0, Vector3 v1, Block b0, Block b1) {
		fill(w, bb, v0, v1, b0, b1, false);
	}

	/**
	 * Fill a region with blocks
	 * 
	 * @param w
	 *            The world to write to
	 * @param bb
	 *            The structure bounding-box
	 * @param v0
	 *            The start vector
	 * @param v1
	 *            The end vector
	 * @param b0
	 *            The block to fill with
	 * @param m0
	 *            The metadata to fill with
	 * @param b1
	 *            The block to replace blocks with
	 * @param m1
	 *            The metadata to replace blocks with
	 */
	protected void fill(World w, StructureBoundingBox bb, Vector3 v0, Vector3 v1, Block b0, int m0, Block b1, int m1) {
		fill(w, bb, v0, v1, b0, m0, b1, m1, false);
	}

	/**
	 * Fill a region with blocks
	 * 
	 * @param w
	 *            The world to write to
	 * @param bb
	 *            The structure bounding-box
	 * @param v0
	 *            The start vector
	 * @param v1
	 *            The end vector
	 * @param b0
	 *            The block to fill with
	 * @param b1
	 *            The block to replace with
	 * @param rep
	 *            If only replacement is permitted
	 */
	protected void fill(World w, StructureBoundingBox bb, Vector3 v0, Vector3 v1, Block b0, Block b1, boolean rep) {
		fillWithBlocks(w, bb, v0.fx(), v0.fy(), v0.fz(), v1.fx(), v1.fy(), v1.fz(), b0, b1, rep);
	}

	/**
	 * Fill a region with blocks
	 * 
	 * @param w
	 *            The world to write to
	 * @param bb
	 *            The structure bounding-box
	 * @param v0
	 *            The start vector
	 * @param v1
	 *            The end vector
	 * @param b0
	 *            The block to fill with
	 * @param m0
	 *            The metadata to fill with
	 * @param b1
	 *            The block to replace with
	 * @param m1
	 *            The metadata to replace with
	 * @param rep
	 *            If only replacement is permitted
	 */
	protected void fill(World w, StructureBoundingBox bb, Vector3 v0, Vector3 v1, Block b0, int m0, Block b1, int m1,
			boolean rep) {
		fillWithMetadataBlocks(w, bb, v0.fx(), v0.fy(), v0.fz(), v1.fx(), v1.fy(), v1.fz(), b0,
				m0, b1, m1, rep);
	}

}
