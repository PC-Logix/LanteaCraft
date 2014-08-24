package lc.common.base;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LCItem extends Item {

	@Override
	public boolean isBookEnchantable(ItemStack astack, ItemStack bstack) {
		return false;
	}

}
