package lc.items;

import lc.common.base.LCBlock;
import lc.common.base.LCItemBlock;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 * DHD item block implementation.
 * 
 * @author AfterLifeLochie
 *
 */
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
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return subItemName(stack.getItemDamage());
	}

	private static String subItemName(int i) {
		return "tile.DHD." + i;
	}

}
