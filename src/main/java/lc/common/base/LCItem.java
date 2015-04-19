package lc.common.base;

import lc.common.configuration.IConfigure;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Generic item implementation.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCItem extends Item implements IConfigure {

	@Override
	public boolean isBookEnchantable(ItemStack astack, ItemStack bstack) {
		return false;
	}

}
