package pcl.lc.core;

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
		this.cx = x;
		this.cz = z;
		this.isStrongLocation = true;
	}

	public ChunkLocation(int x, int z) {
		this.cx = x;
		this.cz = z;
		this.isStrongLocation = false;
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
		this.isStrongLocation = true;
	}

	public void clearDimension() {
		this.dimension = 0;
		this.isStrongLocation = false;
	}

}
