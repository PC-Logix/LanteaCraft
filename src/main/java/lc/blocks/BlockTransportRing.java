package lc.blocks;

import net.minecraft.block.material.Material;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCBlock;
import lc.common.configuration.xml.ComponentConfig;
import lc.items.ItemBlockTransportRing;
import lc.tiles.TileTransportRing;

/**
 * Transporter ring block implementation
 * @author AfterLifeLochie
 *
 */
@Definition(name = "blockTransportRing", type = ComponentType.STARGATE, blockClass = BlockTransportRing.class, itemBlockClass = ItemBlockTransportRing.class, tileClass = TileTransportRing.class)
public class BlockTransportRing extends LCBlock {

	/** Default constructor */
	public BlockTransportRing() {
		super(Material.ground);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

}
