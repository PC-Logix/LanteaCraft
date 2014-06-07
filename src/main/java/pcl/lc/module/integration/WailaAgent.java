package pcl.lc.module.integration;

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
		// TODO Auto-generated method stub
		
	}

}
