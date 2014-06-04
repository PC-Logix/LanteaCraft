package pcl.lc.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.ITickAgent;

/**
 * RemoteChunkLoading is an agent to allow tile-entities to load chunks which
 * they are not currently in, or regions of other worlds en-mass without
 * worrying about persistence.
 * 
 * @author AfterLifeLochie
 * 
 */
public class RemoteChunkLoading implements ITickAgent {

	public static boolean arePlayersWatchingChunk(WorldServer serverWorld, ChunkCoordIntPair chunk) {
		// we need a nice way of doing this now that
		// PlayerManager$PlayerInstance isn't public
		return true;
	}

	/**
	 * The container for all current requests in the agent.
	 */
	private ArrayList<ChunkLoadRequest> requests = new ArrayList<ChunkLoadRequest>();
	/**
	 * The container for all expired requests which have yet to be removed from
	 * the agent.
	 */
	private ArrayList<ChunkLoadRequest> expiredRequests = new ArrayList<ChunkLoadRequest>();

	/**
	 * A chunkloading request; contains a logical name, the Forge {@link Ticket}
	 * object, the age and the maximum lifecycle of the object.
	 * 
	 * @author AfterLifeLochie
	 * 
	 */
	public static class ChunkLoadRequest {
		private final String name;
		private final Ticket ticket;
		private final NBTTagCompound metadata;
		private int max_age;
		private int age;

		/**
		 * Create a request
		 * 
		 * @param name
		 *            The name of the request
		 * @param ticket
		 *            The {@link Ticket} object
		 * @param metadata
		 *            The NBT metadata for the request
		 * @param expiry
		 *            The number of ticks in which this object will expire, an
		 *            approximate value. The number of ticks which may elapse
		 *            may be more or less than this value (usually slightly
		 *            less).
		 */
		public ChunkLoadRequest(String name, Ticket ticket, NBTTagCompound metadata, int expiry) {
			this.name = name;
			this.ticket = ticket;
			this.metadata = metadata;
			max_age = expiry;
		}

		public String name() {
			return name;
		}

		public boolean expired() {
			return age > max_age;
		}

		public void tick() {
			if (max_age >= age)
				age++;
			if (age > max_age)
				if (BuildInfo.CHUNK_DEBUGGING)
					LanteaCraft.getLogger().log(Level.INFO,
							String.format("Request %s expired, waiting for kill...", name));
		}

		public void expireNow() {
			age = max_age + 1;
			if (BuildInfo.CHUNK_DEBUGGING)
				LanteaCraft.getLogger().log(Level.INFO,
						String.format("Request %s expired on demand, waiting for kill...", name));
		}

		public void extend(int ticks) {
			if (BuildInfo.CHUNK_DEBUGGING)
				LanteaCraft.getLogger()
						.log(Level.INFO, String.format("Request %s extending by %s ticks.", name, ticks));
			max_age += ticks;
		}

		public List<ChunkCoordIntPair> chunksIn() {
			ArrayList<ChunkCoordIntPair> chunks = new ArrayList<ChunkCoordIntPair>();
			int minX = metadata.getInteger("minX");
			int minZ = metadata.getInteger("minZ");
			int maxX = metadata.getInteger("maxX");
			int maxZ = metadata.getInteger("maxZ");
			for (int i = minX; i <= maxX; i++)
				for (int j = minZ; j <= maxZ; j++)
					chunks.add(new ChunkCoordIntPair(i, j));
			return chunks;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof ChunkLoadRequest) {
				ChunkLoadRequest req = (ChunkLoadRequest) o;
				return req.name.equals(name) && req.max_age == max_age && req.age == age;
			} else if (o instanceof NBTTagCompound) {
				NBTTagCompound compound = (NBTTagCompound) o;
				if (!compound.hasKey("minX") || !compound.hasKey("minZ"))
					return false;
				if (!compound.hasKey("maxX") || !compound.hasKey("maxZ"))
					return false;
				if (compound.getInteger("minX") != metadata.getInteger("minX"))
					return false;
				if (compound.getInteger("minZ") != metadata.getInteger("minZ"))
					return false;
				if (compound.getInteger("maxX") != metadata.getInteger("maxX"))
					return false;
				if (compound.getInteger("maxZ") != metadata.getInteger("maxZ"))
					return false;
				return true;
			} else
				return false;
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
	public ChunkLoadRequest create(String name, World world, int maxAge, NBTTagCompound metadata) {
		if (BuildInfo.CHUNK_DEBUGGING && world != null && world.provider != null)
			LanteaCraft.getLogger().log(Level.INFO,
					String.format("RemoteChunkLoading CSR: %s (world: %s)", name, world.provider.dimensionId));
		if (BuildInfo.CHUNK_DEBUGGING && (world == null || world.provider == null))
			LanteaCraft.getLogger().log(Level.WARN, String.format("RemoteChunkLoading CSR: %s (no provider!!)", name));
		synchronized (requests) {
			for (ChunkLoadRequest request : requests)
				if (request.equals(metadata)) {
					request.extend(maxAge);
					if (BuildInfo.CHUNK_DEBUGGING)
						LanteaCraft.getLogger().log(Level.INFO,
								String.format("RemoteChunkLoading CSR: returning cached request for CSR %s", name));
					return request;
				}
		}
		Ticket ticket = ForgeChunkManager.requestTicket(LanteaCraft.getInstance(), world, Type.NORMAL);
		if (ticket == null) {
			if (BuildInfo.CHUNK_DEBUGGING)
				LanteaCraft.getLogger().log(Level.WARN, String.format("Ticket request failed, null result!"));
			return null;
		}

		ChunkLoadRequest request = new ChunkLoadRequest(name, ticket, metadata, maxAge);
		for (ChunkCoordIntPair chunk : request.chunksIn()) {
			if (BuildInfo.CHUNK_DEBUGGING)
				LanteaCraft.getLogger().log(Level.WARN,
						String.format("Forcing chunk (%s, %s)", chunk.chunkXPos, chunk.chunkZPos));
			ForgeChunkManager.forceChunk(ticket, chunk);
			IChunkProvider provider = world.getChunkProvider();
			if (provider.provideChunk(chunk.chunkXPos, chunk.chunkZPos) instanceof EmptyChunk)
				provider.loadChunk(chunk.chunkXPos, chunk.chunkZPos);
		}

		synchronized (requests) {
			requests.add(request);
		}
		if (BuildInfo.CHUNK_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO, String.format("RemoteChunkLoading CSR success: %s", name));
		return request;
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

	/**
	 * Removes a chunkload request from the agent. This also tells
	 * ForgeChunkManager to remove the chunkloading ticket from the engine,
	 * hence freeing other requests for us to use at a later time as needed.
	 * 
	 * @param request
	 *            The request to release and dispose.
	 */
	private void remove(ChunkLoadRequest request) {
		if (BuildInfo.CHUNK_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO,
					String.format("RemoteChunkLoading remove request: %s", request.name));
		if (request.ticket != null) {
			if (BuildInfo.CHUNK_DEBUGGING)
				LanteaCraft.getLogger().log(Level.INFO,
						String.format("Requesting FCM release ticket %s.", request.ticket.toString()));
			ForgeChunkManager.releaseTicket(request.ticket);
		}
		WorldServer ws = (WorldServer) request.ticket.world;
		for (ChunkCoordIntPair chunk : request.chunksIn())
			if (!arePlayersWatchingChunk(ws, chunk))
				ws.theChunkProviderServer.unloadChunksIfNotNearSpawn(chunk.chunkXPos, chunk.chunkZPos);
		synchronized (requests) {
			requests.remove(request);
		}
	}

}
