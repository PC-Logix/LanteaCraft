package lc.common.impl;

import java.util.HashMap;
import java.util.Map;

import lc.api.components.IRecipeRegistry;
import lc.api.defs.IRecipeDefinition;

public class RecipeRegistry implements IRecipeRegistry {

	/** Pool of all known definitions. */
	private final Map<String, IRecipeDefinition> definitionPool;

	public RecipeRegistry() {
		definitionPool = new HashMap<String, IRecipeDefinition>();
	}

	@Override
	public void addRecipe(IRecipeDefinition definition) {
		if (definitionPool.containsKey(definition.getName().toLowerCase()))
			throw new RuntimeException("Attempt to overwrite existing definition " + definition.getName());
		definitionPool.put(definition.getName().toLowerCase(), definition);
	}

	@Override
	public IRecipeDefinition getRecipe(String name) {
		return definitionPool.get(name.toLowerCase());
	}

}
