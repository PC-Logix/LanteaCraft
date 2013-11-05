package gcewing.sg.blocks;

import gcewing.sg.SGCraft;
import net.minecraft.client.renderer.texture.IconRegister;

public class BlockPegasusStargateBase extends BlockStargateBase {

	public BlockPegasusStargateBase(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerIcons(IconRegister reg) {
		topAndBottomTexture = getIcon(reg, "stargateBlock_ATL_" + SGCraft.RenderHD);
		frontTexture = getIcon(reg, "stargateBase_front_ATL_" + SGCraft.RenderHD);
		sideTexture = getIcon(reg, "stargateRing_ATL_" + SGCraft.RenderHD);
	}

}
