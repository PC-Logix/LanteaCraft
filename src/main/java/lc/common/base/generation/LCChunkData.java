package lc.common.base.generation;

import java.util.WeakHashMap;

import lc.LCRuntime;
import lc.server.HintProviderServer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;

public class LCChunkData {

	private static WeakHashMap<Chunk, LCChunkData> chunkCache = new WeakHashMap<Chunk, LCChunkData>();

	public static LCChunkData forChunk(Chunk chunk) {
		LCChunkData data = chunkCache.get(chunk);
		if (data == null) {
			data = new LCChunkData();
			chunkCache.put(chunk, data);
		}
		return data;
	}

	public static void onChunkLoad(ChunkDataEvent.Load e) {
		Chunk chunk = e.getChunk();
		LCChunkData data = LCChunkData.forChunk(chunk);
		data.readFromNBT(e.getData());
		((HintProviderServer) LCRuntime.runtime.hints()).generator().decorator
				.paint(e.world.rand, e.world, chunk, data);
	}

	public static void onChunkSave(ChunkDataEvent.Save e) {
		Chunk chunk = e.getChunk();
		LCChunkData data = LCChunkData.forChunk(chunk);
		data.writeToNBT(e.getData());
	}

	public static void flush() {
		chunkCache.clear();
	}

	public NBTTagCompound compound = new NBTTagCompound();
	public NBTTagCompound legacyCompound;

	private LCChunkData() {
	}

	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("LC2DS"))
			this.compound = nbt.getCompoundTag("LC2DS");
		if (nbt.hasKey("LanteaCraftMeta"))
			this.legacyCompound = nbt.getCompoundTag("LanteaCraftMeta");
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("LanteaCraftMeta", legacyCompound);
		nbt.setTag("LC2DS", compound);
	}

}
