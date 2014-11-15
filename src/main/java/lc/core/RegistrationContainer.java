package lc.core;

import net.minecraft.item.ItemStack;
import lc.api.components.RecipeType;
import lc.api.defs.Blocks;
import lc.api.defs.Items;
import lc.api.defs.Recipes;
import lc.api.world.OreType;
import lc.blocks.BlockDecorative;
import lc.blocks.BlockLanteaOre;
import lc.blocks.BlockStargateBase;
import lc.blocks.BlockStargateRing;
import lc.common.impl.registry.DefinitionReference;
import lc.common.impl.registry.DefinitionWrapperProvider;
import lc.common.impl.registry.RecipeRegistry;
import lc.common.impl.registry.SimpleRecipeDefinition;
import lc.items.ItemGlasses;
import lc.items.ItemLanteaAlloyIngot;
import lc.items.ItemLanteaOre;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Registration initializer
 * 
 * @author AfterLifeLochie
 * 
 */
public class RegistrationContainer {

	/**
	 * Called during pre-initialization
	 * 
	 * @param runtime
	 *            The runtime
	 * @param event
	 *            The original FML event
	 */
	public void preinit(LCRuntime runtime, FMLPreInitializationEvent event) {
		// TODO: Auto-generated method stub
	}

	/**
	 * Called during initialization
	 * 
	 * @param runtime
	 *            The runtime
	 * @param event
	 *            The original FML event
	 */
	public void init(LCRuntime runtime, FMLInitializationEvent event) {
		Blocks blocks = runtime.blocks();
		Items items = runtime.items();
		Recipes recipes = runtime.recipes();

		blocks.stargateRingBlock = DefinitionWrapperProvider.provide(BlockStargateRing.class);
		blocks.stargateBaseBlock = DefinitionWrapperProvider.provide(BlockStargateBase.class);
		blocks.lanteaOreBlock = DefinitionWrapperProvider.provide(BlockLanteaOre.class);

		blocks.lanteaDecorBlock = DefinitionWrapperProvider.provide(BlockDecorative.class);

		items.glasses = DefinitionWrapperProvider.provide(ItemGlasses.class);
		items.lanteaOreItem = DefinitionWrapperProvider.provide(ItemLanteaOre.class);
		items.lanteaAlloyItem = DefinitionWrapperProvider.provide(ItemLanteaAlloyIngot.class);

		recipes.naquadahIngot = new SimpleRecipeDefinition("naquadah_ingot", RecipeType.SHAPELESS,
				new DefinitionReference(items.lanteaAlloyItem, 1, OreType.NAQUADAH.ordinal()), "01", new ItemStack(
						net.minecraft.init.Items.iron_ingot, 1), new DefinitionReference(items.lanteaOreItem, 1,
						OreType.NAQUADAH.ordinal()));
		runtime.registries().recipes().addRecipe(recipes.naquadahIngot);

	}

	/**
	 * Called during post-initialization
	 * 
	 * @param runtime
	 *            The runtime
	 * @param event
	 *            The original FML event
	 */
	public void postinit(LCRuntime runtime, FMLPostInitializationEvent event) {
		// TODO: Auto-generated method stub
	}

}
