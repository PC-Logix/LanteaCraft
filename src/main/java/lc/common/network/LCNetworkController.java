package lc.common.network;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import lc.BuildInfo;
import lc.LCRuntime;
import lc.api.event.ITickEventHandler;

public class LCNetworkController implements ITickEventHandler {

	private final LCPacketPipeline pipe = new LCPacketPipeline(this);
	private final LCNetworkQueue queue = new LCNetworkQueue(this);

	public LCNetworkController() {
		// TODO Auto-generated constructor stub
	}

	public void init(LCRuntime runtime, FMLInitializationEvent event) {
		pipe.init(BuildInfo.modID);
		runtime.ticks().register(this);
	}

	public LCPacketPipeline getPreferredPipe() {
		return pipe;
	}

	public LCNetworkQueue getPreferredQueue() {
		return queue;
	}

	@Override
	public void think(Side what) {
		queue.think(what);
	}

}
