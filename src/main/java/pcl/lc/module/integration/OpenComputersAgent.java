package pcl.lc.module.integration;

import pcl.lc.api.internal.Agent;
import pcl.lc.api.internal.IIntegrationAgent;
import li.cil.oc.api.Driver;

@Agent(modname = "OpenComputers")
public class OpenComputersAgent implements IIntegrationAgent {

	public OpenComputersAgent() {

	}

	@Override
	public String modName() {
		return "OpenComputers";
	}

	@Override
	public void init() {
		Driver.add(new OpenComputersWrapperPool.OpenComputersDriver());
	}

}
