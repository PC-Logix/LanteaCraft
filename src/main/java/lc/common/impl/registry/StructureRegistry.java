package lc.common.impl.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureComponent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import lc.api.components.IStructureRegistry;
import lc.api.defs.IStructureDefinition;
import lc.core.LCRuntime;

public class StructureRegistry implements IStructureRegistry {

	private final Map<String, IStructureDefinition> definitionPool;

	public StructureRegistry() {
		definitionPool = new HashMap<String, IStructureDefinition>();
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
			MapGenStructureIO.registerStructure(entry.getValue().getStructureClass(), entry.getValue().getName());
			Map<String, Class<? extends StructureComponent>> comps = entry.getValue().getAllComponents();
			for (Entry<String, Class<? extends StructureComponent>> comp : comps.entrySet())
				MapGenStructureIO.func_143031_a(comp.getValue(), comp.getKey());
		}
	}

	public IStructureDefinition[] allDefs() {
		return definitionPool.entrySet().toArray(new IStructureDefinition[0]);
	}

}
