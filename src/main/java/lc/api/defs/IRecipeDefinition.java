package lc.api.defs;

import java.util.Map;

import lc.api.components.RecipeType;
import net.minecraft.item.ItemStack;

/**
 * Recipe defintion container
 *
 * @author AfterLifeLochie
 *
 */
public interface IRecipeDefinition extends IGameDef {

	/**
	 * @return The name of the recipe
	 */
	public String getName();

	/**
	 * @return The type of the recipe
	 */
	public RecipeType getType();

	/**
	 * Called by the system to force the evaluation of this recipe so that it
	 * may be used.
	 */
	public void evaluateRecipe();

	/**
	 * Get the input stacks of the recipe
	 *
	 * @return The input stacks
	 */
	public Map<Integer, ItemStack> getInputStacks();

	/**
	 * Get the input consumption map of the recipe
	 *
	 * @return The consumption map
	 */
	public Map<Integer, Boolean> getInputConsumption();

	/**
	 * Get the output stacks of the recipe
	 *
	 * @return The output stacks
	 */
	public Map<Integer, ItemStack> getOutputStacks();

}
