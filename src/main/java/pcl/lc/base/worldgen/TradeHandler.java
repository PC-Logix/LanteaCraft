package pcl.lc.base.worldgen;

import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.ModuleCore;
import pcl.lc.module.core.item.ItemCraftingReagent.ReagentList;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class TradeHandler implements IVillageTradeHandler {

	@SuppressWarnings("unchecked")
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipes, Random random) {
		if (Module.STARGATE.isLoaded()) {
			recipes.add(new MerchantRecipe(new ItemStack(Items.emerald, 8), new ItemStack(Items.diamond, 1),
					new ItemStack(ModuleCore.Items.reagentItem, 1, ReagentList.CORECRYSTAL.ordinal())));

			recipes.add(new MerchantRecipe(new ItemStack(Items.emerald, 16), new ItemStack(Items.diamond, 1),
					new ItemStack(ModuleCore.Items.reagentItem, 1, ReagentList.CONTROLCRYSTAL.ordinal())));
		}
	}
}
