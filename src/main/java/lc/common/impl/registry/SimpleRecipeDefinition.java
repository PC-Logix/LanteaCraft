package lc.common.impl.registry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import lc.api.components.RecipeType;
import lc.api.defs.IRecipeDefinition;

public class SimpleRecipeDefinition implements IRecipeDefinition {

	private String name;
	private RecipeType type;

	private HashMap<Integer, ItemStack> inputs;
	private HashMap<Integer, ItemStack> outputs;
	private HashMap<Integer, Boolean> use;

	public SimpleRecipeDefinition(String name, RecipeType type, ItemStack output, String grid, ItemStack... inputs) {
		this.name = name;
		this.type = type;
		this.inputs = new HashMap<Integer, ItemStack>();
		this.outputs = new HashMap<Integer, ItemStack>();
		this.use = new HashMap<Integer, Boolean>();

		for (int i = 0; i < 9; i++)
			use.put(i, true);
		this.outputs.put(0, output);
		char[] cells = grid.toCharArray();
		for (int i = 0; i < cells.length; i++) {
			char cell = cells[i];
			if (Character.isDigit(cell))
				this.inputs.put(i, inputs[Character.getNumericValue(cell)]);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public RecipeType getType() {
		return type;
	}

	@Override
	public Map<Integer, ItemStack> getInputStacks() {
		return inputs;
	}

	@Override
	public Map<Integer, Boolean> getInputConsumption() {
		return use;
	}

	@Override
	public Map<Integer, ItemStack> getOutputStacks() {
		return outputs;
	}

}
