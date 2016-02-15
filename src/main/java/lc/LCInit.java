package lc;

import java.util.Random;

import lc.api.components.RecipeType;
import lc.api.defs.IDefinitionReference;
import lc.api.init.Blocks;
import lc.api.init.Entities;
import lc.api.init.Interfaces;
import lc.api.init.Items;
import lc.api.init.Recipes;
import lc.api.init.Structures;
import lc.api.jit.AnyPredicate;
import lc.api.world.OreType;
import lc.blocks.BlockBrazier;
import lc.blocks.BlockConfigurator;
import lc.blocks.BlockDHD;
import lc.blocks.BlockDecorative;
import lc.blocks.BlockDecorative.DecorBlockTypes;
import lc.blocks.BlockFrame;
import lc.blocks.BlockLanteaAlloy;
import lc.blocks.BlockLanteaDoor;
import lc.blocks.BlockLanteaOre;
import lc.blocks.BlockObelisk;
import lc.blocks.BlockStargateBase;
import lc.blocks.BlockStargateRing;
import lc.blocks.BlockTransportRing;
import lc.common.impl.registry.DefinitionReference;
import lc.common.impl.registry.DefinitionWrapperProvider;
import lc.common.impl.registry.InterfaceDefinition;
import lc.common.impl.registry.RecipeProxy;
import lc.common.impl.registry.SimpleRecipeDefinition;
import lc.common.impl.registry.StructureDefinition;
import lc.entity.EntityStaffProjectile;
import lc.generation.AbydosPyramid;
import lc.generation.AbydosPyramid.AbydosPyramidFeature;
import lc.generation.SurfaceStargate;
import lc.generation.SurfaceStargate.SurfaceStargateFeature;
import lc.items.ItemCraftingReagent;
import lc.items.ItemDecorator;
import lc.items.ItemGlasses;
import lc.items.ItemIrisUpgrade;
import lc.items.ItemLanteaAlloyIngot;
import lc.items.ItemLanteaOre;
import lc.items.ItemPortableDHD;
import lc.items.ItemStaff;
import lc.items.ItemTransportRingActivator;
import lc.recipe.DecoratorSetterRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Registration initializer
 *
 * @author AfterLifeLochie
 *
 */
public class LCInit {

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
		Entities entities = runtime.entities();
		Recipes recipes = runtime.recipes();
		Structures structures = runtime.structures();
		Interfaces interfaces = runtime.interfaces();

		/* Initialize game blocks and items */
		blocks.stargateRingBlock = DefinitionWrapperProvider.provide(BlockStargateRing.class);
		blocks.stargateBaseBlock = DefinitionWrapperProvider.provide(BlockStargateBase.class);
		blocks.stargateDHDBlock = DefinitionWrapperProvider.provide(BlockDHD.class);
		blocks.transporterBlock = DefinitionWrapperProvider.provide(BlockTransportRing.class);
		blocks.configuratorBlock = DefinitionWrapperProvider.provide(BlockConfigurator.class);
		blocks.frameBlock = DefinitionWrapperProvider.provide(BlockFrame.class);
		blocks.lanteaOreBlock = DefinitionWrapperProvider.provide(BlockLanteaOre.class);
		blocks.lanteaAlloyBlock = DefinitionWrapperProvider.provide(BlockLanteaAlloy.class);

		blocks.lanteaDecorBlock = DefinitionWrapperProvider.provide(BlockDecorative.class);
		blocks.lanteaDoor = DefinitionWrapperProvider.provide(BlockLanteaDoor.class);
		blocks.lanteaObelisk = DefinitionWrapperProvider.provide(BlockObelisk.class);
		blocks.lanteaBrazier = DefinitionWrapperProvider.provide(BlockBrazier.class);

		items.glasses = DefinitionWrapperProvider.provide(ItemGlasses.class);
		items.lanteaOreItem = DefinitionWrapperProvider.provide(ItemLanteaOre.class);
		items.lanteaAlloyItem = DefinitionWrapperProvider.provide(ItemLanteaAlloyIngot.class);
		items.lanteaCraftingItem = DefinitionWrapperProvider.provide(ItemCraftingReagent.class);
		items.lanteaDecoratorTool = DefinitionWrapperProvider.provide(ItemDecorator.class);
		items.lanteaStargateIris = DefinitionWrapperProvider.provide(ItemIrisUpgrade.class);
		items.lanteaTransportRingActivator = DefinitionWrapperProvider.provide(ItemTransportRingActivator.class);
		items.lanteaPortableDHD = DefinitionWrapperProvider.provide(ItemPortableDHD.class);
		items.goauldStaffWeapon = DefinitionWrapperProvider.provide(ItemStaff.class);

		entities.staffProjectile = DefinitionWrapperProvider.provide(EntityStaffProjectile.class);

		/* Initialize recipes */
		initStargateRecipes(runtime, recipes, blocks, items);
		initDecorRecipes(runtime, recipes, blocks, items);
		initCrystalRecipes(runtime, recipes, blocks, items);
		initOreRecipes(runtime, recipes, blocks, items);

		/* Initialize structures */
		structures.scatteredSurfaceStargate = new StructureDefinition("SurfaceStargate", SurfaceStargate.class) {
			AnyPredicate $predicate = new AnyPredicate() {
				@Override
				public boolean test(Object[] t) {
					Random rng = (Random) t[1];
					World world = (World) t[0];
					int x = (Integer) t[2], y = (Integer) t[3];
					if (world.provider.getDimensionId() != 0)
						return false;
					if (x % 16 != 0 || y % 16 != 0)
						return false;
					return rng.nextInt(8) == 0;
				}
			};

			@Override
			protected AnyPredicate getGeneratorPredicate() {
				return $predicate;
			}
		}.addComp("SurfaceStargate", SurfaceStargateFeature.class);
		runtime.registries().structures().register(structures.scatteredSurfaceStargate);

		structures.scatteredAbydosPyramid = new StructureDefinition("AbydosPyramid", AbydosPyramid.class) {
			AnyPredicate $predicate = new AnyPredicate() {
				@Override
				public boolean test(Object[] t) {
					/** Do nothing in case some tool registers us */
					return false;
				}
			};

			@Override
			protected AnyPredicate getGeneratorPredicate() {
				return $predicate;
			}
		}.addComp("AbydosPyramid", AbydosPyramidFeature.class);
		// runtime.registries().structures().register(structures.scatteredAbydosPyramid);

		/* Initialize interfaces */
		interfaces.stargateUI = new InterfaceDefinition("stargateUI", "lc.container.ContainerStargate",
				"lc.gui.GUIStargate");
		interfaces.dhdUI = new InterfaceDefinition("dhdUI", "lc.container.ContainerDHD", "lc.gui.GUIDHD");
		runtime.registries().interfaces().addDefinition(interfaces.stargateUI);
		runtime.registries().interfaces().addDefinition(interfaces.dhdUI);

	}

	private void initOreRecipes(LCRuntime runtime, Recipes recipes, Blocks blocks, Items items) {
		IDefinitionReference naquadah = items.lanteaOreItem.ref().pushAll(1, OreType.NAQUADAH.ordinal());
		IDefinitionReference trinium = items.lanteaOreItem.ref().pushAll(1, OreType.TRINIUM.ordinal());
		IDefinitionReference triniumIngot = new DefinitionReference(items.lanteaAlloyItem, 1, OreType.TRINIUM.ordinal());
		IDefinitionReference naquadahAlloyBlock = blocks.lanteaAlloyBlock.ref().pushAll(1, OreType.NAQUADAH.ordinal());
		IDefinitionReference triniumAlloyBlock = blocks.lanteaAlloyBlock.ref().pushAll(1, OreType.TRINIUM.ordinal());
		IDefinitionReference naquadahIngot = items.lanteaAlloyItem.ref().pushAll(1, OreType.NAQUADAH.ordinal());

		ItemStack ironIngot = new ItemStack(net.minecraft.init.Items.iron_ingot, 1);

		recipes.naquadahIngot = new SimpleRecipeDefinition("naquadah_ingot", RecipeType.SHAPELESS, naquadahIngot, "01",
				ironIngot, naquadah);
		runtime.registries().recipes().addRecipe(recipes.naquadahIngot);

		recipes.triniumIngot = new SimpleRecipeDefinition("trinium_ingot", RecipeType.SHAPELESS, triniumIngot, "01",
				ironIngot, trinium);
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

	private void initCrystalRecipes(LCRuntime runtime, Recipes recipes, Blocks blocks, Items items) {
		ItemStack diamond = new ItemStack(net.minecraft.init.Items.diamond, 1);
		ItemStack lapis = new ItemStack(net.minecraft.init.Items.dye, 1, 4);
		ItemStack glassPane = new ItemStack(net.minecraft.init.Blocks.glass_pane, 1);
		ItemStack redstone = new ItemStack(net.minecraft.init.Items.redstone, 1);

		IDefinitionReference coreCrystal = items.lanteaCraftingItem.ref().pushAll(1,
				ItemCraftingReagent.ReagentList.CORECRYSTAL.ordinal());
		IDefinitionReference controlCrystal = items.lanteaCraftingItem.ref().pushAll(1,
				ItemCraftingReagent.ReagentList.CONTROLCRYSTAL.ordinal());
		IDefinitionReference blankCrystal = items.lanteaCraftingItem.ref().pushAll(1,
				ItemCraftingReagent.ReagentList.BLANKCRYSTAL.ordinal());

		recipes.crystalBlankRecipe = new SimpleRecipeDefinition("blank_crystal", RecipeType.SHAPED, blankCrystal,
				"000010000", glassPane, diamond);
		runtime.registries().recipes().addRecipe(recipes.crystalBlankRecipe);

		recipes.crystalCoreRecipe = new SimpleRecipeDefinition("core_crystal", RecipeType.SHAPED, coreCrystal,
				"000010000", lapis, blankCrystal);
		runtime.registries().recipes().addRecipe(recipes.crystalCoreRecipe);

		recipes.crystalControlRecipe = new SimpleRecipeDefinition("control_crystal", RecipeType.SHAPED, controlCrystal,
				"000010000", redstone, blankCrystal);
		runtime.registries().recipes().addRecipe(recipes.crystalControlRecipe);
	}

	private void initStargateRecipes(LCRuntime runtime, Recipes recipes, Blocks blocks, Items items) {
		IDefinitionReference ringBlock = blocks.stargateRingBlock.ref();
		IDefinitionReference chevronBlock = blocks.stargateRingBlock.ref().pushAll(1, 1);
		IDefinitionReference baseBlock = blocks.stargateBaseBlock.ref();
		IDefinitionReference naquadahIngot = items.lanteaAlloyItem.ref().pushAll(1, OreType.NAQUADAH.ordinal());
		IDefinitionReference coreCrystal = items.lanteaCraftingItem.ref().pushAll(1,
				ItemCraftingReagent.ReagentList.CORECRYSTAL.ordinal());
		IDefinitionReference frameBlock = blocks.frameBlock.ref();
		blocks.transporterBlock.ref();

		ItemStack cSandstone = new ItemStack(net.minecraft.init.Blocks.sandstone, 1, 1);
		ItemStack eyeOfEnder = new ItemStack(net.minecraft.init.Items.ender_eye, 1);
		ItemStack glowstone = new ItemStack(net.minecraft.init.Items.glowstone_dust, 1);
		ItemStack enderPearl = new ItemStack(net.minecraft.init.Items.ender_pearl, 1);
		ItemStack ironIngot = new ItemStack(net.minecraft.init.Items.iron_ingot, 1);
		ItemStack redstone = new ItemStack(net.minecraft.init.Items.redstone, 1);

		recipes.stargateBase = new SimpleRecipeDefinition("stargate_base", RecipeType.SHAPED, baseBlock, "010232454",
				cSandstone, redstone, naquadahIngot, eyeOfEnder, ironIngot, coreCrystal);
		runtime.registries().recipes().addRecipe(recipes.stargateBase);

		recipes.stargateRing = new SimpleRecipeDefinition("stargate_ring", RecipeType.SHAPED, ringBlock, "010222000",
				ironIngot, cSandstone, naquadahIngot);
		runtime.registries().recipes().addRecipe(recipes.stargateRing);

		recipes.stargateChevron = new SimpleRecipeDefinition("stargate_chevron", RecipeType.SHAPED, chevronBlock,
				"010232454", cSandstone, glowstone, naquadahIngot, enderPearl, ironIngot, redstone);
		runtime.registries().recipes().addRecipe(recipes.stargateChevron);

		recipes.frame = new SimpleRecipeDefinition("frame", RecipeType.SHAPED, frameBlock, " 0  010  0 ", ironIngot,
				cSandstone);
		runtime.registries().recipes().addRecipe(recipes.frame);
	}

	private void initDecorRecipes(LCRuntime runtime, Recipes recipes, Blocks blocks, Items items) {
		IDefinitionReference decorator = items.lanteaDecoratorTool.ref();
		IDefinitionReference decorLantSteel = blocks.lanteaDecorBlock.ref().pushAll(1, DecorBlockTypes.LantSteel.idx);
		IDefinitionReference decorLantDecSteel = blocks.lanteaDecorBlock.ref().pushAll(1,
				DecorBlockTypes.LantDecSteel.idx);
		IDefinitionReference decorGoaGold = blocks.lanteaDecorBlock.ref().pushAll(1, DecorBlockTypes.GoaGold.idx);
		IDefinitionReference decorGoaPatGold = blocks.lanteaDecorBlock.ref().pushAll(1, DecorBlockTypes.GoaDecGold.idx);
		IDefinitionReference decorLantDoor = blocks.lanteaDoor.ref().pushAll(0);
		IDefinitionReference decorGoauldDoor = blocks.lanteaDoor.ref().pushAll(1);
		IDefinitionReference naquadah = items.lanteaOreItem.ref().pushAll(1, OreType.NAQUADAH.ordinal());

		ItemStack blockGold = new ItemStack(net.minecraft.init.Blocks.gold_block, 1);
		ItemStack blockIron = new ItemStack(net.minecraft.init.Blocks.iron_block, 1);
		ItemStack wool = new ItemStack(net.minecraft.init.Blocks.wool, 1);
		ItemStack stick = new ItemStack(net.minecraft.init.Items.stick, 1);
		recipes.decorCrafterRecipe = new SimpleRecipeDefinition("decorator", RecipeType.SHAPED, decorator, "000 1  1 ",
				wool, stick);
		runtime.registries().recipes().addRecipe(recipes.decorCrafterRecipe);

		recipes.decorSetterRecipe = new RecipeProxy("decor_editor", RecipeType.PROXY, DecoratorSetterRecipe.class);
		runtime.registries().recipes().addRecipe(recipes.decorSetterRecipe);

		recipes.decorLanteanSteel = new SimpleRecipeDefinition("lantean_steel", RecipeType.SHAPELESS, decorLantSteel
				.copy().push(0, 16), "01", blockIron, naquadah);
		runtime.registries().recipes().addRecipe(recipes.decorLanteanSteel);

		recipes.decorLanteanPatternSteel = new SimpleRecipeDefinition("lantean_pattern_steel", RecipeType.SHAPED,
				decorLantDecSteel.copy().push(0, 4), "00 00    ", decorLantSteel);
		runtime.registries().recipes().addRecipe(recipes.decorLanteanPatternSteel);

		recipes.decorGoauldGold = new SimpleRecipeDefinition("goauld_gold", RecipeType.SHAPELESS, decorGoaGold.copy()
				.push(0, 16), "01", blockGold, naquadah);
		runtime.registries().recipes().addRecipe(recipes.decorGoauldGold);

		recipes.decorGoauldDecorGold = new SimpleRecipeDefinition("goauld_decor_gold", RecipeType.SHAPED,
				decorGoaPatGold.copy().push(0, 4), "00 00    ", decorGoaGold);
		runtime.registries().recipes().addRecipe(recipes.decorGoauldDecorGold);

		recipes.decorLanteanDoor = new SimpleRecipeDefinition("lantean_door", RecipeType.SHAPED, decorLantDoor,
				" 00 00 11", decorLantDecSteel.copy().push(0, 4), decorLantSteel);
		runtime.registries().recipes().addRecipe(recipes.decorLanteanDoor);

		recipes.decorGoauldDoor = new SimpleRecipeDefinition("goauld_door", RecipeType.SHAPED, decorGoauldDoor,
				" 00 00 11", decorGoaPatGold.copy().push(0, 4), decorGoaGold);
		runtime.registries().recipes().addRecipe(recipes.decorGoauldDoor);
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
