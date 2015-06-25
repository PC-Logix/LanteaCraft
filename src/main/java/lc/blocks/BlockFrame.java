package lc.blocks;

import net.minecraft.block.material.Material;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCBlock;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import lc.items.ItemBlockFrame;
import lc.items.ItemBlockStargateRing;
import lc.tiles.TileFrame;
import lc.tiles.TileStargateRing;

@Definition(name = "frame", type = ComponentType.CORE, blockClass = BlockFrame.class, itemBlockClass = ItemBlockFrame.class, tileClass = TileFrame.class)
public class BlockFrame extends LCBlock {

	public BlockFrame() {
		super(Material.ground);
		setHardness(3F).setResistance(2000F);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getTextureName() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", getUnlocalizedName());
	}

}
