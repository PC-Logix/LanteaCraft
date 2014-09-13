package lc.common.util.math;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ChunkPos {

	public int dimension;
	public int cx, cz;
	public boolean isStrongLocation;

	public ChunkPos(TileEntity te) {
		this(te.getWorldObj().provider.dimensionId, te.xCoord >> 4, te.zCoord >> 4);
	}

	public ChunkPos(int dimension, int x, int z) {
		this.dimension = dimension;
		cx = x;
		cz = z;
		isStrongLocation = true;
	}

	public ChunkPos(int x, int z) {
		cx = x;
		cz = z;
		isStrongLocation = false;
	}

	public ChunkPos(NBTTagCompound nbt) {
		dimension = nbt.getInteger("dimension");
		cx = nbt.getInteger("cx");
		cz = nbt.getInteger("cz");
		isStrongLocation = nbt.getBoolean("isStrongLocation");
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("dimension", dimension);
		nbt.setInteger("cx", cx);
		nbt.setInteger("cz", cz);
		nbt.setBoolean("isStrongLocation", isStrongLocation);
		return nbt;
	}

	public DimensionPos toWorldLocation() {
		if (!isStrongLocation)
			throw new IllegalArgumentException("Can't make a weak location a DimensionPos!");
		return new DimensionPos(dimension, cx << 4, 0, cz << 4);
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
		isStrongLocation = true;
	}

	public void clearDimension() {
		dimension = 0;
		isStrongLocation = false;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ChunkPos))
			return false;
		ChunkPos location = (ChunkPos) o;
		return (location.dimension == dimension && location.cx == cx && location.cz == cz);
	}

}
