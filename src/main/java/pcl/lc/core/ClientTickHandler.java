package pcl.lc.core;

import java.util.ArrayDeque;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {

	private ArrayDeque<ModPacket> taskQueue = new ArrayDeque<ModPacket>();

	public void putTask(ModPacket thePacket) {
		taskQueue.add(thePacket);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.CLIENT))) {
			if (Minecraft.getMinecraft().theWorld != null) {
				doTask();
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub

	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT, TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return "LanteaCraftClientWorker";
	}

	public void doTask() {
		if (taskQueue.size() > 0) {
			ModPacket thePacket = taskQueue.pop();
			if (thePacket instanceof StandardModPacket) {
				StandardModPacket task = (StandardModPacket) thePacket;
				
			}
		}
	}

}
