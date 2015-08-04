package lc.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import lc.common.base.LCItemBlock;
import lc.common.configuration.xml.ComponentConfig;

/**
 * Obelisk item block implementation
 * 
 * @author AfterLifeLochie
 * 
 */
public class ItemBlockObelisk extends LCItemBlock {

	public ItemBlockObelisk(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "tile.blockObelisk." + stack.getItemDamage();
	}

}
