package lc.server;

import java.util.ArrayList;

import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import lc.LCRuntime;
import lc.api.components.ComponentType;
import lc.api.stargate.MessagePayload;
import lc.api.stargate.StargateAddress;
import lc.api.stargate.StargateConnectionType;
import lc.api.stargate.StargateState;
import lc.common.LCLog;
import lc.common.base.multiblock.MultiblockState;
import lc.common.configuration.xml.ConfigHelper;
import lc.common.util.ScanningHelper;
import lc.common.util.math.ChunkPos;
import lc.common.util.math.Vector3;
import lc.server.database.StargateRecord;
import lc.server.world.LCLoadedChunkManager.LCChunkTicket;
import lc.tiles.TileStargateBase;

public class StargateConnection {

	public final StargateConnectionType type;
	public final StargateAddress source;
	public final StargateRecord dest;

	private int maxConnectionAge;

	public TileStargateBase tileFrom, tileTo;
	public LCChunkTicket ticketFrom, ticketTo;

	/** The Stargate state */
	public StargateState state;
	/** The timeout until this state ends */
	public int stateTimeout = 0;

	/** The dial-progress */
	public int diallingProgress = 0;
	/** The currently dialling symbol */
	public int diallingSymbol = 0;
	/** The dial-state timeout */
	public int diallingTimeout = 0;

	/** The connection live state */
	public boolean dead = false;

	public StargateConnection(StargateConnectionType type, TileStargateBase tileFrom, StargateRecord what) {
		this.type = type;
		this.tileFrom = tileFrom;
		this.source = tileFrom.getStargateAddress();
		this.dest = what;
		this.state = StargateState.IDLE;
		this.ticketFrom = ((HintProviderServer) LCRuntime.runtime.hints()).chunkLoaders().requestTicket(
				tileFrom.getWorldObj());
		ChunkPos origin = new ChunkPos(tileFrom);
		ticketFrom.loadChunkRange(origin, -1, -1, 1, 1);
		this.maxConnectionAge = Integer.parseInt((String) ConfigHelper.getOrSetParam(
				LCRuntime.runtime.config().config(ComponentType.STARGATE), "Time", "Stargate", "maxConnectionAge",
				"Maximum connection age in ticks", 6000));
	}

	public void think() {
		if (dead)
			return;
		switch (state) {
		case CONNECTED:
			thinkConnection();
			break;
		case DIALLING:
			thinkSpinUp();
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
		if (stateTimeout <= 0)
			changeState(StargateState.DISCONNECTING, 60);
	}

	private void thinkSpinUp() {
		diallingTimeout--;
		if (diallingTimeout <= 0) {
			diallingProgress++;
			if (diallingProgress >= dest.address.getAddress().length) {
				thinkPerformConnection();
			} else {
				diallingSymbol = dest.address.getAddress()[diallingProgress];
				diallingTimeout += 40;
				sendUpdates();
				thinkFindTile();
			}
		}
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
		try {
			if (tileFrom == null || tileTo == null)
				throw new IllegalStateException("Can't connect to no Stargate.");
			if (!tileFrom.hasConnectionState() || !tileTo.hasConnectionState())
				throw new IllegalStateException("Can't connect to unnotified Stargate.");
			if (tileFrom.getConnectionState() != this || tileTo.getConnectionState() != this)
				throw new IllegalStateException("Stargate is busy with non-self connection.");
			changeState(StargateState.CONNECTED, maxConnectionAge);
		} catch (Exception e) {
			LCLog.debug("Can't connect to Stargate.", e);
			changeState(StargateState.FAILED, 60);
		}
	}

	private void changeState(StargateState state, int stateTimeout) {
		this.state = state;
		this.stateTimeout = stateTimeout;
		sendUpdates();
	}

	public void sendUpdates() {
		if (tileFrom != null)
			tileFrom.notifyConnectionState(this);
		if (tileTo != null)
			tileTo.notifyConnectionState(this);
	}

	public void deleteConnection() {
		if (tileFrom != null)
			tileFrom.notifyConnectionState(null);
		if (tileTo != null)
			tileTo.notifyConnectionState(null);
		tileFrom = null;
		tileTo = null;
		dead = true;
	}

	public void openConnection() {
		if (state != StargateState.IDLE)
			return;
		diallingTimeout = 40;
		diallingSymbol = dest.address.getAddress()[0];
		changeState(StargateState.DIALLING, 0);
	}

	public void closeConnection() {
		if (state != StargateState.CONNECTED)
			return;
		changeState(StargateState.DISCONNECTING, 60);
	}

	public void shutdown() {
		state = StargateState.IDLE;
		deleteConnection();
	}

	public void transmit(TileStargateBase source, MessagePayload payload) {
		if (source == tileFrom)
			tileTo.receive(payload);
		if (source == tileTo)
			tileFrom.receive(payload);

	}
}
