package pcl.lc.worldgen;

import java.util.WeakHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import pcl.common.helpers.ConfigValue;
import pcl.lc.LanteaCraft;

public class ChunkData {

	private static WeakHashMap<ChunkCoordIntPair, ChunkData> chunkCache = new WeakHashMap<ChunkCoordIntPair, ChunkData>();

	public static ChunkData forChunk(Chunk chunk) {
		ChunkData data = chunkCache.get(chunk.getChunkCoordIntPair());
		if (data == null) {
			data = new ChunkData();
			chunkCache.put(chunk.getChunkCoordIntPair(), data);
		}
		return data;
	}

	public static void flush() {
		chunkCache.clear();
	}

	public boolean oresGenerated;

	public void readFromNBT(NBTTagCompound nbt) {
		oresGenerated = nbt.getBoolean("pcl.lc.oresGenerated");
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("pcl.lc.oresGenerated", oresGenerated);
	}

	public static void onChunkLoad(ChunkDataEvent.Load e) {
		Chunk chunk = e.getChunk();
		ChunkData data = ChunkData.forChunk(chunk);
		data.readFromNBT(e.getData());
		if (!data.oresGenerated && ((ConfigValue<Boolean>) LanteaCraft.getProxy().getConfigValue("addOresToExistingWorlds")).getValue()) {
			NaquadahOreWorldGen gen = LanteaCraft.getProxy().getOreGenerator();
			if (gen != null)
				gen.regenerate(chunk);
		}
	}

	public static void onChunkSave(ChunkDataEvent.Save e) {
		Chunk chunk = e.getChunk();
		ChunkData data = ChunkData.forChunk(chunk);
		data.writeToNBT(e.getData());
	}

}
