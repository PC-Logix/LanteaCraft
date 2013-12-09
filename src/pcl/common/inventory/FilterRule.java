package pcl.common.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class FilterRule {

	private ArrayList<ItemStack> accept = new ArrayList<ItemStack>();
	private ArrayList<ItemStack> deny = new ArrayList<ItemStack>();

	private boolean whitelist;

	/**
	 * Creates a blank FilterRule.
	 */
	public FilterRule() {
		new FilterRule(null, null, false);
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
		new FilterRule(accept, deny, false);
	}

	/**
	 * Creates a new FilterRule with the provided Lists of accept and deny rules, and a given
	 * mode
	 * 
	 * @param accept
	 *            The list of accept rules
	 * @param deny
	 *            The list of deny rules
	 * @param whitelist
	 *            If this FilterRule is a white-list rule-set
	 */
	public FilterRule(ItemStack[] accept, ItemStack[] deny, boolean whitelist) {
		if (accept != null)
			for (ItemStack item : accept)
				this.accept.add(new ItemStack(item.getItem(), 1));
		if (deny != null)
			for (ItemStack item : deny)
				this.deny.add(new ItemStack(item.getItem(), 1));
		this.whitelist = whitelist;
	}

	/**
	 * Adds an item to the accept list. This also removes the item from the deny list.
	 * 
	 * @param item
	 *            The item to accept
	 */
	public void accept(ItemStack item) {
		this.deny.remove(item);
		this.accept.add(item);
	}

	/**
	 * Adds an item to the deny list. This also removes the item from the accept list.
	 * 
	 * @param item
	 *            The item to deny
	 */
	public void deny(ItemStack item) {
		this.accept.remove(item);
		this.deny.add(item);
	}

	/**
	 * Removes an item from all accept and deny rules.
	 * 
	 * @param item
	 *            The item to remove
	 */
	public void remove(ItemStack item) {
		this.accept.remove(item);
		this.deny.remove(item);
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
	 * Tests an ItemStack of any size to see if the underlying Item is permitted in this
	 * particular rule-set.
	 * 
	 * @param testing
	 *            The ItemStack to test
	 * @return If the ItemStack matches the rule-set
	 */
	public boolean test(ItemStack testing) {
		if (testing == null || testing.getItem() == null)
			return true;
		if (whitelist)
			return this.accept.contains(new ItemStack(testing.getItem(), 1));
		else
			return !this.deny.contains(new ItemStack(testing.getItem(), 1));
	}

}
