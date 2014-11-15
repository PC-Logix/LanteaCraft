package lc.core;

import net.minecraft.item.ItemStack;
import lc.api.components.RecipeType;
import lc.api.defs.IDefinitionReference;
import lc.api.init.Blocks;
import lc.api.init.Items;
import lc.api.init.Recipes;
import lc.api.world.OreType;
import lc.blocks.BlockDecorative;
import lc.blocks.BlockLanteaAlloy;
import lc.blocks.BlockLanteaOre;
import lc.blocks.BlockStargateBase;
import lc.blocks.BlockStargateRing;
import lc.common.impl.registry.DefinitionReference;
import lc.common.impl.registry.DefinitionWrapperProvider;
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
		blocks.lanteaAlloyBlock = DefinitionWrapperProvider.provide(BlockLanteaAlloy.class);

		blocks.lanteaDecorBlock = DefinitionWrapperProvider.provide(BlockDecorative.class);

		items.glasses = DefinitionWrapperProvider.provide(ItemGlasses.class);
		items.lanteaOreItem = DefinitionWrapperProvider.provide(ItemLanteaOre.class);
		items.lanteaAlloyItem = DefinitionWrapperProvider.provide(ItemLanteaAlloyIngot.class);

		IDefinitionReference naquadah = items.lanteaOreItem.ref().pushAll(1, OreType.NAQUADAH.ordinal());
		IDefinitionReference trinium = items.lanteaOreItem.ref().pushAll(1, OreType.TRINIUM.ordinal());
		IDefinitionReference naquadahIngot = items.lanteaAlloyItem.ref().pushAll(1, OreType.NAQUADAH.ordinal());
		IDefinitionReference triniumIngot = new DefinitionReference(items.lanteaAlloyItem, 1, OreType.TRINIUM.ordinal());
		IDefinitionReference naquadahAlloyBlock = blocks.lanteaAlloyBlock.ref().pushAll(1, OreType.NAQUADAH.ordinal());
		IDefinitionReference triniumAlloyBlock = blocks.lanteaAlloyBlock.ref().pushAll(1, OreType.TRINIUM.ordinal());

		recipes.naquadahIngot = new SimpleRecipeDefinition("naquadah_ingot", RecipeType.SHAPELESS, naquadahIngot, "01",
				new ItemStack(net.minecraft.init.Items.iron_ingot, 1), naquadah);
		runtime.registries().recipes().addRecipe(recipes.naquadahIngot);

		recipes.triniumIngot = new SimpleRecipeDefinition("trinium_ingot", RecipeType.SHAPELESS, triniumIngot, "01",
				new ItemStack(net.minecraft.init.Items.iron_ingot, 1), trinium);
		runtime.registries().recipes().addRecipe(recipes.triniumIngot);

		recipes.naquadahAlloyBlock = new SimpleRecipeDefinition("naquadah_alloy_block", RecipeType.SHAPED,
				naquadahAlloyBlock, "000000000", naquadahIngot);
		runtime.registries().recipes().addRecipe(recipes.naquadahAlloyBlock);
		recipes.naquadahAlloyToIngots = new SimpleRecipeDefinition("naquadah_alloy_to_ingot", RecipeType.SHAPELESS,
				naquadahIngot.copy().push(0, 9), "0", naquadahAlloyBlock);
		runtime.registries().recipes().addRecipe(recipes.naquadahAlloyToIngots);

		recipes.triniumAlloyBlock = new SimpleRecipeDefinition("trinium_alloy_block", RecipeType.SHAPED,
				triniumAlloyBlock, "000000000", triniumIngot);
		runtime.registries().recipes().addRecipe(recipes.triniumAlloyBlock);
		recipes.triniumAlloyToIngots = new SimpleRecipeDefinition("trinium_alloy_to_ingot", RecipeType.SHAPELESS,
				triniumIngot.copy().push(0, 9), "0", triniumAlloyBlock);
		runtime.registries().recipes().addRecipe(recipes.triniumAlloyToIngots);

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
