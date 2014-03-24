package pcl.lc.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.lc.tileentity.TileEntityStargateBase;

public class WorldLocation {

	public int dimension;
	public int x, y, z;

	public WorldLocation(TileEntity te) {
		this(te.worldObj.provider.dimensionId, te.xCoord, te.yCoord, te.zCoord);
	}

	public WorldLocation(int dimension, int x, int y, int z) {
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WorldLocation(NBTTagCompound nbt) {
		dimension = nbt.getInteger("dimension");
		x = nbt.getInteger("x");
		y = nbt.getInteger("y");
		z = nbt.getInteger("z");
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("dimension", dimension);
		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);
		return nbt;
	}

	public ChunkLocation toChunkLocation() {
		return new ChunkLocation(dimension, x >> 4, z >> 4);
	}

}
