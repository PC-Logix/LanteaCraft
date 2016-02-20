package lc.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import lc.common.base.LCItemBlock;
import lc.common.configuration.xml.ComponentConfig;

/**
 * Brazier item block implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class ItemBlockBrazier extends LCItemBlock {

	public ItemBlockBrazier(Block block) {
		super(block);
		setHasSubtypes(true);
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
		return "tile.blockBrazier." + stack.getItemDamage();
	}

}
