package lc.blocks;

import net.minecraft.block.material.Material;
import lc.api.defs.Definition;
import lc.common.base.LCBlock;
import lc.items.ItemBlockTest;

@Definition(name = "testBlock", blockClass = BlockTest.class, itemBlockClass = ItemBlockTest.class)
public class BlockTest extends LCBlock {

	protected BlockTest() {
		super(Material.ground);
		// TODO Auto-generated constructor stub
	}

}
