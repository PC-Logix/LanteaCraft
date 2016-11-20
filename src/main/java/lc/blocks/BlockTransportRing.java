package lc.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCBlock;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import lc.items.ItemBlockTransportRing;
import lc.tiles.TileTransportRing;

/**
 * Transporter ring block implementation
 * 
 * @author AfterLifeLochie
 *
 */
@Definition(name = "blockTransportRing", type = ComponentType.MACHINE, blockClass = BlockTransportRing.class, itemBlockClass = ItemBlockTransportRing.class, tileClass = TileTransportRing.class)
public class BlockTransportRing extends LCBlock {

	IIcon topAndBottomTexture;
	IIcon sideTexture;

	/** Default constructor */
	public BlockTransportRing() {
		super(Material.ground);
		setHardness(3F).setResistance(2000F);
		setOpaque(false).setProvidesInventory(false);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public IIcon getIcon(int side, int data) {
		if (side <= 1)
			return topAndBottomTexture;
		else
			return sideTexture;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		topAndBottomTexture = register.registerIcon(ResourceAccess
				.formatResourceName("${ASSET_KEY}:transport_ring_base_${TEX_QUALITY}"));
		sideTexture = register.registerIcon(ResourceAccess
				.formatResourceName("${ASSET_KEY}:stargate_ring_${TEX_QUALITY}"));
	}

}
