package lc.common.base;

import lc.common.configuration.IConfigure;
import lc.common.util.game.SlotFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

/**
 * Internal base inventory stub.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCInventory implements ISidedInventory, IConfigure {

	/** The items in the inventory */
	protected ItemStack[] items;
	/** The slot rules in the inventory */
	protected SlotFilter[] rules;

	/**
	 * Create an inventory
	 *
	 * @param size
	 *            The size of the inventory in slots.
	 */
	public LCInventory(int size) {
		items = new ItemStack[size];
		rules = new SlotFilter[size];
	}

	/**
	 * Set a filter rule on a slot number
	 *
	 * @param slot
	 *            The slot number
	 * @param rule
	 *            The filtering rule
	 */
	public void setFilterRule(int slot, SlotFilter rule) {
		rules[slot] = rule;
	}

	/**
	 * Fetch a filter rule for a slot number
	 *
	 * @param slot
	 *            The slot number
	 * @return The filtering rule
	 */
	public SlotFilter getFilterRule(int slot) {
		return rules[slot];
	}

	@Override
	public int getSizeInventory() {
		return items.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return items[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int take) {
		if (items[slot] == null)
			return null;
		if (items[slot].stackSize == 0)
			return null;
		return items[slot].splitStack(Math.min(take, items[slot].stackSize));
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		items[i] = itemstack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (rules[i] != null && !rules[i].test(itemstack))
			return false;
		return true;
	}

	@Override
	public void markDirty() {
		// TODO Auto-generated method stub

	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub

	}

}
