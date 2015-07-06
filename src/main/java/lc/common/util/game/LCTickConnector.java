package lc.common.util.game;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import lc.api.event.ITickEventHandler;
import lc.common.LCLog;
import lc.common.util.Tracer;

/**
 * Minecraft tick connection manager.
 * 
 * @author AfterLifeLochie
 *
 */
public class LCTickConnector {
	/** The system lock */
	protected ReentrantLock childLock = new ReentrantLock();
	/** The list of children in the connection */
	protected ArrayList<ITickEventHandler> children = new ArrayList<ITickEventHandler>();
	/** The list of future children in the connection */
	protected ArrayList<ITickEventHandler> newChildren = new ArrayList<ITickEventHandler>();

	/**
	 * Create a new tick connector
	 */
	public LCTickConnector() {
		FMLCommonHandler.instance().bus().register(this);
	}

	/**
	 * Register a tick event handler with the connector.
	 * 
	 * @param host
	 *            The tick event handler object
	 */
	public void register(ITickEventHandler host) {
		try {
			childLock.lock();
			if (!newChildren.contains(host))
				newChildren.add(host);
			childLock.unlock();
		} catch (Throwable t) {
		} finally {
			if (childLock.isLocked())
				childLock.unlock();
		}
	}

	/**
	 * Called when a world tick occurs on the server
	 * 
	 * @param tick
	 *            The tick state
	 */
	@SubscribeEvent
	public void onWorldTick(ServerTickEvent tick) {
		if (tick.phase != Phase.START)
			return;
		update();
		doTick(Side.SERVER);
	}

	/**
	 * Called when a client tick occurs on the client
	 * 
	 * @param tick
	 *            The tick state
	 */
	@SubscribeEvent
	public void onClientTick(ClientTickEvent tick) {
		if (tick.phase != Phase.START)
			return;
		update();
		doTick(Side.CLIENT);
	}

	private void update() {
		if (!childLock.isLocked())
			if (newChildren.size() > 0)
				try {
					childLock.lock();
					for (ITickEventHandler host : newChildren)
						children.add(host);
					newChildren.clear();
					childLock.unlock();
				} catch (Throwable t) {
				} finally {
					if (childLock.isLocked())
						childLock.unlock();
				}
	}

	private void doTick(Side what) {
		for (ITickEventHandler host : children)
			try {
				Tracer.begin(this, "tick child: " + host.getClass().getName());
				host.think(what);
			} catch (Throwable t) {
				LCLog.warn("Unhandled exception in ITickEventHandler.", t);
			} finally {
				Tracer.end();
			}
	}

}
