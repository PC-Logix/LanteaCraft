//------------------------------------------------------------------------------------------------
//
//   SG Craft - Structure representing the location of a stargate
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

import net.minecraftforge.common.*;

public class SGLocation {

	public int dimension;
	public int x, y, z;
	
	public SGLocation(TileEntity te) {
		this(te.worldObj.provider.dimensionId, te.xCoord, te.yCoord, te.zCoord);
	}
	
	public SGLocation(int dimension, int x, int y, int z) {
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public SGLocation(NBTTagCompound nbt) {
		dimension = nbt.getInteger("dimension");
		x = nbt.getInteger("x");
		y = nbt.getInteger("y");
		z = nbt.getInteger("z");
	}
	
	NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("dimension", dimension);
		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);
		return nbt;
	}
	
	SGBaseTE getStargateTE() {
		World world = /*DimensionManager.*/SGAddressing.getWorld(dimension);
		if (world == null) {
			//System.out.printf(
				//"SGCraft: SGLocation.getStargateTE: Oh, noes! Dimension %d is not loaded. How can this be?",
				//dimension);
				return null;
		}
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof SGBaseTE)
			return (SGBaseTE)te;
		else
			return null;
	}


}
