package lc.common.util.math;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * A position in four-dimensional space (dimension, x, y, z)
 *
 * @author AfterLifeLochie
 *
 */
public class DimensionPos {

	/** The dimension element */
	public int dimension;
	/** The x-coordinate */
	public int x;
	/** The y-coordinate */
	public int y;
	/** The z-coordinate */
	public int z;

	/**
	 * Create a new position based on a tile
	 *
	 * @param te
	 *            The tile entity
	 */
	public DimensionPos(TileEntity te) {
		this(te.getWorldObj().provider.dimensionId, te.xCoord, te.yCoord, te.zCoord);
	}

	/**
	 * Create a new dimension position from fixed values
	 *
	 * @param dimension
	 *            The dimension ID
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param z
	 *            The z-coordinate
	 */
	public DimensionPos(int dimension, int x, int y, int z) {
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Create a new position based on a stored NBT compound
	 *
	 * @param nbt
	 *            The NBT compound to load from
	 */
	public DimensionPos(NBTTagCompound nbt) {
		dimension = nbt.getInteger("dimension");
		x = nbt.getInteger("x");
		y = nbt.getInteger("y");
		z = nbt.getInteger("z");
	}

	/**
	 * Save the position to an NBT compound
	 *
	 * @return The saved NBT compound
	 */
	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("dimension", dimension);
		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);
		return nbt;
	}

	/**
	 * Convert this dimension position to a chunk location
	 *
	 * @return The chunk location
	 */
	public ChunkPos toChunkLocation() {
		return new ChunkPos(dimension, x >> 4, z >> 4);
	}

	/**
	 * Converts this position to a vector (disposes the dimension data).
	 *
	 * @return The vector position of this dimension position
	 */
	public Vector3 toVector3() {
		return new Vector3(x, y, z);
	}

}
