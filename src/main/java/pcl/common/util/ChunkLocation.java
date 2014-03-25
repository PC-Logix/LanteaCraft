package pcl.common.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ChunkLocation {

	public int dimension;
	public int cx, cz;
	public boolean isStrongLocation;

	public ChunkLocation(TileEntity te) {
		this(te.worldObj.provider.dimensionId, te.xCoord >> 4, te.zCoord >> 4);
	}

	public ChunkLocation(int dimension, int x, int z) {
		this.dimension = dimension;
		cx = x;
		cz = z;
		isStrongLocation = true;
	}

	public ChunkLocation(int x, int z) {
		cx = x;
		cz = z;
		isStrongLocation = false;
	}

	public ChunkLocation(NBTTagCompound nbt) {
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

	public WorldLocation toWorldLocation() {
		if (!isStrongLocation)
			throw new IllegalArgumentException("Can't make a weak location a WorldLocation!");
		return new WorldLocation(dimension, cx << 4, 0, cz << 4);
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
		isStrongLocation = true;
	}

	public void clearDimension() {
		dimension = 0;
		isStrongLocation = false;
	}

}
