package lc.common.util;

import java.util.HashMap;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * Creative tab helper
 * 
 * @author AfterLifeLochie
 * 
 */
public class CreativeTabHelper {

	public static class CreativeTabImpl extends CreativeTabs {
		private final Item par3Item;

		public CreativeTabImpl(int par1, String par2Str, Item par3Item) {
			super(par1, par2Str);
			this.par3Item = par3Item;
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

	private static HashMap<String, CreativeTabImpl> tabs = new HashMap<String, CreativeTabImpl>();

	public static void registerTab(String name, Item item) {
		tabs.put(name, new CreativeTabImpl(CreativeTabs.getNextID(), name, item));
	}

	public static CreativeTabImpl getTab(String name) {
		return tabs.get(name);
	}
}
