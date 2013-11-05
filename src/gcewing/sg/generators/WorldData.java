//------------------------------------------------------------------------------------------------
//
//   SG Craft - World saved data
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.generators;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class WorldData extends WorldSavedData {

	final static String key = "gcewing.sg";

	NBTTagCompound chunkGenFlags = new NBTTagCompound();

	public WorldData() {
		super(key);
	}

	public static WorldData forWorld(World world) {
		MapStorage storage = world.perWorldStorage;
		WorldData result = (WorldData) storage.loadData(WorldData.class, key);
		if (result == null) {
			result = new WorldData();
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
