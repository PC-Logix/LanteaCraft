package lc.common.impl.registry;

import lc.api.components.IComponentRegistry;
import lc.api.components.IDefinitionRegistry;
import lc.api.components.IInterfaceRegistry;
import lc.api.components.IRecipeRegistry;
import lc.api.components.IRegistryContainer;
import lc.api.components.IStructureRegistry;

/**
 * Registry container implementation.
 *
 * @author AfterLifeLochie
 *
 */
public class RegistryContainer implements IRegistryContainer {

	private ComponentRegistry components = new ComponentRegistry();
	private DefinitionRegistry definitions = new DefinitionRegistry();
	private RecipeRegistry recipes = new RecipeRegistry();
	private StructureRegistry structures = new StructureRegistry();
	private InterfaceRegistry interfaces = new InterfaceRegistry();

	@Override
	public IComponentRegistry components() {
		return components;
	}

	@Override
	public IDefinitionRegistry definitions() {
		return definitions;
	}

	@Override
	public IRecipeRegistry recipes() {
		return recipes;
	}

	@Override
	public IStructureRegistry structures() {
		return structures;
	}

	@Override
	public IInterfaceRegistry interfaces() {
		return interfaces;
	}

}
