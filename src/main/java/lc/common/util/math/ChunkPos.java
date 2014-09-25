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

	public ChunkPos(TileEntity te) {
		this(te.getWorldObj().provider.dimensionId, te.xCoord >> 4, te.zCoord >> 4);
	}

	public ChunkPos(int dimension, int x, int z) {
		this.dimension = dimension;
		cx = x;
		cz = z;
	}

	public ChunkPos(int x, int z) {
		cx = x;
		cz = z;
	}

	public ChunkPos(NBTTagCompound nbt) {
		dimension = nbt.getInteger("dimension");
		cx = nbt.getInteger("cx");
		cz = nbt.getInteger("cz");
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("dimension", dimension);
		nbt.setInteger("cx", cx);
		nbt.setInteger("cz", cz);
		return nbt;
	}

	public DimensionPos toWorldLocation() {
		return new DimensionPos(dimension, cx << 4, 0, cz << 4);
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public void clearDimension() {
		dimension = 0;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ChunkPos))
			return false;
		ChunkPos location = (ChunkPos) o;
		return (location.dimension == dimension && location.cx == cx && location.cz == cz);
	}

}
