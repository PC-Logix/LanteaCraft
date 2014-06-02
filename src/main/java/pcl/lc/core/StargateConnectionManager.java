package pcl.lc.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.logging.Level;

import net.afterlifelochie.sandbox.WatchedValue;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import pcl.common.util.Vector3;
import pcl.common.util.WorldLocation;
import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumStargateState;
import pcl.lc.api.internal.ITickAgent;
import pcl.lc.core.RemoteChunkLoading.ChunkLoadRequest;
import pcl.lc.tileentity.TileEntityStargateBase;

public class StargateConnectionManager implements ITickAgent {

	public final static int diallingTime = 40;
	public final static int interDiallingTime = 10;
	public final static int transientDuration = 20;
	public final static int disconnectTime = 30;

	/**
	 * Used to simulate a connection on a server.
	 * 
	 * @author AfterLifeLochie
	 */
	public static class ConnectionRequest {
		/* The local and target addresses */
		public String hostAddress, clientAddress;
		/* The location for the request */
		public WorldLocation hostLocation, clientLocation;
		/* The chunkloaders for the request */
		public ChunkLoadRequest hostChunkLoader, clientChunkLoader;
		/* The tiles associated with the request */
		public WeakReference<TileEntityStargateBase> hostTile, clientTile;
		/*
		 * The absolute location of each tile once found, so that we can yank
		 * them back in the event we lose a reference.
		 */
		public Vector3 hostTilePos, clientTilePos;
		/* The worlds associated with the request */
		public World hostWorld, clientWorld;
		/* The name of the request */
		public String name, hostName, clientName;

		/* Whether the request is currently running */
		public WatchedValue<Boolean> running = new WatchedValue<Boolean>(true);
		/* The number of total ticks the request has been running for */
		public int ticks = 0;
		/* The current state of the connection */
		public WatchedValue<EnumStargateState> state = new WatchedValue<EnumStargateState>(EnumStargateState.Idle);
		/* The current symbol being dialled */
		public WatchedValue<Character> symbol = new WatchedValue<Character>(' ');
		/* The number of chevrons dialled */
		public WatchedValue<Integer> chevrons = new WatchedValue<Integer>(0);
		/* The remaining number of ticks to remain in this state */
		public int ticksRemaining = 0;

		private ConnectionRequest(String hostAddress, String clientAddress, WorldLocation hostLocation,
				WorldLocation clientLocation, String name) {
			this.hostAddress = hostAddress;
			this.hostLocation = hostLocation;
			this.clientAddress = clientAddress;
			this.clientLocation = clientLocation;
			this.name = name;
			hostName = String.format("%s-Host-%s", name, hostLocation.toString());
			clientName = String.format("%s-Client-%s", name, clientLocation.toString());
		}

		public void setup(TileEntityStargateBase host) {
			RemoteChunkLoading loader = LanteaCraft.getProxy().getRemoteChunkManager();
			hostTile = new WeakReference<TileEntityStargateBase>(host);
			hostTilePos = new Vector3(host);
			host.setConnection(this);
			/* Prevent the initiator from unloading */
			hostWorld = GateAddressHelper.getWorld(hostLocation.dimension);
			if (hostWorld != null)
				hostChunkLoader = loader.create(hostName, hostWorld, TileEntityStargateBase.ticksToStayOpen,
						createRadiusOf(hostLocation, 1));

			/* Load the remote target chunks */
			clientWorld = GateAddressHelper.getWorld(clientLocation.dimension);
			if (clientWorld != null)
				clientChunkLoader = loader.create(clientName, clientWorld, TileEntityStargateBase.ticksToStayOpen,
						createRadiusOf(clientLocation, 1));
			/* Start dialling */
			runState(EnumStargateState.Dialling, diallingTime);
		}

		public void advance() {
			if (!running.get())
				return;
			ticks++;
			/* If we have no remote tile reference, attempt to find one */
			if ((clientTile == null || clientTile.get() == null) && clientWorld != null) {
				Chunk chunk = clientWorld.getChunkFromBlockCoords(clientLocation.x, clientLocation.z);
				if (chunk != null)
					if (clientTilePos == null) {
						for (Object o : chunk.chunkTileEntityMap.values())
							if (o instanceof TileEntityStargateBase) {
								TileEntityStargateBase tile = (TileEntityStargateBase) o;
								clientTile = new WeakReference<TileEntityStargateBase>(tile);
								clientTilePos = new Vector3(tile);
								tile.setConnection(this);
							}
					} else {
						Object o = chunk.worldObj.getBlockTileEntity(clientTilePos.floorX(), clientTilePos.floorY(),
								clientTilePos.floorZ());
						if (o != null && (o instanceof TileEntityStargateBase)) {
							TileEntityStargateBase tile = (TileEntityStargateBase) o;
							clientTile = new WeakReference<TileEntityStargateBase>(tile);
							clientTilePos = new Vector3(tile);
							tile.setConnection(this);
						}
					}
			}

			if (ticksRemaining > 0) {
				if (state.get() == EnumStargateState.Transient) {
					hostTile.get().performTransientDamage();
					clientTile.get().performTransientDamage();
				}
				--ticksRemaining;
			} else
				switch (state.get()) {
				case InterDialling: // Any dial_wait state -> any dial state
					symbol.set(nextChevron());
					runState(EnumStargateState.Dialling, diallingTime);
					break;
				case Dialling: // Any dial state -> any idle_wait state
					chevrons.set(chevrons.get() + 1);
					if (clientAddress.length() > chevrons.get())
						runState(EnumStargateState.InterDialling, interDiallingTime);
					else if (clientTile != null)
						runState(EnumStargateState.Transient, transientDuration);
					else {
						if (BuildInfo.DEBUG)
							LanteaCraft.getLogger().log(Level.WARNING, "Cannot find host tile, aborting!");
						requestDisconnect();
					}
					break;
				case Transient: // Any transient state -> any connected state
					runState(EnumStargateState.Connected, TileEntityStargateBase.ticksToStayOpen);
					break;
				case Connected: // Any connected state -> any disconnected state
					requestDisconnect();
					break;
				case Disconnecting: // Any disconnected state -> idle
					state.set(EnumStargateState.Idle);
					shutdown();
					break;
				}
			if (state.get() == EnumStargateState.Connected)
				if (!hostTile.get().useEnergy(1))
					hostTile.get().disconnect();
		}

		public char nextChevron() {
			return clientAddress.charAt(chevrons.get());
		}

		public void runState(EnumStargateState state, int timeout) {
			if (BuildInfo.DEBUG)
				LanteaCraft.getLogger().log(Level.INFO,
						String.format("Stargate transitioning to state %s for %s ticks.", state, timeout));
			this.state.set(state);
			ticksRemaining = timeout;
		}

		public void requestDisconnect() {
			runState(EnumStargateState.Disconnecting, disconnectTime);
		}

		public void shutdown() {
			running.set(false);
			state.set(EnumStargateState.Idle);
			/* Drop all worldly references! */
			hostTile = clientTile = null;
			hostWorld = clientWorld = null;
			/* Flush the chunk loaders */
			if (clientChunkLoader != null)
				clientChunkLoader.expireNow();
			if (hostChunkLoader != null)
				hostChunkLoader.expireNow();
		}

		private NBTTagCompound createRadiusOf(WorldLocation location, int radius) {
			NBTTagCompound result = new NBTTagCompound();
			result.setInteger("minX", location.x - radius);
			result.setInteger("minZ", location.z - radius);
			result.setInteger("maxX", location.x + radius);
			result.setInteger("maxZ", location.z + radius);
			return result;
		}

		public boolean isHost(TileEntityStargateBase that) {
			if (hostTile == null || hostTile.get() == null)
				return false;
			return hostTile.get().equals(that);
		}
	}

	/**
	 * The container for all current requests in the agent.
	 */
	private ArrayList<ConnectionRequest> requests = new ArrayList<ConnectionRequest>();
	/**
	 * The container for all expired requests which have yet to be removed from
	 * the agent.
	 */
	private ArrayList<ConnectionRequest> expiredRequests = new ArrayList<ConnectionRequest>();

	public ConnectionRequest create(String hostAddress, String clientAddress, WorldLocation hostLocation,
			WorldLocation clientLocation, TileEntityStargateBase hostTile, String name) {
		ConnectionRequest request = new ConnectionRequest(hostAddress, clientAddress, hostLocation, clientLocation,
				name);
		request.setup(hostTile);
		synchronized (requests) {
			requests.add(request);
		}
		return request;
	}

	@Override
	public void advance() {
		synchronized (requests) {
			for (ConnectionRequest request : requests) {
				request.advance();
				if (!request.running.get())
					expiredRequests.add(request);
			}
		}

		if (expiredRequests.size() > 0) {
			for (ConnectionRequest expired : expiredRequests)
				remove(expired);
			expiredRequests.clear();
		}
	}

	private void remove(ConnectionRequest request) {
		if (BuildInfo.CHUNK_DEBUGGING)
			LanteaCraft.getLogger()
					.log(Level.INFO, String.format("ConnectionRequest remove request: %s", request.name));
		// Confirm a shutdown was invoked
		request.shutdown();
		synchronized (requests) {
			requests.remove(request);
		}
	}

}
