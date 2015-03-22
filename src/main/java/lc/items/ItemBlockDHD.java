package lc.items;

import lc.ResourceAccess;
import lc.common.base.LCBlock;
import lc.common.base.LCItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemBlockDHD extends LCItemBlock {

	/**
	 * Default constructor
	 *
	 * @param block
	 *            The parent block type.
	 */
	public ItemBlockDHD(Block block) {
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

	private static String subItemName(int i) {
		return "tile.DHD." + i;
	}

}
