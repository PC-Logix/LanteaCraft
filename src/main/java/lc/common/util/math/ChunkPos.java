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
		this(te.getWorldObj().provider.dimensionId, te.xCoord >> 4, te.zCoord >> 4);
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cx;
		result = prime * result + cz;
		result = prime * result + dimension;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChunkPos other = (ChunkPos) obj;
		if (cx != other.cx)
			return false;
		if (cz != other.cz)
			return false;
		if (dimension != other.dimension)
			return false;
		return true;
	}

}
