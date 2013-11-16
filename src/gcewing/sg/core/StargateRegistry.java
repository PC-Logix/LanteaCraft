package gcewing.sg.core;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.minecraft.block.Block;
import gcewing.sg.SGCraft;
import gcewing.sg.multiblock.IStructureConfiguration;

public class StargateRegistry {
	private HashMap<Integer, StargateDefinition> stargateTypes = new HashMap<Integer, StargateDefinition>();

	public void registerGate(EnumStargateType typeof, IStructureConfiguration configuration) {
		SGCraft.getLogger().log(Level.INFO,
				"Proxy is internally registering gate typeof " + typeof + " ordinal " + typeof.getOrdinal());
		StargateDefinition config = new StargateDefinition(typeof, configuration);
		stargateTypes.put(typeof.getOrdinal(), config);
	}

	public void postRegister() {
		for (Entry<Integer, StargateDefinition> gateType : stargateTypes.entrySet()) {
			SGCraft.getLogger().log(Level.INFO, "SGCraft setting up gate typeof " + gateType.getValue().getTypeof());

		}
	}

}
