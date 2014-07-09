package pcl.lc.module;

import java.util.Set;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import pcl.common.helpers.RegistrationHelper;
import pcl.common.xmlcfg.ConfigHelper;
import pcl.common.xmlcfg.ModuleConfig;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IModule;
import pcl.lc.base.render.WrittenFontRenderer;
import pcl.lc.core.ModuleManager;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.core.block.BlockLanteaOre;
import pcl.lc.module.core.block.BlockOfLanteaOre;
import pcl.lc.module.core.fluid.BlockLiquidNaquadah;
import pcl.lc.module.core.fluid.ItemSpecialBucket;
import pcl.lc.module.core.fluid.LiquidNaquadah;
import pcl.lc.module.core.item.ItemBlockOfLanteaOre;
import pcl.lc.module.core.item.ItemCraftingReagent;
import pcl.lc.module.core.item.ItemDebugTool;
import pcl.lc.module.core.item.ItemJacksonNotebook;
import pcl.lc.module.core.item.ItemLanteaOre;
import pcl.lc.module.core.item.ItemLanteaOreBlock;
import pcl.lc.module.core.item.ItemLanteaOreIngot;
import pcl.lc.module.core.item.ItemTokraSpawnEgg;
import pcl.lc.module.core.render.BlockVoidRenderer;
import pcl.lc.module.core.render.BlockRotationOrientedRenderer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public class ModuleCore implements IModule {

	public static class Blocks {
		public static BlockLanteaOre lanteaOre;
		public static BlockOfLanteaOre lanteaOreAsBlock;
	}

	public static class Items {
		public static ItemCraftingReagent reagentItem;
		public static ItemLanteaOre lanteaOreItem;
		public static ItemLanteaOreIngot lanteaOreIngot;
		public static ItemJacksonNotebook jacksonNotebook;
		public static ItemDebugTool debugger;
		public static ItemTokraSpawnEgg tokraSpawnEgg;
	}

	public static class Fluids {
		public static LiquidNaquadah fluidLiquidNaquadah;
		public static BlockLiquidNaquadah fluidLiquidNaquadahHost;
		public static ItemSpecialBucket fluidLiquidNaquadahBucket;
	}

	public static class Render {
		public static BlockRotationOrientedRenderer blockOrientedRenderer;
		public static BlockVoidRenderer blockVoidRenderer;
		public static WrittenFontRenderer danielFontRenderer;
	}

	@Override
	public Set<Module> getDependencies() {
		return null;
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return null;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {

	}

	@Override
	public void init(FMLInitializationEvent event) {
		ModuleConfig config = ModuleManager.getConfig(this);

		Blocks.lanteaOre = RegistrationHelper.registerBlock(BlockLanteaOre.class, ItemLanteaOreBlock.class,
				"lanteaOreBlock");
		Items.reagentItem = RegistrationHelper.registerItem(ItemCraftingReagent.class, "lanteaCraftingReagent");
		Items.lanteaOreItem = RegistrationHelper.registerItem(ItemLanteaOre.class, "lanteaOreItem");
		Items.lanteaOreIngot = RegistrationHelper.registerItem(ItemLanteaOreIngot.class, "lanteaOreIngot");
		Items.jacksonNotebook = RegistrationHelper.registerItem(ItemJacksonNotebook.class, "jacksonNotebook");
		Blocks.lanteaOreAsBlock = RegistrationHelper.registerBlock(BlockOfLanteaOre.class, ItemBlockOfLanteaOre.class,
				"lanteaOreIngotBlock");

		Items.tokraSpawnEgg = RegistrationHelper.registerItem(ItemTokraSpawnEgg.class, "tokraSpawnEgg");
		Items.debugger = RegistrationHelper.registerItem(ItemDebugTool.class, "lanteadebug");

		RegistrationHelper.newShapelessRecipe(new ItemStack(Items.lanteaOreIngot, 1), new ItemStack(
				Items.lanteaOreItem, 1), net.minecraft.init.Items.iron_ingot);

		RegistrationHelper.newSmeltingRecipe(new ItemStack(Items.lanteaOreItem, 4), new ItemStack(Blocks.lanteaOre, 0),
				0.1f);
		RegistrationHelper.newSmeltingRecipe(new ItemStack(Items.lanteaOreItem, 4, 1), new ItemStack(Blocks.lanteaOre,
				1), 0.1f);
		RegistrationHelper.newSmeltingRecipe(new ItemStack(Items.lanteaOreItem, 4, 2), new ItemStack(Blocks.lanteaOre,
				2), 0.1f);

		RegistrationHelper.newRecipe(new ItemStack(Blocks.lanteaOreAsBlock, 1), "NNN", "NNN", "NNN", 'N',
				new ItemStack(Items.lanteaOreIngot, 1));
		RegistrationHelper.newRecipe(new ItemStack(Items.lanteaOreIngot, 9), "B", 'B', Blocks.lanteaOreAsBlock);

		Fluids.fluidLiquidNaquadah = new LiquidNaquadah();
		FluidRegistry.registerFluid(Fluids.fluidLiquidNaquadah);
		Fluids.fluidLiquidNaquadahHost = RegistrationHelper.registerBlock(BlockLiquidNaquadah.class, ItemBlock.class,
				"blockLiquidNaquadah", false);
		Fluids.fluidLiquidNaquadahBucket = RegistrationHelper.registerSpecialBucket(Fluids.fluidLiquidNaquadahHost,
				"liquidNaquadahBucket", "liquid-naquadah");

		RegistrationHelper.registerOre("oreNaquadah", new ItemStack(Blocks.lanteaOre));
		RegistrationHelper.registerOre("naquadah", new ItemStack(Items.lanteaOreItem));
		RegistrationHelper.registerOre("ingotNaquadahAlloy", new ItemStack(Items.lanteaOreIngot));

		if (ConfigHelper.getOrSetBooleanParam(config, "Crafting", "allowCrafting", "naquadah",
				"Allow crafting of certain hard-to-obtain items. This de-balances the mod.", false))
			RegistrationHelper.newShapelessRecipe(new ItemStack(Items.lanteaOreItem, 1), net.minecraft.init.Items.coal,
					net.minecraft.init.Items.slime_ball, net.minecraft.init.Items.blaze_powder);

		if (event.getSide() == Side.CLIENT) {
			Render.blockOrientedRenderer = new BlockRotationOrientedRenderer();
			RegistrationHelper.registerRenderer(Render.blockOrientedRenderer);

			Render.blockVoidRenderer = new BlockVoidRenderer();
			RegistrationHelper.registerRenderer(Render.blockVoidRenderer);

			Render.danielFontRenderer = new WrittenFontRenderer(
					LanteaCraft.getResource("textures/notebook/daniel.png"),
					LanteaCraft.getResource("textures/notebook/daniel.metrics.xml"));
			Render.danielFontRenderer.buildFont();
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

	}

}
