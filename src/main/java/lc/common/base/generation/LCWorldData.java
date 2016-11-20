package lc.common.base.generation;

import java.util.Set;

import lc.common.LCLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class LCWorldData extends WorldSavedData {

	NBTTagCompound compound;

	public LCWorldData() {
		this("LC2DS");
	}

	public LCWorldData(String label) {
		super("LC2DS");
		if (!label.equals("LC2DS"))
			LCLog.warn("Warning, loading LCWorldData wrapper around storage with label %s!", label);
		compound = new NBTTagCompound();
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
		Set<String> tags = (Set<String>) nbt.func_150296_c();
		for (String tag : tags)
			compound.setTag(tag, nbt.getTag(tag));
		if (!compound.hasKey("LC2DS")) {
			LCLog.warn("Warning, readFromNBT LCWorldData wrapper couldn't find LC2DS store!");
			compound.setTag("LC2DS", new NBTTagCompound());
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		if (!compound.hasKey("LC2DS"))
			LCLog.warn("Warning, writeToNBT LCWorldData wrapper doesn't have LC2DS store!");
		Set<String> tags = (Set<String>) compound.func_150296_c();
		for (String tag : tags) {
			nbt.setTag(tag, compound.getTag(tag));
		}
	}
	
	public NBTTagCompound getDSData() {
		return compound.getCompoundTag("LC2DS");
	}

}
