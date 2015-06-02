package lc.server.world;

import java.util.ArrayList;

import lc.LanteaCraft;
import lc.common.LCLog;
import lc.common.util.math.ChunkPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

public class LCLoadedChunkManager {

	public static class LCChunkTicket {

		protected final Ticket ticket;
		protected final ArrayList<ChunkPos> loaded;

		public LCChunkTicket(Ticket ticket) {
			this.ticket = ticket;
			this.loaded = new ArrayList<ChunkPos>();
		}

		public void loadChunk(ChunkPos chunk) {
			ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(chunk.cx, chunk.cz));
			loaded.add(chunk);
		}

		public void loadChunkRange(ChunkPos origin, int ix, int iz, int mx, int mz) {
			for (int x = ix; x <= mx; x++)
				for (int z = iz; z <= mz; z++)
					loadChunk(new ChunkPos(origin.cx + x, origin.cz + z));
		}

		public void unload() {
			for (ChunkPos chunk : loaded)
				ForgeChunkManager.unforceChunk(ticket, new ChunkCoordIntPair(chunk.cx, chunk.cz));
			loaded.clear();
		}
	}

	private ArrayList<LCChunkTicket> tickets = new ArrayList<LCChunkTicket>();

	public LCChunkTicket requestTicket(World world) {
		Ticket ticket = ForgeChunkManager.requestTicket(LanteaCraft.instance, world, Type.NORMAL);
		if (ticket == null)
			LCLog.fatal("Too many open chunk tickets.");
		LCChunkTicket wrapper = new LCChunkTicket(ticket);
		tickets.add(wrapper);
		return wrapper;
	}

	public void closeTicket(LCChunkTicket ticket) {
		ticket.unload();
		ForgeChunkManager.releaseTicket(ticket.ticket);
		tickets.remove(ticket);
	}

}
