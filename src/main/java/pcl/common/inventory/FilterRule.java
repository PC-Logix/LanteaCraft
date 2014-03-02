package pcl.common.inventory;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public class FilterRule {

	private ArrayList<ItemStack> accept = new ArrayList<ItemStack>();
	private ArrayList<ItemStack> deny = new ArrayList<ItemStack>();

	private boolean whitelist;
	private boolean observeMetadata;

	/**
	 * Creates a blank FilterRule.
	 */
	public FilterRule() {
		new FilterRule(null, null, false, true);
	}

	/**
	 * Creates a new FilterRule with the provided Lists of accept and deny rules
	 * 
	 * @param accept
	 *            The list of accept rules
	 * @param deny
	 *            The list of deny rules
	 */
	public FilterRule(ItemStack[] accept, ItemStack[] deny) {
		new FilterRule(accept, deny, false, true);
	}

	/**
	 * Creates a new FilterRule with the provided Lists of accept and deny
	 * rules, and a given mode
	 * 
	 * @param accept
	 *            The list of accept rules
	 * @param deny
	 *            The list of deny rules
	 * @param whitelist
	 *            If this FilterRule is a white-list rule-set
	 * @param metadata
	 *            If this FilterRule is metadata sensitive.
	 */
	public FilterRule(ItemStack[] accept, ItemStack[] deny, boolean whitelist, boolean metadata) {
		if (accept != null)
			for (ItemStack item : accept)
				this.accept.add(new ItemStack(item.getItem(), 1));
		if (deny != null)
			for (ItemStack item : deny)
				this.deny.add(new ItemStack(item.getItem(), 1));
		this.whitelist = whitelist;
		this.observeMetadata = metadata;
	}

	/**
	 * Adds an item to the accept list. This also removes the item from the deny
	 * list.
	 * 
	 * @param item
	 *            The item to accept
	 */
	public void accept(ItemStack item) {
		deny.remove(item);
		accept.add(item);
	}

	/**
	 * Adds an item to the deny list. This also removes the item from the accept
	 * list.
	 * 
	 * @param item
	 *            The item to deny
	 */
	public void deny(ItemStack item) {
		accept.remove(item);
		deny.add(item);
	}

	/**
	 * Removes an item from all accept and deny rules.
	 * 
	 * @param item
	 *            The item to remove
	 */
	public void remove(ItemStack item) {
		accept.remove(item);
		deny.remove(item);
	}

	/**
	 * Sets the FilterRule white-list rule-set mode
	 * 
	 * @param whitelist
	 *            If this FilterRule is a white-list rule-set
	 */
	public void setMode(boolean whitelist) {
		this.whitelist = whitelist;
	}

	/**
	 * Tests an ItemStack of any size to see if the underlying Item is permitted
	 * in this particular rule-set.
	 * 
	 * @param testing
	 *            The ItemStack to test
	 * @return If the ItemStack matches the rule-set
	 */
	public boolean test(ItemStack testing) {
		if (testing == null || testing.getItem() == null)
			return true;
		if (whitelist) {
			for (ItemStack s : accept)
				if (isItemVirtuallyEqual(s, testing))
					return true;
			return false;
		} else {
			for (ItemStack s : deny)
				if (isItemVirtuallyEqual(s, testing))
					return false;
			return true;
		}
	}
	
	private boolean isItemVirtuallyEqual(ItemStack a, ItemStack b) {
		if (a.itemID != b.itemID)
			return false;
		if (observeMetadata && a.getItemDamage() != b.getItemDamage())
			return false;
		return true;
	}
}
