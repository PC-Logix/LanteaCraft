package pcl.lc.module.stargate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.apache.logging.log4j.Level;

import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumStargateState;
import pcl.lc.api.access.IStargateConnectionAccess;
import pcl.lc.api.access.IStargateManagementAccess;
import pcl.lc.api.internal.ITickAgent;
import pcl.lc.base.data.WatchedValue;
import pcl.lc.cfg.ConfigHelper;
import pcl.lc.cfg.ModuleConfig;
import pcl.lc.core.RemoteChunkLoading;
import pcl.lc.core.RemoteChunkLoading.ChunkLoadRequest;
import pcl.lc.module.stargate.tile.TileStargateBase;
import pcl.lc.util.Vector3;
import pcl.lc.util.WorldLocation;

public class StargateConnectionManager implements ITickAgent, IStargateManagementAccess {

	public final static int diallingTime = 40;
	public final static int interDiallingTime = 10;
	public final static int transientDuration = 20;
	public final static int disconnectTime = 30;
	public final static int abortTime = 40;

	public static boolean canPowerFromEitherEnd = true;
	public static boolean canGatesCrossTypes = true;
	public static float costInitialize = 1.0f;
	public static float costPerTick = 0.1f;
	public static float costPentaltyCrossDimension = 1.1f;
	public static int secondsPerTick = 1;

	public static void configure(ModuleConfig config) {
		canPowerFromEitherEnd = ConfigHelper.getOrSetBooleanParam(config, "Power", "powerFromEitherEnd", "enabled",
				"Can the Stargate draw power from either end when connected?", canPowerFromEitherEnd);
		canGatesCrossTypes = ConfigHelper.getOrSetBooleanParam(config, "Connection", "canGatesCrossTypes", "enabled",
				"Can a Stargate connect to a Stargate of another type?", canGatesCrossTypes);
		costInitialize = Float.parseFloat(ConfigHelper.getOrSetParam(config, "Power", "costInitialize", "value",
				"The energy cost when a Stargate opens a connection.", costInitialize).toString());
		costPerTick = Float.parseFloat(ConfigHelper.getOrSetParam(config, "Power", "costPerTick", "value",
				"The energy cost per energy tick to maintain the connction.", costPerTick).toString());
		costPentaltyCrossDimension = Float.parseFloat(ConfigHelper.getOrSetParam(config, "Power",
				"costPentaltyCrossDimension", "value", "The energy cost penalty if the connection crosses dimensions.",
				costPentaltyCrossDimension).toString());
		secondsPerTick = Integer.parseInt(ConfigHelper.getOrSetParam(config, "Power", "secondsPerTick", "value",
				"The number of seconds between each energy cost tick.", secondsPerTick).toString());
	}

	/**
	 * Used to simulate a connection on a server.
	 * 
	 * @author AfterLifeLochie
	 */
	public static class ConnectionRequest implements IStargateConnectionAccess {
		/* The local and target addresses */
		public String hostAddress, clientAddress;
		/* The location for the request */
		public WorldLocation hostLocation, clientLocation;
		/* The chunkloaders for the request */
		public ChunkLoadRequest hostChunkLoader, clientChunkLoader;
		/* The tiles associated with the request */
		public WeakReference<TileStargateBase> hostTile, clientTile;
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

		/* The computed cost per tick this gate will draw */
		public float energyPerTick = StargateConnectionManager.costPerTick;
		/* The computed cost for initialization this gate will draw */
		public float energyForInit = StargateConnectionManager.costInitialize;
		/* The number of ticks until the next energy update */
		public int energyTicksRemaining = 0;

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

		public void setup(TileStargateBase host) {
			RemoteChunkLoading loader = LanteaCraft.getProxy().getRemoteChunkManager();
			hostTile = new WeakReference<TileStargateBase>(host);
			hostTilePos = new Vector3(host);
			host.setConnection(this);
			/* Prevent the initiator from unloading */
			hostWorld = GateAddressHelper.getWorld(hostLocation.dimension);
			if (hostWorld != null)
				hostChunkLoader = loader.create(hostName, hostWorld, TileStargateBase.ticksToStayOpen,
						createRadiusOf(hostLocation, 1));

			/* Load the remote target chunks */
			clientWorld = GateAddressHelper.getWorld(clientLocation.dimension);
			if (clientWorld != null)
				clientChunkLoader = loader.create(clientName, clientWorld, TileStargateBase.ticksToStayOpen,
						createRadiusOf(clientLocation, 1));

			energyTicksRemaining = 20 * StargateConnectionManager.secondsPerTick;
			if (hostLocation.dimension != clientLocation.dimension) {
				energyForInit += StargateConnectionManager.costPentaltyCrossDimension;
				energyPerTick += StargateConnectionManager.costPentaltyCrossDimension;
			}

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
							if (o instanceof TileStargateBase) {
								TileStargateBase tile = (TileStargateBase) o;
								if (!tile.isBusy()) {
									tile.setConnection(this);
									clientTile = new WeakReference<TileStargateBase>(tile);
									clientTilePos = new Vector3(tile);
								}
							}
					} else {
						Object o = chunk.worldObj.getTileEntity(clientTilePos.floorX(), clientTilePos.floorY(),
								clientTilePos.floorZ());
						if (o != null && (o instanceof TileStargateBase)) {
							TileStargateBase tile = (TileStargateBase) o;
							if (!tile.isBusy()) {
								tile.setConnection(this);
								clientTile = new WeakReference<TileStargateBase>(tile);
								clientTilePos = new Vector3(tile);
							}
						}
					}
			}

			if (ticksRemaining > 0) {
				if (state.get() == EnumStargateState.Transient) {
					hostTile.get().performTransientDamage();
					clientTile.get().performTransientDamage();
				}
				--ticksRemaining;
			} else {
				switch (state.get()) {
				case InterDialling: // Any dial_wait state -> any dial state
					symbol.set(nextChevron());
					if (hostTile.get().getAsStructure().isSimpleGate() && chevrons.get() >= 6 && clientAddress.length() != 7) {
						if (BuildInfo.DEBUG)
							LanteaCraft.getLogger().log(Level.WARN,
									"Gate only has seven chevrons, can't dial long addresses, aborting!");
						runState(EnumStargateState.Abort, abortTime);
					} else
						runState(EnumStargateState.Dialling, diallingTime);
					break;
				case Dialling: // Any dial state -> any idle_wait state
					chevrons.set(chevrons.get() + 1);
					if (clientAddress.length() > chevrons.get()) {
						runState(EnumStargateState.InterDialling, interDiallingTime);
					} else if (clientTile != null && clientTile.get() != null) {
						if (!canGatesCrossTypes && clientTile.get().getType() != hostTile.get().getType()) {
							if (BuildInfo.DEBUG)
								LanteaCraft.getLogger().log(Level.WARN,
										"Inter-gate type dialling not allowed, aborting!");
							runState(EnumStargateState.Abort, abortTime);
						}

						runState(EnumStargateState.Transient, transientDuration);
					} else {
						if (BuildInfo.DEBUG)
							LanteaCraft.getLogger().log(Level.WARN, "Cannot find host tile, aborting!");
						runState(EnumStargateState.Abort, abortTime);
					}
					break;
				case Transient: // Any transient state -> any connected state
					if (!hostTile.get().useEnergy(energyForInit))
						runState(EnumStargateState.Abort, abortTime);
					else
						runState(EnumStargateState.Connected, TileStargateBase.ticksToStayOpen);
					break;
				case Connected: // Any connected state -> any disconnected state
					requestDisconnect();
					break;
				case Abort: // Any transient state -> idle
					state.set(EnumStargateState.Idle);
					shutdown();
					break;
				case Disconnecting: // Any disconnected state -> idle
					state.set(EnumStargateState.Idle);
					shutdown();
					break;
				}
			}

			if (state.get() == EnumStargateState.Connected) {
				if (energyTicksRemaining < 0) {
					if (!hostTile.get().useEnergy(energyPerTick))
						if (StargateConnectionManager.canPowerFromEitherEnd && clientTile.get() != null
								&& !clientTile.get().useEnergy(energyPerTick))
							hostTile.get().disconnect();
					energyTicksRemaining = 20 * StargateConnectionManager.secondsPerTick;
				} else
					energyTicksRemaining--;
			}
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

		public boolean isHost(TileStargateBase that) {
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
			WorldLocation clientLocation, TileStargateBase hostTile, String name) {
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

	@Override
	public IStargateConnectionAccess[] getCurrentConnections() {
		return requests.toArray(new IStargateConnectionAccess[0]);
	}

}
