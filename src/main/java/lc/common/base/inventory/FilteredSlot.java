package lc.common.base.inventory;

import lc.common.base.ux.LCTabbedSlot;
import lc.common.util.game.SlotFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Filtered inventory slot implementation
 * 
 * @author AfterLifeLochie
 */
public class FilteredSlot extends LCTabbedSlot {
	private final boolean readonly;
	private final int slotIndex;

	/**
	 * Create a new filtered inventory slot
	 * 
	 * @param host
	 *            The host inventory
	 * @param slotIndex
	 *            The slot index
	 * @param xDisplayPosition
	 *            The x-coordinate to render the slot
	 * @param yDisplayPosition
	 *            The y-coordinate to render the slot
	 * @param readonly
	 *            If the slot is read-only mode
	 */
	public FilteredSlot(IInventory host, int slotIndex, int xDisplayPosition, int yDisplayPosition, boolean readonly) {
		super(host, slotIndex, xDisplayPosition, yDisplayPosition);
		this.readonly = readonly;
		this.slotIndex = slotIndex;
	}

	@Override
	public void putStack(ItemStack stack) {
		if (inventory instanceof FilteredInventory) {
			FilteredInventory fint = (FilteredInventory) inventory;
			SlotFilter rule = fint.getFilterRule(slotIndex);
			if (rule == null || rule.test(stack))
				super.putStack(stack);
		} else
			super.putStack(stack);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		if (inventory instanceof FilteredInventory) {
			FilteredInventory fint = (FilteredInventory) inventory;
			SlotFilter rule = fint.getFilterRule(slotIndex);
			return rule == null || rule.test(par1ItemStack);
		} else
			return super.isItemValid(par1ItemStack);
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return !readonly || super.canTakeStack(par1EntityPlayer);
	}
}
