package lc.api.components;

import lc.api.defs.IRecipeDefinition;

public interface IRecipeRegistry {

	public void addRecipe(IRecipeDefinition definition);

	public IRecipeDefinition getRecipe(String name);

}
