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

	protected double energyStored, energyMax, energyFract;
	protected double tankStored, tankMax, tankFract;
	protected double burnProgress;
	protected int burnTimeout;

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
		crafter.sendProgressBarUpdate(this, 0, (int) Math.floor(100 * te.getStoredEnergy()));
		crafter.sendProgressBarUpdate(this, 1, (int) Math.floor(100 * te.getMaximumStoredEnergy()));
		crafter.sendProgressBarUpdate(this, 2, (int) Math.floor(100 * te.tank.getFluidAmount()));
		crafter.sendProgressBarUpdate(this, 3, (int) Math.floor(100 * te.tank.getCapacity()));
		crafter.sendProgressBarUpdate(this, 4, (int) Math.floor(100 * te.getBurnProgress()));
		crafter.sendProgressBarUpdate(this, 5, te.getBurnTimer());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int i, int value) {
		if (i == 0) {
			energyStored = value / 100.0d;
		} else if (i == 1) {
			energyMax = value / 100.0d;
		} else if (i == 2) {
			tankStored = value / 100.0d;
		} else if (i == 3) {
			tankMax = value / 100.0d;
		} else if (i == 4) {
			burnProgress = value / 100.0d;
		} else if (i == 5) {
			burnTimeout = value;
		}

		if (energyMax > 0.0d)
			energyFract = energyStored / energyMax;
		if (tankMax > 0.0d)
			tankFract = tankStored / tankMax;
	}

}
