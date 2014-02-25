package pcl.lc.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import pcl.common.helpers.VersionHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IWorldTickHost;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class CoreTickHandler implements ITickHandler {

	private boolean messageSent;
	private ArrayList<IWorldTickHost> children = new ArrayList<IWorldTickHost>();

	private ReentrantLock childLock = new ReentrantLock();
	private ArrayList<IWorldTickHost> newChildren = new ArrayList<IWorldTickHost>();

	public void registerTickHost(IWorldTickHost host) {
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

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.PLAYER))) {
			VersionHelper versioning = LanteaCraft.getProxy().getVersionHelper();
			if (versioning.finished && !messageSent) {
				if (versioning.requiresNotify) {
					EntityPlayer player = (EntityPlayer) tickData[0];
					player.sendChatToPlayer(new ChatMessageComponent().addText("LanteaCraft "
							+ versioning.remoteVersion + "-" + versioning.remoteBuild + " is available: "
							+ versioning.remoteLabel));
				}
				messageSent = true;
			}
		}

		if (type.equals(EnumSet.of(TickType.WORLD))) {
			// Only allow modification of the child stack when the child input
			// stack is not locked; don't wait to obtain the lock either.
			if (!childLock.isLocked()) {
				if (newChildren.size() > 0) {
					try {
						childLock.lock();
						for (IWorldTickHost host : newChildren)
							children.add(host);
						newChildren.clear();
						childLock.unlock();
					} catch (Throwable t) {
					} finally {
						if (childLock.isLocked())
							childLock.unlock();
					}
				}
			}

			for (IWorldTickHost host : children)
				try {
					host.tick();
				} catch (Throwable t) {
					LanteaCraft.getLogger().log(Level.WARNING, "Unhandled exception in IWorldTickHost.", t);
				}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub

	}

	@Override
	public EnumSet<TickType> ticks() {
		if (messageSent)
			return EnumSet.of(TickType.WORLD);
		else
			return EnumSet.of(TickType.WORLD, TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "LanteaCraft Server Worker";
	}
}
