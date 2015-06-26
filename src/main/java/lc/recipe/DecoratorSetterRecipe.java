package lc.recipe;

import java.util.ArrayList;
import java.util.List;

import lc.LCRuntime;
import lc.common.util.game.BlockHelper;
import lc.common.util.game.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Decorator block-setting recipe handler
 *
 * @author AfterLifeLochie
 *
 */
public class DecoratorSetterRecipe implements IRecipe {

	private final int width = 2, height = 2;
	private final Item decorTool;

	/** Default constructor */
	public DecoratorSetterRecipe() {
		decorTool = LCRuntime.runtime.items().lanteaDecoratorTool.getItem();
	}

	@Override
	public boolean matches(InventoryCrafting crafting, World world) {
		for (int x = 0; x <= 3 - width; x++)
			for (int y = 0; y <= 3 - height; y++)
				if (checkMatch(crafting, x, y, false) || checkMatch(crafting, x, y, true))
					return true;
		return false;
	}

	private boolean checkMatch(InventoryCrafting crafting, int startX, int startY, boolean mirror) {
		ArrayList<ItemStack> shapelessItems = new ArrayList<ItemStack>();
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++) {
				ItemStack stack = crafting.getStackInRowAndColumn(x, y);
				if (stack != null)
					shapelessItems.add(stack);
			}
		return shapelessItems.size() > 0 && checkShapelessItems(crafting, shapelessItems);
	}

	/**
	 * Determine if all items are present in the crafting grid in order to
	 * complete the recipe correctly
	 *
	 * @param crafting
	 *            The crafting grid
	 * @param shapelessItems
	 *            The shapelss item list
	 * @return If all items are present in the crafting grid in order to
	 *         complete the recipe
	 */
	public boolean checkShapelessItems(InventoryCrafting crafting, ArrayList<ItemStack> shapelessItems) {
		boolean flag0 = false, flag2 = false;
		int p = 0;
		for (ItemStack stack : shapelessItems)
			if (stack.getItem().equals(decorTool))
				if (flag0)
					flag2 = true;
				else
					flag0 = true;
			else {
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block != null)
					p++;
			}
		return flag0 && (p == 0 || p == 1) && !flag2;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craft) {
		List<ItemStack> allItems = InventoryHelper.allItems(craft);
		ItemStack decorStack = new ItemStack(decorTool), blockStack = null;
		Block block = null;
		for (ItemStack stack : allItems) {
			Block theBlock = Block.getBlockFromItem(stack.getItem());
			if (theBlock != null && theBlock.isBlockNormalCube()) {
				blockStack = stack;
				block = theBlock;
			}
		}
		if (block != null) {
			String blockName = BlockHelper.saveBlock(block, blockStack.getItemDamage());
			decorStack.stackTagCompound = new NBTTagCompound();
			decorStack.stackTagCompound.setString("block-name", blockName);
		}
		return decorStack;
	}

	@Override
	public int getRecipeSize() {
		return 4;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(decorTool);
	}

}
