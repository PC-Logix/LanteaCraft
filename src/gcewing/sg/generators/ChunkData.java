//------------------------------------------------------------------------------------------------
//
//   SG Craft - Extra data saved with a chunk
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.generators;

import gcewing.sg.SGCraft;

import java.util.WeakHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;

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
		oresGenerated = nbt.getBoolean("gcewing.sg.oresGenerated");
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("gcewing.sg.oresGenerated", oresGenerated);
	}

	public static void onChunkLoad(ChunkDataEvent.Load e) {
		Chunk chunk = e.getChunk();
		// System.out.printf("SGChunkData.onChunkLoad: (%d, %d)\n",
		// chunk.xPosition, chunk.zPosition);
		ChunkData data = ChunkData.forChunk(chunk);
		data.readFromNBT(e.getData());
		// if (data.oresGenerated)
		// System.out.printf("SGChunkData.onChunkLoad: Ores already added to chunk (%d, %d)\n",
		// chunk.xPosition, chunk.zPosition);
		if (!data.oresGenerated && SGCraft.addOresToExistingWorlds)
			// if (debug)
			// System.out.printf("SGChunkData.onChunkLoad: Adding ores to chunk (%d, %d)\n",
			// chunk.xPosition, chunk.zPosition);
			SGCraft.naquadahOreGenerator.regenerate(chunk);
	}

	public static void onChunkSave(ChunkDataEvent.Save e) {
		Chunk chunk = e.getChunk();
		// System.out.printf("SGChunkData.onChunkLoad: (%d, %d)\n",
		// chunk.xPosition, chunk.zPosition);
		ChunkData data = ChunkData.forChunk(chunk);
		data.writeToNBT(e.getData());
	}

}
