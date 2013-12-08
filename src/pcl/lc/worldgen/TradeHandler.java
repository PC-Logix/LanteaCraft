// ------------------------------------------------------------------------------------------------
//
// SG Craft - Villager trade handler
//
// ------------------------------------------------------------------------------------------------

package pcl.lc.worldgen;

import java.util.Random;

import pcl.lc.LanteaCraft.Items;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class TradeHandler implements IVillageTradeHandler {

	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipes, Random random) {
		recipes.add(new MerchantRecipe(new ItemStack(Item.emerald, 8), new ItemStack(Item.diamond, 1), new ItemStack(
				Items.sgCoreCrystal)));

		recipes.add(new MerchantRecipe(new ItemStack(Item.emerald, 16), new ItemStack(Item.diamond, 1), new ItemStack(
				Items.sgControllerCrystal)));
	}

}
