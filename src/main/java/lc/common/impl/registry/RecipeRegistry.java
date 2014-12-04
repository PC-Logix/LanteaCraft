package lc.common.impl.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lc.api.components.IRecipeRegistry;
import lc.api.components.RecipeType;
import lc.api.defs.IRecipeDefinition;
import lc.common.LCLog;
import lc.core.LCRuntime;
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
			LCLog.info("Setting up recipe %s (type: %s)", entry.getValue().getName(), entry.getValue().getType());
			IRecipeDefinition definition = entry.getValue();
			RecipeType type = definition.getType();
			if (type == RecipeType.SHAPELESS) {
				Map<Integer, ItemStack> in = definition.getInputStacks();
				Map<Integer, ItemStack> out = definition.getOutputStacks();
				if (out.size() != 1 || !out.containsKey(0))
					LCLog.fatal("Bad recipe %s: expected 1 output stack for shapeless, got %s.", definition.getName(),
							out.size());
				ItemStack[] inputs = in.values().toArray(new ItemStack[0]);
				LCLog.info("Creating shapless recipe: in %s, out %s", inputs, out.get(0));
				CraftingManager.getInstance().addShapelessRecipe(out.get(0), inputs);
			} else if (type == RecipeType.SHAPED) {
				Map<Integer, ItemStack> in = definition.getInputStacks();
				Map<Integer, ItemStack> out = definition.getOutputStacks();
				if (out.size() != 1 || !out.containsKey(0))
					LCLog.fatal("Bad recipe %s: expected 1 output stack for shapeless, got %s.", definition.getName(),
							out.size());
				CraftingManager.getInstance().addRecipe(out.get(0), "012", "345", "678", '0', in.get(0), '1',
						in.get(1), '2', in.get(2), '3', in.get(3), '4', in.get(4), '5', in.get(5), '6', in.get(6), '7',
						in.get(7), '8', in.get(8));
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
			} else if (type == RecipeType.PROXY) {
				GameRegistry.addRecipe(definition.getParentObject());
			} else {
				LCLog.fatal("Cannot handle recipe type %s. Panic!", type);
			}
		}
	}
}
