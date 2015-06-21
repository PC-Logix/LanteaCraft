package lc.common.impl.registry;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import lc.LCRuntime;
import lc.api.components.IInterfaceRegistry;
import lc.api.defs.IInterfaceDefinition;

/**
 * Interface registry implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class InterfaceRegistry implements IInterfaceRegistry {

	/** Pool of all known definitions. */
	private final Map<String, IInterfaceDefinition> definitionPool;

	/** Last element ID */
	private int lastId = 0;

	/** Default constructor */
	public InterfaceRegistry() {
		definitionPool = new HashMap<String, IInterfaceDefinition>();
	}

	@Override
	public void addDefinition(IInterfaceDefinition definition) {
		definition.setGUIID(lastId++);
		definitionPool.put(definition.getName(), definition);
	}

	@Override
	public IInterfaceDefinition getDefinition(String name) {
		return definitionPool.get(name);
	}

	@Override
	public IInterfaceDefinition getDefinition(int guiId) {
		for (IInterfaceDefinition def : definitionPool.values())
			if (def.getGUIID() == guiId)
				return def;
		return null;
	}

	/**
	 * Initializes the registry
	 *
	 * @param runtime
	 *            The LanteaCraft runtime instance
	 * @param event
	 *            The FML event initializing the runtime
	 */
	public void init(LCRuntime runtime, FMLInitializationEvent event) {

	}

}
