package lc.common.util.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryHelper {

	public static List<ItemStack> allItems(IInventory inventory) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null)
				list.add(stack);
		}
		return list;
	}

	public static ItemStack findItem(IInventory inventory, Item item) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem().equals(item))
				return stack;
		}
		return null;
	}

	public static List<ItemStack> findItems(IInventory inventory, Item item) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem().equals(item))
				list.add(stack);
		}
		return list;
	}

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
