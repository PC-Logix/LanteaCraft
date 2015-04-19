package lc.items;

import net.minecraft.block.Block;
import lc.common.base.LCItemBlock;
import lc.common.configuration.xml.ComponentConfig;

public class ItemBlockObelisk extends LCItemBlock {

	public ItemBlockObelisk(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

}
