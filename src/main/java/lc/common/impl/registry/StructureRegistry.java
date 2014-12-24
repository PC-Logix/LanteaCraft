package lc.common.impl.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import lc.LCRuntime;
import lc.api.components.IStructureRegistry;
import lc.api.defs.IStructureDefinition;
import lc.common.LCLog;

public class StructureRegistry implements IStructureRegistry {

	private final Map<String, IStructureDefinition> definitionPool;
	private final Map<Class<? extends StructureStart>, List<IStructureDefinition>> typeCache;

	public StructureRegistry() {
		definitionPool = new HashMap<String, IStructureDefinition>();
		typeCache = new HashMap<Class<? extends StructureStart>, List<IStructureDefinition>>();
	}

	@Override
	public void register(IStructureDefinition definition) {
		if (definitionPool.containsKey(definition.getName().toLowerCase()))
			throw new RuntimeException("Attempt to overwrite existing definition " + definition.getName());
		definitionPool.put(definition.getName().toLowerCase(), definition);
	}

	@Override
	public IStructureDefinition getDefinition(String name) {
		return definitionPool.get(name.toLowerCase());
	}

	public void init(LCRuntime runtime, FMLInitializationEvent event) {
		for (Entry<String, IStructureDefinition> entry : definitionPool.entrySet()) {
			LCLog.debug("Registering structure %s (class: %s)", entry.getValue().getName(), entry.getValue()
					.getStructureClass().getName());
			MapGenStructureIO.registerStructure(entry.getValue().getStructureClass(), entry.getValue().getName());
			Map<String, Class<? extends StructureComponent>> comps = entry.getValue().getAllComponents();
			for (Entry<String, Class<? extends StructureComponent>> comp : comps.entrySet()) {
				LCLog.debug("Registring component %s (class: %s)", comp.getKey(), comp.getValue().getName());
				MapGenStructureIO.func_143031_a(comp.getValue(), comp.getKey());
			}
		}
	}

	public IStructureDefinition[] allDefs(Class<? extends StructureStart> type) {
		if (!typeCache.containsKey(type)) {
			typeCache.put(type, new ArrayList<IStructureDefinition>());
			for (IStructureDefinition def : definitionPool.values()) {
				if (def.getStructureClass().equals(type) || type.isAssignableFrom(def.getStructureClass()))
					typeCache.get(type).add(def);
			}
		}
		return typeCache.get(type).toArray(new IStructureDefinition[0]);
	}

}
