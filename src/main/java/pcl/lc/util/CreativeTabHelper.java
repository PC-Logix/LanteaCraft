package pcl.lc.util;

import net.minecraft.creativetab.CreativeTabs;

/**
 * Creative tab helper
 * 
 * @author AfterLifeLochie
 * 
 */
public abstract class CreativeTabHelper extends CreativeTabs {
	public CreativeTabHelper(int par1, String par2Str) {
		super(par1, par2Str);
	}

	@Override
	public String getTranslatedTabLabel() {
		return getTabLabel();
	}
}
