package pcl.lc.module.integration;

import java.util.logging.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.Agent;
import pcl.lc.api.internal.IIntegrationAgent;

@Agent(modname = "ComputerCraft")
public class ComputerCraftAgentFactory implements IIntegrationAgent {

	private IIntegrationAgent child_agent;

	public ComputerCraftAgentFactory() {
		try {
			Class<?> computercraft_legacy = Class.forName("dan200.computer.api.ComputerCraftAPI");
			if (computercraft_legacy != null)
				child_agent = new ComputerCraftLegacyAgent();
		} catch (Throwable t) {
		}

		if (child_agent == null) {
			try {
				Class<?> computercraft_latest = Class.forName("dan200.computercraft.api.ComputerCraftAPI");
				if (computercraft_latest != null)
					child_agent = new ComputerCraftLegacyAgent();
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.WARNING, "Could not load a ComputerCraft wrapper environment.");
			}
		}
	}

	@Override
	public String modName() {
		return "ComputerCraft";
	}

	@Override
	public void init() {
		if (child_agent != null)
			child_agent.init();
	}

}
