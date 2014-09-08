package lc.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import lc.common.base.LCBlock;
import lc.common.base.LCItemBlock;
import lc.core.ResourceAccess;

public class ItemBlockStargateBase extends LCItemBlock {

	public ItemBlockStargateBase(Block block) {
		super((LCBlock) block);
	}

	@Override
	public IIcon getIconFromDamage(int i) {
		return blockType.getIcon(0, i);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return subItemName(stack.getItemDamage());
	}

	@Override
	protected String getIconString() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", getUnlocalizedName());
	}

	public static String subItemName(int i) {
		return "tile.stargateBase." + i;
	}
}
