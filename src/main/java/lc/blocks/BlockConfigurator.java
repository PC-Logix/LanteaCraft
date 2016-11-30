package lc.blocks;

import net.minecraft.block.material.Material;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCBlock;
import lc.common.configuration.xml.ComponentConfig;
import lc.items.ItemBlockConfigurator;
import lc.tiles.TileConfigurator;

@Definition(name = "blockConfigurator", type = ComponentType.CORE, blockClass = BlockConfigurator.class, itemBlockClass = ItemBlockConfigurator.class, tileClass = TileConfigurator.class)
public class BlockConfigurator extends LCBlock {

	public BlockConfigurator() {
		super(Material.ground);
		setHardness(3);
		setProvidesInventory(true).setCanRotate(true);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

}
