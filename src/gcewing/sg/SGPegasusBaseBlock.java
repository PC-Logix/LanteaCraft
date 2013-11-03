package gcewing.sg;

import net.minecraft.client.renderer.texture.IconRegister;

public class SGPegasusBaseBlock extends SGBaseBlock {

	public SGPegasusBaseBlock(int id) {
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
