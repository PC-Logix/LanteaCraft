package pcl.lc.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import pcl.common.base.GenericContainer;
import pcl.common.inventory.FilteredInventory;
import pcl.common.inventory.FilteredSlot;
import pcl.lc.tileentity.TileEntityStargateController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerStargateControllerEnergy extends GenericContainer {

	private TileEntityStargateController te;
	private FilteredInventory filterInventory;
	private double storedEnergy;

	public ContainerStargateControllerEnergy(TileEntityStargateController te, EntityPlayer player) {
		super(177, 108);
		this.te = te;
		filterInventory = (FilteredInventory) te.getInventory();
		addSlotToContainer(new FilteredSlot(filterInventory, 0, 89 - 12 + 4, 4, false));
		addPlayerSlots(player, 8, 47);
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		int energy = (int) Math.floor(100 * 100 * (te.getEnergyStored() / te.getMaxEnergyStored()));
		if (energy > 10000)
			energy = 10000;
		crafter.sendProgressBarUpdate(this, 0, energy);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int i, int value) {
		if (i == 0)
			storedEnergy = value;
	}

	public double getStoredEnergy() {
		return storedEnergy / 100.0d;
	}

}
