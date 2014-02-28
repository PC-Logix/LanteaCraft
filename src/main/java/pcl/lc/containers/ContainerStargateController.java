package pcl.lc.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.world.World;
import pcl.common.base.GenericContainer;
import pcl.lc.tileentity.TileEntityStargateController;

public class ContainerStargateController extends GenericContainer {

	static final int numFuelSlotColumns = 2;
	static final int fuelSlotsX = 174;
	static final int fuelSlotsY = 84;
	static final int playerSlotsX = 48;
	static final int playerSlotsY = 124;

	public TileEntityStargateController te;

	public static ContainerStargateController create(EntityPlayer player, World world, int x, int y, int z) {
		TileEntityStargateController te = (TileEntityStargateController) world.getBlockTileEntity(x, y, z);
		if (te != null)
			return new ContainerStargateController(te, player);
		return null;
	}

	public ContainerStargateController(TileEntityStargateController te, EntityPlayer player) {
		super(256, 208);
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 1, 1));
		//addPlayerSlots(player, playerSlotsX, playerSlotsY);
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		// TODO Auto-generated method stub

	}

}
