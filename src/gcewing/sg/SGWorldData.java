//------------------------------------------------------------------------------------------------
//
//   SG Craft - World saved data
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.nbt.*;
import net.minecraft.world.*;
import net.minecraft.world.storage.*;

import net.minecraftforge.common.*;

public class SGWorldData extends WorldSavedData {

	final static String key = "gcewing.sg";
	
	NBTTagCompound chunkGenFlags =  new NBTTagCompound();
	
	public SGWorldData() {
		super(key);
	}

	public static SGWorldData forWorld(World world) {
		MapStorage storage = world.perWorldStorage;
		SGWorldData result = (SGWorldData)storage.loadData(SGWorldData.class, key);
		if (result == null) {
			result = new SGWorldData();
			storage.setData(key, result);
		}
		return result;
	}

	boolean chunkGenCheck(int chunkX, int chunkZ) {
		String key = chunkX + "," + chunkZ;
		boolean result = chunkGenFlags.getBoolean(key);
		if (!result) {
			chunkGenFlags.setBoolean(key, true);
			markDirty();
		}
		return result;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		chunkGenFlags = nbt.getCompoundTag("chunkGenFlags");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setCompoundTag("chunkGenFlags", chunkGenFlags);
	}

}
