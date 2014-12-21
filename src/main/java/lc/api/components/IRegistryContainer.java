/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.components;

/**
 * Registry container interface
 *
 * @author AfterLifeLochie
 *
 */
public interface IRegistryContainer {

	/**
	 * @return The component registry instance
	 */
	public IComponentRegistry components();

	/**
	 * @return The definition registry interface
	 */
	public IDefinitionRegistry definitions();

	/**
	 * @return The recipe registry interface
	 */
	public IRecipeRegistry recipes();

	/**
	 * @return The structure registry interface
	 */
	public IStructureRegistry structures();

	/**
	 * @return The interface registry interface
	 */
	public IInterfaceRegistry interfaces();

}
