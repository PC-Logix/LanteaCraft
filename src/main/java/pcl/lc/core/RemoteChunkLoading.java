package pcl.lc.core;

import java.util.ArrayList;
import java.util.logging.Level;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.ITickAgent;

public class RemoteChunkLoading implements ITickAgent {

	private ArrayList<ChunkLoadRequest> requests = new ArrayList<ChunkLoadRequest>();
	private ArrayList<ChunkLoadRequest> expiredRequests = new ArrayList<ChunkLoadRequest>();

	private class ChunkLoadRequest {
		private final String name;
		private final Ticket ticket;
		private final int max_age;
		private int age;

		public ChunkLoadRequest(String name, Ticket ticket, int expiry) {
			this.name = name;
			this.ticket = ticket;
			max_age = expiry;
		}

		public String name() {
			return name;
		}

		public boolean expired() {
			return age > max_age;
		}

		public void tick() {
			age++;
		}
	}

	/**
	 * Requests Forge load a range of chunks specified under a world and name
	 * with a maximum age in ticks. This method initializes and forces the
	 * chunks specified in the metadata payload between the NBT integers (minX,
	 * minZ) to (maxX, maxZ) inclusive.
	 * 
	 * @param name
	 *            The name of the request, used only for debugging.
	 * @param world
	 *            The World to load.
	 * @param maxAge
	 *            The maximum number of ticks the request may remain persistent
	 *            for.
	 * @param metadata
	 *            The metadata fields (minX, minZ, maxX, maxZ).
	 * @return If the loading request was a success.
	 */
	public boolean create(String name, World world, int maxAge, NBTTagCompound metadata) {
		Ticket ticket = ForgeChunkManager.requestTicket(LanteaCraft.getInstance(), world, Type.NORMAL);
		if (ticket == null)
			return false;
		int minX = metadata.getInteger("minX");
		int minZ = metadata.getInteger("minZ");
		int maxX = metadata.getInteger("maxX");
		int maxZ = metadata.getInteger("maxZ");
		for (int i = minX; i <= maxX; i++)
			for (int j = minZ; j <= maxZ; j++)
				ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(i, j));
		ChunkLoadRequest request = new ChunkLoadRequest(name, ticket, maxAge);
		synchronized (requests) {
			requests.add(request);
		}
		LanteaCraft.getLogger().log(Level.INFO, "Chunk loading request completed: " + name);
		return true;
	}

	@Override
	public void advance() {
		synchronized (requests) {
			for (ChunkLoadRequest request : requests) {
				request.tick();
				if (request.expired())
					expiredRequests.add(request);
			}
		}

		if (expiredRequests.size() > 0) {
			for (ChunkLoadRequest expired : expiredRequests)
				remove(expired);
			expiredRequests.clear();
		}
	}

	private void remove(ChunkLoadRequest request) {
		LanteaCraft.getLogger().log(Level.INFO, "Chunk loading request shutting down: " + request.name());
		ForgeChunkManager.releaseTicket(request.ticket);
		synchronized (requests) {
			requests.remove(request);
		}
	}

}
