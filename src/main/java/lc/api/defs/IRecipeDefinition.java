package lc.api.defs;

import java.util.Map;

import net.minecraft.item.ItemStack;
import lc.api.components.RecipeType;

public interface IRecipeDefinition {

	public String getName();

	public RecipeType getType();

	public Map<Integer, ItemStack> getInputStacks();

	public Map<Integer, Boolean> getInputConsumption();

	public Map<Integer, ItemStack> getOutputStacks();

}
