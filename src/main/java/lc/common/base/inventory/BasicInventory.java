package lc.common.base.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Simple inventory stub.
 * 
 * @author AfterLifeLochie
 *
 */
public abstract class BasicInventory implements IInventory {

	/** The items in the inventory */
	protected ItemStack[] items;

	/**
	 * Create a simple inventory
	 * 
	 * @param size
	 *            The size of the inventory
	 */
	public BasicInventory(int size) {
		items = new ItemStack[size];
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
		return true;
	}
}
