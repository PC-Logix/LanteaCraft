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

public class LCTickConnector {
	protected ReentrantLock childLock = new ReentrantLock();
	protected ArrayList<ITickEventHandler> children = new ArrayList<ITickEventHandler>();
	protected ArrayList<ITickEventHandler> newChildren = new ArrayList<ITickEventHandler>();

	public LCTickConnector() {
		FMLCommonHandler.instance().bus().register(this);
	}

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

	@SubscribeEvent
	public void onWorldTick(ServerTickEvent tick) {
		if (tick.phase != Phase.START)
			return;
		update();
		doTick(Side.SERVER);
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent tick) {
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
				host.think(what);
			} catch (Throwable t) {
				LCLog.warn("Unhandled exception in ITickEventHandler.", t);
			}
	}

}
