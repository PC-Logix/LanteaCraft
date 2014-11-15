package lc.common.util;

import java.util.HashMap;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * Creative Tab manager
 *
 * @author AfterLifeLochie
 *
 */
public class LCCreativeTabManager {

	/**
	 * Creative tab stub class
	 *
	 * @author AfterLifeLochie
	 *
	 */
	public static class CreativeTabImpl extends CreativeTabs {
		private Item par3Item;

		/**
		 * Creative tab stub constructor
		 *
		 * @param par1
		 *            The tabID
		 * @param par2Str
		 *            The tab name
		 * @param par3Item
		 *            The item on the tab
		 */
		public CreativeTabImpl(int par1, String par2Str, Item par3Item) {
			super(par1, par2Str);
			this.par3Item = par3Item;
		}

		/**
		 * Update the tab icon
		 *
		 * @param item
		 *            The new item to show on the tab
		 */
		public void setTabIconItem(Item item) {
			par3Item = item;
		}

		@Override
		public String getTranslatedTabLabel() {
			return getTabLabel();
		}

		@Override
		public Item getTabIconItem() {
			if (par3Item == null)
				return Items.potato;
			return par3Item;
		}
	}

	/** Creative tab map */
	private static HashMap<String, CreativeTabImpl> tabs = new HashMap<String, CreativeTabImpl>();

	/**
	 * Create a new Creative tab element with the specified name and item. If
	 * the item is null, a potato will be selected for the item instead.
	 *
	 * @param name
	 *            The Tab name.
	 * @param item
	 *            The item or null.
	 */
	public static void registerTab(String name, Item item) {
		tabs.put(name.toLowerCase(), new CreativeTabImpl(CreativeTabs.getNextID(), name, item));
	}

	/**
	 * Fetch an existing Creative tab element with the specified name.
	 *
	 * @param name
	 *            The name.
	 * @return The Tab element.
	 */
	public static CreativeTabImpl getTab(String name) {
		return tabs.get(name.toLowerCase());
	}
}
