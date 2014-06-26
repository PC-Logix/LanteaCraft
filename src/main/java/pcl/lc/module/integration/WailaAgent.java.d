package pcl.lc.module.integration;

import cpw.mods.fml.common.event.FMLInterModComms;
import pcl.lc.api.internal.Agent;
import pcl.lc.api.internal.IIntegrationAgent;

@Agent(modname = "Waila")
public class WailaAgent implements IIntegrationAgent {

	@Override
	public String modName() {
		return "Waila";
	}

	@Override
	public void init() {
		FMLInterModComms.sendMessage("Waila", "register", "pcl.lc.module.integration.WailaHook.callbackRegister");
	}

}
