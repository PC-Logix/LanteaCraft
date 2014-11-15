package lc.common.base;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Generic item implementation.
 *
 * @author AfterLifeLochie
 *
 */
public class LCItem extends Item {

	@Override
	public boolean isBookEnchantable(ItemStack astack, ItemStack bstack) {
		return false;
	}

}
