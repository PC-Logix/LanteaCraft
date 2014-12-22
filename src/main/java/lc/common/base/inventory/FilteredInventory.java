package lc.common.base.inventory;

import net.minecraft.item.ItemStack;
import lc.common.util.game.SlotFilter;

public abstract class FilteredInventory extends BasicInventory {

	protected SlotFilter[] rules;

	public FilteredInventory(int size) {
		super(size);
		rules = new SlotFilter[size];
	}
	
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

	public SlotFilter getFilterRule(int slotIndex) {
		return rules[slotIndex];
	}
}
