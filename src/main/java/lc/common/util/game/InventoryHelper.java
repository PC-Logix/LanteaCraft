package lc.common.util.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Inventory helper class
 * 
 * @author AfterLifeLochie
 *
 */
public class InventoryHelper {

	/**
	 * Convert an IInventory to a <code>List<ItemStack></code> list of items
	 * 
	 * @param inventory
	 *            The inventory to access.
	 * @return The list of items
	 */
	public static List<ItemStack> allItems(IInventory inventory) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null)
				list.add(stack);
		}
		return list;
	}

	/**
	 * Find a particular ItemStack of an Item in an inventory
	 * 
	 * @param inventory
	 *            The inventory to access.
	 * @param item
	 *            The item type
	 * @return The ItemStack of the item type, or null if no ItemStack of the
	 *         provided Item is found.
	 */
	public static ItemStack findItem(IInventory inventory, Item item) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem().equals(item))
				return stack;
		}
		return null;
	}

	/**
	 * Find all instances of ItemStack of an Item in an inventory
	 * 
	 * @param inventory
	 *            The inventory to access.
	 * @param item
	 *            The item type.
	 * @return The ItemStacks of the item type, or an empty list of no ItemStack
	 *         of the provide Item is found.
	 */
	public static List<ItemStack> findItems(IInventory inventory, Item item) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem().equals(item))
				list.add(stack);
		}
		return list;
	}

	/**
	 * Filter a list of items over a rule (lambda expression)
	 * 
	 * @param in
	 *            The input list
	 * @param rule
	 *            The rule to test
	 * @return The filtered list based on the lambda test
	 */
	public static List<ItemStack> filterItems(List<ItemStack> in, Predicate<ItemStack> rule) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		Iterator<ItemStack> items = in.iterator();
		while (items.hasNext()) {
			ItemStack stack = items.next();
			if (rule.apply(stack))
				list.add(stack);
		}
		return list;
	}

	/**
	 * Convert a list of ItemStack elements to a list of Blocks
	 * 
	 * @param in
	 *            The input list
	 * @return The output block list
	 */
	public static List<Block> toBlocks(List<ItemStack> in) {
		List<Block> list = new ArrayList<Block>();
		Iterator<ItemStack> items = in.iterator();
		while (items.hasNext()) {
			ItemStack stack = items.next();
			Block block = Block.getBlockFromItem(stack.getItem());
			if (block != null)
				list.add(block);
		}
		return list;
	}
}
