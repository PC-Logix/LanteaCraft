package lc.common.base.generation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class LCWorldData extends WorldSavedData {

	NBTTagCompound compound = new NBTTagCompound();

	public LCWorldData() {
		super("LC2DS");
	}

	public static LCWorldData forWorld(World world) {
		MapStorage storage = world.perWorldStorage;
		LCWorldData result = (LCWorldData) storage.loadData(LCWorldData.class, "LC2DS");
		if (result == null) {
			result = new LCWorldData();
			storage.setData("LC2DS", result);
		}
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		compound = nbt.getCompoundTag("LC2DS");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("LC2DS", compound);
	}

}
