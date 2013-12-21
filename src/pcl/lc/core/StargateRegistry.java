package pcl.lc.core;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.afterlifelochie.minecore.multiblock.IStructureConfiguration;
import pcl.lc.LanteaCraft;

public class StargateRegistry {
	private HashMap<Integer, StargateDefinition> stargateTypes = new HashMap<Integer, StargateDefinition>();

	public void registerGate(EnumStargateType typeof, IStructureConfiguration configuration) {
		LanteaCraft.getLogger().log(Level.INFO,
				"Proxy is internally registering gate typeof " + typeof + " ordinal " + typeof.getOrdinal());
		// StargateDefinition config = new StargateDefinition(typeof, configuration);
		// stargateTypes.put(typeof.getOrdinal(), config);
	}

	public void postRegister() {
		for (Entry<Integer, StargateDefinition> gateType : stargateTypes.entrySet())
			LanteaCraft.getLogger().log(Level.INFO,
					"LanteaCraft setting up gate typeof " + gateType.getValue().getTypeof());
	}

}
