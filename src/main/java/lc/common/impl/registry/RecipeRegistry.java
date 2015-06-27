package lc.common.impl.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lc.LCRuntime;
import lc.api.components.IRecipeRegistry;
import lc.api.components.RecipeType;
import lc.api.defs.IRecipeDefinition;
import lc.common.LCLog;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Recipe registry implementation.
 *
 * @author AfterLifeLochie
 *
 */
public class RecipeRegistry implements IRecipeRegistry {

	/** Pool of all known definitions. */
	private final Map<String, IRecipeDefinition> definitionPool;

	/** Default constructor */
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

	/**
	 * Initializes the registry
	 *
	 * @param runtime
	 *            The LanteaCraft runtime instance
	 * @param event
	 *            The FML event initializing the runtime
	 */
	public void init(LCRuntime runtime, FMLInitializationEvent event) {
		for (Entry<String, IRecipeDefinition> entry : definitionPool.entrySet()) {
			entry.getValue().evaluateRecipe();
			LCLog.debug("Setting up recipe %s (type: %s)", entry.getValue().getName(), entry.getValue().getType());
			IRecipeDefinition definition = entry.getValue();
			RecipeType type = definition.getType();
			if (type == RecipeType.SHAPELESS) {
				Map<Integer, ItemStack> in = definition.getInputStacks();
				Map<Integer, ItemStack> out = definition.getOutputStacks();
				if (out.size() != 1 || !out.containsKey(0))
					LCLog.fatal("Bad recipe %s: expected 1 output stack for shapeless, got %s.", definition.getName(),
							out.size());
				ItemStack[] inputs = in.values().toArray(new ItemStack[0]);
				CraftingManager.getInstance().addShapelessRecipe(out.get(0), (Object[]) inputs);
			} else if (type == RecipeType.SHAPED) {
				Map<Integer, ItemStack> in = definition.getInputStacks();
				Map<Integer, ItemStack> out = definition.getOutputStacks();
				if (out.size() != 1 || !out.containsKey(0))
					LCLog.fatal("Bad recipe %s: expected 1 output stack for shaped, got %s.", definition.getName(),
							out.size());

				StringBuilder[] grid = new StringBuilder[3];
				ArrayList<Object> qt = new ArrayList<Object>();
				for (int i = 0; i < 3; i++) {
					grid[i] = new StringBuilder();
					for (int j = 0; j < 3; j++) {
						int q = (3 * i) + j;
						if (in.get(q) != null) {
							grid[i].append(q);
							qt.add(Integer.toString(q).charAt(0));
							qt.add(in.get(q));
						} else
							grid[i].append(" ");
					}
				}
				ArrayList<Object> varargs = new ArrayList<Object>();
				varargs.add(grid[0].toString());
				varargs.add(grid[1].toString());
				varargs.add(grid[2].toString());
				varargs.addAll(qt);
				LCLog.debug("Adding shaped recipe: [ '%s', '%s', '%s' ]", grid[0].toString(), grid[1].toString(), grid[2].toString());
				CraftingManager.getInstance().addRecipe(out.get(0), varargs.toArray());
			} else if (type == RecipeType.SMELTING) {
				Map<Integer, ItemStack> in = definition.getInputStacks();
				if (in.size() != 1 || !in.containsKey(0))
					LCLog.fatal("Bad recipe %s: expected 1 input stack for smelting, got %s.", definition.getName(),
							in.size());
				Map<Integer, ItemStack> out = definition.getOutputStacks();
				if (out.size() != 1 || !out.containsKey(0))
					LCLog.fatal("Bad recipe %s: expected 1 output stack for smelting, got %s.", definition.getName(),
							out.size());
				FurnaceRecipes.smelting().func_151394_a(in.get(0), out.get(0), 0.0f);
			} else if (type == RecipeType.PROXY)
				GameRegistry.addRecipe(definition.getParentObject());
			else
				LCLog.fatal("Cannot handle recipe type %s. Panic!", type);
		}
	}
}
