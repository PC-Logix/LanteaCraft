package lc.server.stargate;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import lc.LCRuntime;
import lc.api.stargate.MessagePayload;
import lc.api.stargate.StargateAddress;
import lc.api.stargate.StargateConnectionType;
import lc.api.stargate.StargateState;
import lc.common.LCLog;
import lc.common.base.multiblock.MultiblockState;
import lc.common.util.java.DestructableReferenceQueue;
import lc.common.util.math.ChunkPos;
import lc.server.HintProviderServer;
import lc.server.world.LCLoadedChunkManager.LCChunkTicket;
import lc.tiles.TileStargateBase;

/**
 * Stargate connection emulation class
 * 
 * @author AfterLifeLochie
 *
 */
public class StargateConnection {

	/** Connection type */
	public final StargateConnectionType type;
	/** Source Stargate address */
	public final StargateAddress source;
	/** Destination Stargate address */
	public final StargateRecord dest;

	private int maxConnectionAge;
	private int maxTimeout;

	/** Source tile Stargate */
	public TileStargateBase tileFrom;
	/** Destination tile Stargate */
	public TileStargateBase tileTo;
	/** Source tile chunk loader */
	public LCChunkTicket ticketFrom;
	/** Destination tile chunk loader */
	public LCChunkTicket ticketTo;

	/** The Stargate state */
	public StargateState state;
	/** The timeout until this state ends */
	public int stateTimeout = 0;

	/** The connection live state */
	public boolean dead = false;

	/**
	 * Create a Stargate connection
	 * 
	 * @param type
	 *            The connection type
	 * @param tileFrom
	 *            The source tile
	 * @param what
	 *            The stargate record being connected to
	 */
	public StargateConnection(StargateConnectionType type, TileStargateBase tileFrom, StargateRecord what, int timeout,
			int maxAge) {
		this.type = type;
		this.tileFrom = tileFrom;
		this.source = tileFrom.getStargateAddress();
		this.dest = what;
		this.state = StargateState.IDLE;
		this.ticketFrom = ((HintProviderServer) LCRuntime.runtime.hints()).chunkLoaders().requestTicket(
				tileFrom.getWorldObj());
		this.maxConnectionAge = maxAge;
		this.maxTimeout = timeout;
		ChunkPos origin = new ChunkPos(tileFrom);
		ticketFrom.loadChunkRange(origin, -1, -1, 1, 1);
	}

	/** Update the connection */
	public void think() {
		if (dead)
			return;
		switch (state) {
		case DIALLING:
			thinkPerformConnection();
			break;
		case CONNECTED:
			thinkConnection();
			break;
		case DISCONNECTING:
		case FAILED:
			thinkSpinDown();
			break;
		case IDLE:
			thinkIdle();
			break;
		}
	}

	private void thinkConnection() {
		stateTimeout--;
		if (tileFrom == null || tileTo == null)
			stateTimeout = 0;
		if (DestructableReferenceQueue.queued(tileFrom) || DestructableReferenceQueue.queued(tileTo))
			stateTimeout = 0;
		if (tileFrom != null && tileTo != null) {
			if (tileFrom.getState() != MultiblockState.FORMED)
				stateTimeout = 0;
			if (tileTo.getState() != MultiblockState.FORMED)
				stateTimeout = 0;
		}
		if (stateTimeout <= 0)
			changeState(StargateState.DISCONNECTING, 60);
	}

	private void thinkFindTile() {
		if (tileTo != null)
			return;
		try {
			WorldServer world = MinecraftServer.getServer().worldServerForDimension(dest.dimension);
			if (world == null) {
				LCLog.debug("Can't find world %s", dest.dimension);
				return;
			}
			if (ticketTo == null) {
				ticketTo = ((HintProviderServer) LCRuntime.runtime.hints()).chunkLoaders().requestTicket(world);
				ticketTo.loadChunkRange(dest.chunk, -1, -1, 1, 1);
			}
			Chunk chunk = world.getChunkFromChunkCoords(dest.chunk.cx, dest.chunk.cz);
			if (chunk == null) {
				LCLog.debug("Can't find chunk [%s, %s]", dest.chunk.cx, dest.chunk.cz);
				return;
			}

			for (Object o : chunk.chunkTileEntityMap.values())
				if (o instanceof TileStargateBase) {
					TileStargateBase tile = (TileStargateBase) o;
					if (tile.getState() == MultiblockState.FORMED && !tile.hasConnectionState()) {
						LCLog.debug("Found Stargate in chunk [%s, %s]", dest.chunk.cx, dest.chunk.cz);
						tileTo = tile;
						return;
					}
				}

			LCLog.debug("Failed to find Stargate in chunk [%s, %s]", dest.chunk.cx, dest.chunk.cz);
		} catch (Exception e) {
			// TODO: We need to actually sample some errors here
			LCLog.warn("Problem scanning for Stargate.", e);
		}
	}

	private void thinkSpinDown() {
		stateTimeout--;
		if (stateTimeout <= 0)
			changeState(StargateState.IDLE, 0);
	}

	private void thinkIdle() {
		deleteConnection();
	}

	private void thinkPerformConnection() {
		stateTimeout--;
		thinkFindTile();
		try {
			if (tileFrom != null && tileTo != null) {
				if (tileFrom.getConnectionState() != null && tileFrom.getConnectionState() != this)
					throw new IllegalStateException("Source Stargate is busy with non-self connection.");
				if (tileTo.getConnectionState() != null && tileTo.getConnectionState() != this)
					throw new IllegalStateException("Destination Stargate is busy with non-self connection.");
				changeState(StargateState.CONNECTED, maxConnectionAge);
			}
		} catch (Exception e) {
			LCLog.debug("Can't connect to Stargate.", e);
		}
		if (stateTimeout <= 0)
			changeState(StargateState.FAILED, 60);
	}

	private void changeState(StargateState state, int stateTimeout) {
		this.state = state;
		this.stateTimeout = stateTimeout;
		LCLog.debug("Going to state %s (timeout %s)", state, stateTimeout);
		sendUpdates();
	}

	/** Update the connection */
	public void sendUpdates() {
		if (tileFrom != null)
			tileFrom.notifyConnectionState(this);
		if (tileTo != null)
			tileTo.notifyConnectionState(this);
	}

	/** Dispose the connection */
	public void deleteConnection() {
		if (tileFrom != null)
			tileFrom.notifyConnectionState(null);
		if (tileTo != null)
			tileTo.notifyConnectionState(null);
		tileFrom = null;
		tileTo = null;
		if (ticketTo != null) {
			ticketTo.unload();
			((HintProviderServer) LCRuntime.runtime.hints()).chunkLoaders().closeTicket(ticketTo);
			ticketTo = null;
		}
		if (ticketFrom != null) {
			ticketFrom.unload();
			((HintProviderServer) LCRuntime.runtime.hints()).chunkLoaders().closeTicket(ticketFrom);
			ticketFrom = null;
		}
		LCLog.debug("Connection closed!");
		dead = true;
	}

	/** Open the connection */
	public void openConnection() {
		if (state != StargateState.IDLE)
			return;
		changeState(StargateState.DIALLING, maxTimeout);
	}

	/**
	 * Close the connection
	 * 
	 * @param source
	 *            The source of the request
	 */
	public boolean closeConnection(TileStargateBase source) {
		if (state != StargateState.CONNECTED)
			return false;
		if (source == tileFrom) {
			changeState(StargateState.DISCONNECTING, 60);
			return true;
		}
		return false;
	}

	/** Shut down the connection */
	public void shutdown() {
		state = StargateState.IDLE;
		deleteConnection();
	}

	/**
	 * Transmit data across the Stargate
	 * 
	 * @param source
	 *            The source tile
	 * @param payload
	 *            The data payload
	 */
	public void transmit(TileStargateBase source, MessagePayload payload) {
		if (source == tileFrom)
			tileTo.receive(payload);
		if (source == tileTo)
			tileFrom.receive(payload);

	}
}
