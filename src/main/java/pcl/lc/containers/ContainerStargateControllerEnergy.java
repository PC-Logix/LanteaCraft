package pcl.lc.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import pcl.common.base.GenericContainer;
import pcl.common.inventory.FilteredInventory;
import pcl.common.inventory.FilteredSlot;
import pcl.lc.tileentity.TileEntityStargateController;

public class ContainerStargateControllerEnergy extends GenericContainer {

	private FilteredInventory filterInventory;

	public ContainerStargateControllerEnergy(TileEntityStargateController te, EntityPlayer player) {
		super(177, 108);
		filterInventory = (FilteredInventory) te.getInventory();
		addSlotToContainer(new FilteredSlot(filterInventory, 0, 89 - 12 + 4, 4, false));
		addPlayerSlots(player, 8, 42);
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		// TODO Auto-generated method stub

	}

}
