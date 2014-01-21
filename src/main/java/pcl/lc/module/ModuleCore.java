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
import pcl.lc.blocks.BlockNaquadah;
import pcl.lc.blocks.BlockNaquadahOre;
import pcl.lc.fluids.BlockLiquidNaquadah;
import pcl.lc.fluids.LiquidNaquadah;
import pcl.lc.items.ItemDebugTool;
import pcl.lc.items.ItemNaquadah;
import pcl.lc.items.ItemNaquadahIngot;
import pcl.lc.items.ItemTokraSpawnEgg;
import pcl.lc.module.ModuleManager.Module;

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
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		Blocks.naquadahBlock = RegistrationHelper.registerBlock(BlockNaquadah.class, "naquadahBlock");
		Blocks.naquadahOre = RegistrationHelper.registerBlock(BlockNaquadahOre.class, "naquadahOre");

		Items.naquadah = RegistrationHelper.registerItem(ItemNaquadah.class, "naquadah");
		Items.naquadahIngot = RegistrationHelper.registerItem(ItemNaquadahIngot.class, "naquadahIngot");
		Items.tokraSpawnEgg = RegistrationHelper.registerItem(ItemTokraSpawnEgg.class, "tokraSpawnEgg");
		Items.debugger = RegistrationHelper.registerItem(ItemDebugTool.class, "lanteadebug");

		RegistrationHelper.newShapelessRecipe(new ItemStack(Items.naquadahIngot, 1), "naquadah", Item.ingotIron);
		RegistrationHelper.newRecipe(new ItemStack(Blocks.naquadahBlock, 1), "NNN", "NNN", "NNN", 'N', "ingotNaquadahAlloy");
		RegistrationHelper.newRecipe(new ItemStack(Items.naquadahIngot, 9), "B", 'B', Blocks.naquadahBlock);

		Fluids.fluidLiquidNaquadah = new LiquidNaquadah();
		FluidRegistry.registerFluid(Fluids.fluidLiquidNaquadah);
		Fluids.fluidLiquidNaquadahHost = RegistrationHelper.registerBlock(BlockLiquidNaquadah.class, ItemBlock.class,
				"blockLiquidNaquadah", false);
		Fluids.fluidLiquidNaquadahBucket = RegistrationHelper.registerSpecialBucket(Fluids.fluidLiquidNaquadahHost, "liquidNaquadahBucket",
				"liquid-naquadah");
	}

	@Override
	public void postInit() {
		RegistrationHelper.registerOre("oreNaquadah", new ItemStack(Blocks.naquadahOre));
		RegistrationHelper.registerOre("naquadah", new ItemStack(Items.naquadah));
		RegistrationHelper.registerOre("ingotNaquadahAlloy", new ItemStack(Items.naquadahIngot));

		RegistrationHelper.newShapelessRecipe(new ItemStack(Items.naquadah, 1), Item.coal, Item.slimeBall, Item.blazePowder);
	}

}
