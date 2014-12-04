/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.components;

/**
 * Component registry interface
 *
 * @author AfterLifeLochie
 *
 */
public interface IComponentRegistry {

	/**
	 * Ask if a component type is allowed to be loaded
	 *
	 * @param type
	 *            The type of component
	 * @return If the component type can be loaded
	 */
	public boolean isEnabled(ComponentType type);

	/**
	 * Ask if a component type has been loaded okay
	 *
	 * @param type
	 *            The type of component
	 * @return If the component type has been loaded
	 */
	public boolean isLoaded(ComponentType type);

}
