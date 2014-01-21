package pcl.common.worldgen;

import java.util.WeakHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import pcl.common.helpers.ConfigValue;
import pcl.lc.LanteaCraft;
import pcl.lc.worldgen.NaquadahOreWorldGen;

public class ChunkData {

	static boolean debug = false;

	static WeakHashMap<Chunk, ChunkData> map = new WeakHashMap<Chunk, ChunkData>();

	public boolean oresGenerated;

	public static ChunkData forChunk(Chunk chunk) {
		ChunkData data = map.get(chunk);
		if (data == null) {
			data = new ChunkData();
			map.put(chunk, data);
		}
		return data;
	}

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
