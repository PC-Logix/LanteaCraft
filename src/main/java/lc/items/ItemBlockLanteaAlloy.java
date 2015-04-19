package lc.items;

import lc.common.base.LCItemBlock;
import lc.common.configuration.xml.ComponentConfig;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * LanteaCraft global ore as item implementation.
 *
 * @author AfterLifeLochie
 *
 */
public class ItemBlockLanteaAlloy extends LCItemBlock {

	/**
	 * Create a new ItemBlock wrapper around a block type
	 *
	 * @param block
	 *            The block type
	 */
	public ItemBlockLanteaAlloy(Block block) {
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
		return "tile.lanteaAlloy." + stack.getItemDamage();
	}

}
