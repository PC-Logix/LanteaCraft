package lc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import lc.common.base.LCContainer;
import lc.tiles.TileStargateBase;

public class ContainerStargate extends LCContainer {

	public ContainerStargate(TileStargateBase tile, EntityPlayer player) {
		super(800, 600);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		// TODO Auto-generated method stub

	}

}
