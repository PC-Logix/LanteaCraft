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

		/** The underlying ticket */
		protected final Ticket ticket;
		/** The list of loaded chunks */
		protected final ArrayList<ChunkPos> loaded;

		/**
		 * Create a chunk ticket handler
		 * 
		 * @param ticket
		 *            The underlying Forge ticket
		 */
		public LCChunkTicket(Ticket ticket) {
			this.ticket = ticket;
			this.loaded = new ArrayList<ChunkPos>();
		}

		/**
		 * Load a chunk in the ticket
		 * 
		 * @param chunk
		 *            The chunk position to load
		 */
		public void loadChunk(ChunkPos chunk) {
			LCLog.debug("Forcing chunk: [%s, %s]", chunk.cx, chunk.cz);
			ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(chunk.cx, chunk.cz));
			loaded.add(chunk);
		}

		/**
		 * Load a range of chunks
		 * 
		 * @param origin
		 *            The origin chunk coordinate
		 * @param ix
		 *            The initial x-coordinate
		 * @param iz
		 *            The initial z-coordinate
		 * @param mx
		 *            The delta x-coordinate
		 * @param mz
		 *            The delta z-coordinate
		 */
		public void loadChunkRange(ChunkPos origin, int ix, int iz, int mx, int mz) {
			for (int x = ix; x <= mx; x++)
				for (int z = iz; z <= mz; z++)
					loadChunk(new ChunkPos(origin.cx + x, origin.cz + z));
		}

		/**
		 * Unload all the forced chunks held by this ticket
		 */
		public void unload() {
			LCLog.debug("Releasing all forced chunks");
			for (ChunkPos chunk : loaded)
				ForgeChunkManager.unforceChunk(ticket, new ChunkCoordIntPair(chunk.cx, chunk.cz));
			loaded.clear();
		}
	}

	private ArrayList<LCChunkTicket> tickets = new ArrayList<LCChunkTicket>();

	/**
	 * Request a ticket for a particular world
	 * 
	 * @param world
	 *            The world to request for
	 * @return The ticket, or null if no ticket is available
	 */
	public LCChunkTicket requestTicket(World world) {
		Ticket ticket = ForgeChunkManager.requestTicket(LanteaCraft.instance, world, Type.NORMAL);
		if (ticket == null)
			LCLog.fatal("Too many open chunk tickets.");
		LCChunkTicket wrapper = new LCChunkTicket(ticket);
		tickets.add(wrapper);
		return wrapper;
	}

	/**
	 * Closes a chunk ticket
	 * 
	 * @param ticket
	 *            The ticket to close
	 */
	public void closeTicket(LCChunkTicket ticket) {
		ticket.unload();
		ForgeChunkManager.releaseTicket(ticket.ticket);
		tickets.remove(ticket);
	}

}
