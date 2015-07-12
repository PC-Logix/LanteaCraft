package lc.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.EnumMap;
import java.util.List;

import lc.common.LCLog;
import lc.common.network.packets.abs.LCTargetPacket;
import lc.common.util.math.DimensionPos;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.network.ForgeMessage;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * LanteaCraft network connection driver.
 *
 * @author AfterLifeLochie
 *
 */
@ChannelHandler.Sharable
public class LCPacketPipeline extends MessageToMessageCodec<FMLProxyPacket, LCPacket> {

	/** The channels currently controlled by this pipe */
	protected EnumMap<Side, FMLEmbeddedChannel> channels;
	private final LCNetworkController controller;

	/**
	 * Create a new packet pipeline.
	 * 
	 * @param controller
	 *            The network controller instance to bind.
	 */
	public LCPacketPipeline(LCNetworkController controller) {
		this.controller = controller;
	}

	/**
	 * Initializes the network handler
	 *
	 * @param channelName
	 *            The channel name to listen on
	 */
	public void init(String channelName) {
		channels = NetworkRegistry.INSTANCE.newChannel(channelName, this);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, LCPacket msg, List<Object> out) throws Exception {
		try {
			ByteBuf buffer = Unpooled.buffer();
			controller.encodePacket(msg, buffer);
			FMLProxyPacket proxyPacket = new FMLProxyPacket(buffer, ctx.channel().attr(NetworkRegistry.FML_CHANNEL)
					.get());
			out.add(proxyPacket);
		} catch (Exception e) {
			LCLog.fatal("Network encode exception.", e);
			throw e;
		}
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
		try {
			ByteBuf payload = msg.payload();
			LCPacket packet = controller.decodePacket(payload);
			EntityPlayer player;
			switch (FMLCommonHandler.instance().getEffectiveSide()) {
			case CLIENT:
				player = getClientPlayer();
				break;
			case SERVER:
				INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
				player = ((NetHandlerPlayServer) netHandler).playerEntity;
				break;
			default:
				throw new LCNetworkException("Instance is not client or server. Cannot continue!");
			}
			if (msg.getTarget() == Side.SERVER)
				controller.serverQueue.queue(packet, msg.getTarget(), player);
			if (msg.getTarget() == Side.CLIENT)
				controller.clientQueue.queue(packet, msg.getTarget(), player);
		} catch (Exception e) {
			LCLog.fatal("Network decode exception on side %s, packet dropped.", FMLCommonHandler.instance()
					.getEffectiveSide(), e);
		}
	}

	@SideOnly(Side.CLIENT)
	private EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	/**
	 * Send a packet to all players
	 *
	 * @param message
	 *            The packet
	 */
	public void sendToAll(LCPacket message) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		channels.get(Side.SERVER).writeAndFlush(message);
	}

	/**
	 * Send a packet to a player
	 *
	 * @param message
	 *            The packet
	 * @param player
	 *            The player
	 */
	public void sendTo(LCPacket message, EntityPlayerMP player) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET)
				.set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		channels.get(Side.SERVER).writeAndFlush(message);
	}

	/**
	 * Send an FML packet to a player
	 *
	 * @param message
	 *            The packet
	 * @param player
	 *            The player
	 */
	public void sendForgeMessageTo(ForgeMessage message, EntityPlayerMP player) {
		FMLEmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
		channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		channel.writeAndFlush(message);
	}

	/**
	 * Send a packet to all players in range of a location
	 *
	 * @param message
	 *            The packet
	 * @param location
	 *            The location
	 * @param range
	 *            The range
	 */
	public void sendToAllAround(LCPacket message, DimensionPos location, double range) {
		TargetPoint point = new TargetPoint(location.dimension, location.x, location.y, location.z, range);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET)
				.set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
		channels.get(Side.SERVER).writeAndFlush(message);
	}

	/**
	 * Send a packet to all players in a dimension
	 *
	 * @param message
	 *            The packet
	 * @param dimensionId
	 *            The dimension ID
	 */
	public void sendToDimension(LCPacket message, int dimensionId) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET)
				.set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
		channels.get(Side.SERVER).writeAndFlush(message);
	}

	/**
	 * Send a packet to the server
	 *
	 * @param message
	 *            The packet
	 */
	public void sendToServer(LCPacket message) {
		channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET)
				.set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		channels.get(Side.CLIENT).writeAndFlush(message);
	}

	/**
	 * Send a packet to all players or to players in range if the packet
	 * supports a target
	 *
	 * @param packet
	 *            The packet
	 * @param range
	 *            The maximum range if supported
	 */
	public void sendScoped(LCPacket packet, double range) {
		if (packet instanceof LCTargetPacket)
			sendToAllAround(packet, ((LCTargetPacket) packet).target, range);
		else
			sendToAll(packet);
	}
}
