package lc.common.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.WeakHashMap;

import lc.BuildInfo;
import lc.LCRuntime;
import lc.api.event.ITickEventHandler;
import lc.common.LCLog;
import lc.common.network.packets.LCServerToServerEnvelope;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class LCNetworkController implements ITickEventHandler {

	/** The network pipe */
	private final LCPacketPipeline pipe = new LCPacketPipeline(this);
	/** The server-side packet incoming data queue */
	protected final LCNetworkQueue serverQueue = new LCNetworkQueue(this, "Client queue");
	/** The client-side packet incoming data queue */
	protected final LCNetworkQueue clientQueue = new LCNetworkQueue(this, "Server queue");
	/** Client server-to-server envelope buffer */
	protected final LCPacketBuffer<LCServerToServerEnvelope> envelopeBuffer = new LCPacketBuffer<LCServerToServerEnvelope>();
	/** Client player */
	protected final LCNetworkPlayer clientPlayer = new LCNetworkPlayer(this);
	/** Server player-state tracker */
	protected final WeakHashMap<EntityPlayerMP, LCNetworkPlayer> players = new WeakHashMap<EntityPlayerMP, LCNetworkPlayer>();

	/** Default constructor */
	public LCNetworkController() {
		// TODO Auto-generated constructor stub
	}

	public void init(LCRuntime runtime, FMLInitializationEvent event) {
		pipe.init(BuildInfo.modID);
		runtime.ticks().register(this);
	}

	/**
	 * Gets the currently preferred network pipe.
	 * 
	 * @return The currently preferred network pipe to use.
	 */
	public LCPacketPipeline getPreferredPipe() {
		return pipe;
	}

	public void encodePacket(LCPacket packet, ByteBuf stream) throws IOException {
		Class<? extends LCPacket> clazz = packet.getClass();
		LCPacket.encodePrimitiveInto(stream, clazz.getName());
		packet.encodeInto(stream);
	}

	public LCPacket decodePacket(ByteBuf stream) throws IOException {
		String clazzName = (String) LCPacket.decodePrimitiveFrom(stream);
		try {
			Class<? extends LCPacket> clazz = (Class<? extends LCPacket>) Class.forName(clazzName);
			LCPacket packet = clazz.newInstance();
			packet.decodeFrom(stream.slice());
			return packet;
		} catch (Exception ex) {
			if (ex instanceof IOException)
				throw (IOException) ex;
			throw new IOException("Decoding exception", ex);
		}
	}

	@Override
	public void think(Side what) {
		if (what == Side.SERVER)
			serverQueue.think(what);
		if (what == Side.CLIENT)
			clientQueue.think(what);
	}

	public void injectPacket(Side side, LCPacket packet, EntityPlayer player) {
		if (side == Side.CLIENT)
			clientQueue.queue(packet, side, player);
		if (side == Side.SERVER)
			serverQueue.queue(packet, side, player);
	}

	public void playerConnected(EntityPlayerMP player) {
		if (!players.containsKey(player))
			players.put(player, new LCNetworkPlayer(this));
		LCLog.info("Sending LanteaCraft server handshake to client.");
		players.get(player).sendHandshake(player);
	}

	public void playerDisconnected(EntityPlayerMP player) {
		players.remove(player);
	}

	public void serverShutdown() {
		players.clear();
	}
}
