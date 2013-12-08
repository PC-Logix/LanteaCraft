package pcl.lc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.world.World;
import pcl.lc.base.GenericContainer;
import pcl.lc.base.inventory.FilteredInventory;
import pcl.lc.base.inventory.FilteredSlot;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;

public class ContainerNaquadahGenerator extends GenericContainer {

	private TileEntityNaquadahGenerator te;
	private FilteredInventory filterInventory;

	public ContainerNaquadahGenerator(TileEntityNaquadahGenerator te, EntityPlayer player) {
		super(256, 208);
		this.te = te;
		filterInventory = (FilteredInventory) te.getInventory();
		addSlotToContainer(new FilteredSlot(filterInventory, 0, 32, 32, false));
		addPlayerSlots(player, 48, 124);
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		//crafter.sendProgressBarUpdate(this, 0, te.fuelBuffer);
	}

	@Override
	public void updateProgressBar(int i, int value) {
		switch (i) {
			case 0:
				//te.fuelBuffer = value;
				break;
		}
	}

}
