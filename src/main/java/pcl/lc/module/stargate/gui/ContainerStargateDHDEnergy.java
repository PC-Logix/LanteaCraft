package pcl.lc.module.stargate.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import pcl.lc.base.GenericContainer;
import pcl.lc.base.inventory.FilteredInventory;
import pcl.lc.base.inventory.FilteredSlot;
import pcl.lc.module.stargate.tile.TileStargateDHD;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerStargateDHDEnergy extends GenericContainer {

	private TileStargateDHD te;
	private FilteredInventory filterInventory;
	private double storedEnergy;

	public ContainerStargateDHDEnergy(TileStargateDHD te, EntityPlayer player) {
		super(177, 148);
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
		super.updateProgressBar(i, value);
		if (i == 0)
			storedEnergy = value;
	}

	public double getStoredEnergy() {
		return storedEnergy / 100.0d;
	}

}
