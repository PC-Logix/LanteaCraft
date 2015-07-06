package lc.common.network;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lc.LCRuntime;
import lc.api.event.ITickEventHandler;
import lc.client.HintProviderClient;
import lc.common.LCLog;
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

	public LCNetworkQueue(LCNetworkController controller) {
		this.controller = controller;
	}

	public void queue(LCPacket packet, Side target, EntityPlayer player) {
		queue.offer(new QueuedPacket(packet, target, player));
	}

	@Override
	public void think(Side what) {
		Tracer.begin();
		queue.drainTo(drain);
		Iterator<QueuedPacket> stack = drain.iterator();

		while (stack.hasNext()) {
			try {
				QueuedPacket obj = stack.next();
				EntityPlayer player = obj.player.get();
				if (player == null)
					throw new LCNetworkException("Packet enqueued without player or with dead reference.");
				LCPacket packet = obj.packet;
				if (packet instanceof LCTargetPacket) {
					LCTargetPacket target = (LCTargetPacket) packet;
					LCTargetPacket.handlePacket(target, player);
				} else if (packet instanceof LCServerToServerEnvelope) {
					LCServerToServerEnvelope envelope = (LCServerToServerEnvelope) packet;
					if (obj.target == Side.CLIENT) {
						HintProviderClient client = (HintProviderClient) LCRuntime.runtime.hints();
						client.forwarder().handle(envelope);
					} else {

					}
				} else
					throw new LCNetworkException(String.format("Unsupported packet %s.", packet.getClass().getName()));
			} catch (LCNetworkException exception) {
				LCLog.warn("Problem handling packet in queue.", exception);
			}
		}
		drain.clear();
		Tracer.end();
	}

	@SideOnly(Side.CLIENT)
	private EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
}
