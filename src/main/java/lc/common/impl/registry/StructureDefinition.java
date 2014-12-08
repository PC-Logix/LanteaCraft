package lc.common.impl.registry;

import java.util.ArrayList;

import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import lc.api.defs.IDefinitionReference;
import lc.api.defs.IStructureDefinition;

/**
 * Structure definition implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class StructureDefinition implements IStructureDefinition {

	private final String name;
	private final Class<? extends StructureStart> startClazz;
	private final ArrayList<Class<? extends StructureComponent>> components;

	/**
	 * Default constructor
	 * 
	 * @param name
	 *            The name of the structure type
	 * @param startClazz
	 *            The StructureStart class
	 */
	public StructureDefinition(String name, Class<? extends StructureStart> startClazz) {
		this.name = name;
		this.startClazz = startClazz;
		this.components = new ArrayList<Class<? extends StructureComponent>>();
	}

	/**
	 * Add a new component to the defintion
	 * 
	 * @param component
	 *            The component type
	 * @return The self definition
	 */
	public StructureDefinition addComp(Class<? extends StructureComponent> component) {
		if (!components.contains(component))
			components.add(component);
		return this;
	}

	@Override
	public IDefinitionReference ref() {
		return new DefinitionReference(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<? extends StructureStart> getStructureClass() {
		return startClazz;
	}

	@Override
	public Class<? extends StructureComponent>[] getAllComponents() {
		return components.toArray(new Class[0]);
	}

}
