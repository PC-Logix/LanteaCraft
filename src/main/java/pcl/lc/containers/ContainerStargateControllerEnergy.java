package pcl.lc.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.world.World;
import pcl.common.base.GenericContainer;
import pcl.lc.tileentity.TileEntityStargateController;

public class ContainerStargateControllerEnergy extends GenericContainer {

	public static ContainerStargateControllerEnergy create(EntityPlayer player, World world, int x, int y, int z) {
		TileEntityStargateController te = (TileEntityStargateController) world.getBlockTileEntity(x, y, z);
		if (te != null)
			return new ContainerStargateControllerEnergy(te, player);
		return null;
	}

	public ContainerStargateControllerEnergy(TileEntityStargateController te, EntityPlayer player) {
		super(256, 208);
		// addPlayerSlots(player, playerSlotsX, playerSlotsY);
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		// TODO Auto-generated method stub

	}

}
