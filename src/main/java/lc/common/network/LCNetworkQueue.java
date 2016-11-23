package lc.common.network;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lc.api.event.ITickEventHandler;
import lc.common.LCLog;
import lc.common.network.packets.LCNetworkHandshake;
import lc.common.network.packets.LCServerToServerEnvelope;
import lc.common.network.packets.abs.LCTargetPacket;
import lc.common.util.Tracer;

public class LCNetworkQueue implements ITickEventHandler {

	private static class QueuedPacket {
		public final LCPacket packet;
		public final Side target;
		public final WeakReference<EntityPlayer> player;

		public QueuedPacket(LCPacket packet, Side target, EntityPlayer player) {
			this.packet = packet;
			this.target = target;
			this.player = new WeakReference<EntityPlayer>(player);
		}
	}

	private final LinkedBlockingQueue<QueuedPacket> queue = new LinkedBlockingQueue<QueuedPacket>();
	private final ArrayList<QueuedPacket> drain = new ArrayList<QueuedPacket>();
	private final LCNetworkController controller;
	private final String name;

	public LCNetworkQueue(LCNetworkController controller, String name) {
		this.controller = controller;
		this.name = name;
	}

	public void queue(LCPacket packet, Side target, EntityPlayer player) {
		queue.offer(new QueuedPacket(packet, target, player));
	}

	@Override
	public void think(Side what) {
		Tracer.begin(this, "process queue: " + name);
		queue.drainTo(drain);
		Iterator<QueuedPacket> stack = drain.iterator();

		while (stack.hasNext()) {
			try {
				QueuedPacket obj = stack.next();
				EntityPlayer player = obj.player.get();
				if (player == null)
					throw new DropPacketException("Packet enqueued without player or with dead reference");
				LCPacket packet = obj.packet;
				if (packet instanceof LCNetworkHandshake) {
					if (obj.target == Side.SERVER)
						controller.players.get(player).handleHandshakePacket(player, (LCNetworkHandshake) packet,
								obj.target);
					if (obj.target == Side.CLIENT)
						controller.clientPlayer.handleHandshakePacket(player, (LCNetworkHandshake) packet, obj.target);
				} else if (packet instanceof LCTargetPacket) {
					LCTargetPacket target = (LCTargetPacket) packet;
					LCTargetPacket.handlePacket(target, player);
				} else if (packet instanceof LCServerToServerEnvelope) {
					LCServerToServerEnvelope envelope = (LCServerToServerEnvelope) packet;
					if (obj.target == Side.CLIENT)
						controller.envelopeBuffer.addPacket(envelope);
					else
						controller.players.get(player).addEnvelopePacket(player, envelope);
				} else
					throw new DropPacketException(String.format("Unsupported packet %s.", packet.getClass().getName()));
			} catch (DropPacketException exception) {
				// TODO: do we want to log this?
				LCLog.warn("Dropping network packet.", exception);
			} catch (LCNetworkException exception) {
				LCLog.warn("Problem handling packet in queue.", exception);
			}
		}
		LCLog.doSoftAssert(drain.size() == 0, "Network drain not empty before clear: still have %s to go", drain.size());
		drain.clear();
		Tracer.end();
	}

	@SideOnly(Side.CLIENT)
	private EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
}
