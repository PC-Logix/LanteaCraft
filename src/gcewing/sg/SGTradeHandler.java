//------------------------------------------------------------------------------------------------
//
//   SG Craft - Villager trade handler
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.util.*;

import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.*;
import net.minecraft.item.*;
import net.minecraft.village.*;

import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.registry.VillagerRegistry.*;

public class SGTradeHandler implements IVillageTradeHandler {

	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipes, Random random) {

//		recipes.add(new MerchantRecipe(
//			new ItemStack(Item.emerald, 4),
//			new ItemStack(SGCraft.naquadahIngot, 3),
//			new ItemStack(SGCraft.sgRingBlock, 1, 0)));
//		recipes.add(new MerchantRecipe(
//			new ItemStack(Item.emerald, 4),
//			new ItemStack(Item.enderPearl),
//			new ItemStack(SGCraft.sgRingBlock, 1, 1)));
//		recipes.add(new MerchantRecipe(
//			new ItemStack(Item.emerald, 16),
//			new ItemStack(Item.eyeOfEnder),
//			new ItemStack(SGCraft.sgBaseBlock)));
//		recipes.add(new MerchantRecipe(
//			new ItemStack(Item.emerald, 16),
//			new ItemStack(Block.obsidian, 4),
//			new ItemStack(SGCraft.sgControllerBlock)));
			
		recipes.add(new MerchantRecipe(
			new ItemStack(Item.emerald, 8),
			new ItemStack(Item.diamond, 1),
			new ItemStack(SGCraft.sgCoreCrystal)));

		recipes.add(new MerchantRecipe(
			new ItemStack(Item.emerald, 16),
			new ItemStack(Item.diamond, 1),
			new ItemStack(SGCraft.sgControllerCrystal)));
	}

}
