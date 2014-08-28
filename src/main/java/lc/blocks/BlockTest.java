package lc.blocks;

import net.minecraft.block.material.Material;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCBlock;
import lc.items.ItemBlockTest;

@Definition(name = "testBlock", type = ComponentType.CORE, blockClass = BlockTest.class, itemBlockClass = ItemBlockTest.class)
public class BlockTest extends LCBlock {

	public BlockTest() {
		super(Material.ground);
		// TODO Auto-generated constructor stub
	}

}
