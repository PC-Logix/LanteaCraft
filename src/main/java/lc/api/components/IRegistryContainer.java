package lc.api.components;

public interface IRegistryContainer {

	public IComponentRegistry components();

	public IDefinitionRegistry definitions();
	
	public IRecipeRegistry recipes();

}
