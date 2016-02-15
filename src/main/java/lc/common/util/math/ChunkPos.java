package lc.common.util.math;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Represents a position in terms of 2D chunk space
 *
 * @author AfterLifeLochie
 *
 */
public class ChunkPos {

	/** The dimension of the chunk */
	public int dimension;
	/** The x-coordinate of the chunk */
	public int cx;
	/** The z-coordinate of the chunk */
	public int cz;

	/**
	 * Derive a chunk position from a tile entity
	 *
	 * @param te
	 *            The tile entity
	 */
	public ChunkPos(TileEntity te) {
		this(te.getWorld().provider.getDimensionId(), te.getPos().getX() >> 4, te.getPos().getZ() >> 4);
	}

	/**
	 * Create a new chunk position
	 *
	 * @param dimension
	 *            The dimension ID
	 * @param x
	 *            The chunk x-coordinate
	 * @param z
	 *            The chunk y-coordinate
	 */
	public ChunkPos(int dimension, int x, int z) {
		this.dimension = dimension;
		cx = x;
		cz = z;
	}

	/**
	 * Create a new chunk position
	 *
	 * @param x
	 *            The chunk x-coordinate
	 * @param z
	 *            The chunk y-coordinate
	 */
	public ChunkPos(int x, int z) {
		cx = x;
		cz = z;
	}

	/**
	 * Derive a chunk position from an NBT tag compound
	 *
	 * @param nbt
	 *            The tag compound
	 */
	public ChunkPos(NBTTagCompound nbt) {
		dimension = nbt.getInteger("dimension");
		cx = nbt.getInteger("cx");
		cz = nbt.getInteger("cz");
	}

	/**
	 * Convert this chunk position to an NBT tag compound
	 *
	 * @return The chunk position saved into an NBT tag compound
	 */
	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("dimension", dimension);
		nbt.setInteger("cx", cx);
		nbt.setInteger("cz", cz);
		return nbt;
	}

	/**
	 * Convert this chunk position to a dimension position
	 *
	 * @return A dimension position representing the origin of this chunk
	 */
	public DimensionPos toWorldLocation() {
		return new DimensionPos(dimension, cx << 4, 0, cz << 4);
	}

	/**
	 * Set the dimension of this chunk position
	 *
	 * @param dimension
	 *            The new dimension ID
	 */
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	/**
	 * Clear the dimension ID of this chunk position
	 */
	public void clearDimension() {
		dimension = 0;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ChunkPos))
			return false;
		ChunkPos location = (ChunkPos) o;
		return location.dimension == dimension && location.cx == cx && location.cz == cz;
	}

}
