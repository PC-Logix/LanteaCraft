package lc.common.base.inventory;

import net.minecraft.item.ItemStack;
import lc.common.util.game.SlotFilter;

/**
 * Filtered simple inventory stub.
 * 
 * @author AfterLifeLochie
 *
 */
public abstract class FilteredInventory extends BasicInventory {

	/** The filtering rules on each slot */
	protected SlotFilter[] rules;

	/**
	 * Create a new filtered inventory
	 * 
	 * @param size
	 *            The number of slots
	 */
	public FilteredInventory(int size) {
		super(size);
		rules = new SlotFilter[size];
	}

	/**
	 * Set a filtering rule on a slot
	 * 
	 * @param index
	 *            The slot index
	 * @param filter
	 *            The filtering rule
	 * @return The self object
	 */
	public FilteredInventory setFilterRule(int index, SlotFilter filter) {
		rules[index] = filter;
		return this;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (rules[i] != null && !rules[i].test(itemstack))
			return false;
		return true;
	}

	/**
	 * Get a filtering rule on a slot
	 * 
	 * @param slotIndex
	 *            The slot index
	 * @return The filtering rule, or null if no rule is set
	 */
	public SlotFilter getFilterRule(int slotIndex) {
		return rules[slotIndex];
	}
}
