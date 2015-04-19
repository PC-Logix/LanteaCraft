package lc.items;

import lc.common.base.LCItemBlock;
import lc.common.configuration.xml.ComponentConfig;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * Decorative item block implementation.
 *
 * @author AfterLifeLochie
 *
 */
public class ItemBlockDecorative extends LCItemBlock {

	/**
	 * Default constructor.
	 *
	 * @param block
	 *            The parent Block object.
	 */
	public ItemBlockDecorative(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

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
		return "tile.lanteaDecor." + stack.getItemDamage();
	}

}
