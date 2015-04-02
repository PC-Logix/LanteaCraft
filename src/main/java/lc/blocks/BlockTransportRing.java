package lc.blocks;

import net.minecraft.block.material.Material;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCBlock;
import lc.items.ItemBlockTransportRing;
import lc.tiles.TileTransportRing;

@Definition(name = "blockTransportRing", type = ComponentType.STARGATE, blockClass = BlockTransportRing.class, itemBlockClass = ItemBlockTransportRing.class, tileClass = TileTransportRing.class)
public class BlockTransportRing extends LCBlock {

	public BlockTransportRing() {
		super(Material.ground);
		// TODO Auto-generated constructor stub
	}

}
