package lc.api.components;

import lc.api.defs.IInterfaceDefinition;

public interface IInterfaceRegistry {
	/**
	 * Add a new game definition
	 *
	 * @param definition
	 *            The definition element
	 */
	public void addDefinition(IInterfaceDefinition definition);

	/**
	 * Get a named definition from this registry
	 *
	 * @param name
	 *            The definition name
	 * @return The definition or null if it does not exist
	 */
	public IInterfaceDefinition getDefinition(String name);

	/**
	 * Get an indexed definition from this registry
	 *
	 * @param guiId
	 *            The GUI ID
	 * @return The definition or null if it does not exist
	 */
	public IInterfaceDefinition getDefinition(int guiId);
}
