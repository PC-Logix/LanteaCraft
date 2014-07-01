package pcl.lc.module.power.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import pcl.lc.base.GenericContainer;
import pcl.lc.base.inventory.FilteredInventory;
import pcl.lc.base.inventory.FilteredSlot;
import pcl.lc.module.power.tile.TileNaquadahGenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerNaquadahGenerator extends GenericContainer {

	private TileNaquadahGenerator te;
	private FilteredInventory filterInventory;

	public ContainerNaquadahGenerator(TileNaquadahGenerator te, EntityPlayer player) {
		super(177, 208);
		this.te = te;
		filterInventory = (FilteredInventory) te.getInventory();
		addSlotToContainer(new FilteredSlot(filterInventory, 0, 8, 94, false));
		addSlotToContainer(new FilteredSlot(filterInventory, 1, 26, 94, false));
		addSlotToContainer(new FilteredSlot(filterInventory, 2, 134, 94, false));
		addSlotToContainer(new FilteredSlot(filterInventory, 3, 152, 94, false));
		addSlotToContainer(new FilteredSlot(filterInventory, 4, 79, 14, false));
		addPlayerSlots(player, 8, 123);
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		int progress = (int) Math.floor(100 * 100 * (te.energy / te.maxEnergy));
		if (progress > 10000)
			progress = 10000;
		crafter.sendProgressBarUpdate(this, 0, progress);

		int volume = (int) Math.floor(100 * 100 * ((double) te.tank.getFluidAmount() / (double) te.tank.getCapacity()));
		if (volume > 10000)
			volume = 10000;
		crafter.sendProgressBarUpdate(this, 1, volume);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int i, int value) {
		switch (i) {
		case 0:
			te.displayEnergy = value;
			break;
		case 1:
			te.displayTankVolume = value;
			break;
		}
	}

}
