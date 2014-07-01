package pcl.lc.module.integration;

import java.util.Map;
import java.util.Map.Entry;

import mcp.mobius.waila.api.IWailaRegistrar;
import cpw.mods.fml.common.event.FMLInterModComms;
import pcl.common.helpers.RegistrationHelper;
import pcl.common.helpers.RegistrationHelper.BlockItemMapping;
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
		
	}
	
	@Override
	public void postInit() {
		FMLInterModComms.sendMessage("Waila", "register", "pcl.lc.module.integration.WailaAgent.callbackRegister");
	}
	
	public static void callbackRegister(IWailaRegistrar registrar) {
		Map<Integer, BlockItemMapping> ablock = RegistrationHelper.getBlockMappings();
		for (Entry<Integer, BlockItemMapping> amapping : ablock.entrySet()) {
			WailaHook hook = new WailaHook(amapping.getValue().blockClass);
			registrar.registerHeadProvider(hook, amapping.getValue().blockClass);
			registrar.registerBodyProvider(hook, amapping.getValue().blockClass);
		}

	}

}
