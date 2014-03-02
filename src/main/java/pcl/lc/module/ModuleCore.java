package pcl.lc.module;

import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft.Blocks;
import pcl.lc.LanteaCraft.Fluids;
import pcl.lc.LanteaCraft.Items;
import pcl.lc.api.internal.IModule;
import pcl.lc.blocks.BlockOfLanteaOre;
import pcl.lc.blocks.BlockLanteaOre;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.fluids.BlockLiquidNaquadah;
import pcl.lc.fluids.LiquidNaquadah;
import pcl.lc.items.ItemBlockOfLanteaOre;
import pcl.lc.items.ItemDebugTool;
import pcl.lc.items.ItemLanteaOre;
import pcl.lc.items.ItemLanteaOreBlock;
import pcl.lc.items.ItemLanteaOreIngot;
import pcl.lc.items.ItemTokraSpawnEgg;

public class ModuleCore implements IModule {

	@Override
	public Set<Module> getDependencies() {
		return null;
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return null;
	}

	@Override
	public void preInit() {
		Blocks.lanteaOre = RegistrationHelper.registerBlock(BlockLanteaOre.class, ItemLanteaOreBlock.class,
				"lanteaOreBlock");
		Items.lanteaOreItem = RegistrationHelper.registerItem(ItemLanteaOre.class, "lanteaOreItem");
		Items.lanteaOreIngot = RegistrationHelper.registerItem(ItemLanteaOreIngot.class, "lanteaOreIngot");
		Blocks.lanteaOreAsBlock = RegistrationHelper.registerBlock(BlockOfLanteaOre.class, ItemBlockOfLanteaOre.class,
				"lanteaOreIngotBlock");

		Items.tokraSpawnEgg = RegistrationHelper.registerItem(ItemTokraSpawnEgg.class, "tokraSpawnEgg");
		Items.debugger = RegistrationHelper.registerItem(ItemDebugTool.class, "lanteadebug");

		RegistrationHelper.newShapelessRecipe(new ItemStack(Items.lanteaOreIngot, 1), "naquadah", Item.ingotIron);
		RegistrationHelper.newRecipe(new ItemStack(Blocks.lanteaOreAsBlock, 1), "NNN", "NNN", "NNN", 'N',
				"ingotNaquadahAlloy");
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

		RegistrationHelper.newShapelessRecipe(new ItemStack(Items.lanteaOreItem, 1), Item.coal, Item.slimeBall,
				Item.blazePowder);

	}

	@Override
	public void init() {

	}

	@Override
	public void postInit() {

	}

}
