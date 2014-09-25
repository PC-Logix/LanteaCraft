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

}
